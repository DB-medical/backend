package db.team12.medical.service;

import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.MedicalRecord;
import db.team12.medical.domain.Medicine;
import db.team12.medical.domain.Patient;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.domain.Prescription;
import db.team12.medical.domain.PrescriptionMedicine;
import db.team12.medical.domain.PrescriptionStatus;
import db.team12.medical.domain.Symptom;
import db.team12.medical.domain.Treatment;
import db.team12.medical.dto.MedicalRecordCreateRequest;
import db.team12.medical.dto.MedicalRecordDetailResponse;
import db.team12.medical.dto.MedicalRecordSummaryResponse;
import db.team12.medical.dto.PatientResponse;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.MedicalRecordRepository;
import db.team12.medical.repository.MedicineRepository;
import db.team12.medical.repository.PatientRepository;
import db.team12.medical.repository.PharmacyRepository;
import db.team12.medical.repository.PrescriptionRepository;
import db.team12.medical.repository.SymptomRepository;
import db.team12.medical.repository.TreatmentRepository;
import db.team12.medical.security.MemberPrincipal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    // 의무기록/환자/의사/증상/치료·처방 관련 의존성 주입
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SymptomRepository symptomRepository;
    private final TreatmentRepository treatmentRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    @Transactional
    public MedicalRecordDetailResponse createRecord(
            MemberPrincipal principal, MedicalRecordCreateRequest request) {
        Doctor doctor = getDoctor(principal); // JWT에서 확인된 의사 계정 검증
        Patient patient = resolvePatient(request.getPatient()); // 기존 환자 조회 또는 신규 등록

        MedicalRecord record = MedicalRecord.builder()
                .visitDate(request.getVisitDate())
                .diagnosis(request.getDiagnosis())
                .patient(patient)
                .doctor(doctor)
                .build();
        record.setSymptoms(resolveSymptoms(request.getSymptoms())); // 증상 목록 동적 생성
        record.setTreatments(resolveTreatments(request.getTreatments())); // 치료 목록 동적 생성

        MedicalRecord savedRecord = medicalRecordRepository.save(record);

        if (request.getPrescription() != null) {
            Prescription prescription = createPrescription(savedRecord, request.getPrescription());
            savedRecord.setPrescription(prescription);
        }

        return toDetailResponse(
                medicalRecordRepository
                        .findById(savedRecord.getId())
                        .orElseThrow(() -> new IllegalArgumentException("진료 기록을 찾을 수 없습니다.")));
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientBySsn(MemberPrincipal principal, String ssn) {
        getDoctor(principal); // DOCTOR 권한 확인만 수행
        Patient patient = patientRepository
                .findBySsn(ssn)
                .orElseThrow(() -> new IllegalArgumentException("해당 주민번호의 환자가 없습니다."));
        return toPatientResponse(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatientsByName(MemberPrincipal principal, String name) {
        getDoctor(principal);
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return patientRepository
                .findTop20ByNameContainingIgnoreCaseOrderByNameAsc(name.trim())
                .stream()
                .map(this::toPatientResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordSummaryResponse> getAllRecords(MemberPrincipal principal) {
        getDoctor(principal); // 목록 조회 역시 의사만 허용
        return medicalRecordRepository.findAllByOrderByVisitDateDesc().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicalRecordDetailResponse getRecord(MemberPrincipal principal, Long recordId) {
        getDoctor(principal);
        MedicalRecord record = medicalRecordRepository
                .findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("진료 기록을 찾을 수 없습니다."));
        return toDetailResponse(record);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordSummaryResponse> getRecordsByPatient(MemberPrincipal principal, Long patientId) {
        getDoctor(principal);
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("존재하지 않는 환자입니다.");
        }
        return medicalRecordRepository.findAllByPatientIdOrderByVisitDateDesc(patientId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    private Doctor getDoctor(MemberPrincipal principal) {
        return doctorRepository
                .findByMemberId(principal.getId())
                .orElseThrow(() -> new IllegalStateException("의사 계정만 해당 기능을 사용할 수 있습니다."));
    }

    private Patient resolvePatient(MedicalRecordCreateRequest.PatientPayload payload) {
        if (payload.getId() != null) { // ID 기반 기존 환자 우선 처리
            Patient patient = patientRepository
                    .findById(payload.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));
            updatePatientInfo(patient, payload);
            return patient;
        }

        if (payload.getSsn() != null) { // 주민번호로도 기존 환자 매칭 허용
            Patient patient = patientRepository.findBySsn(payload.getSsn()).orElse(null);
            if (patient != null) {
                updatePatientInfo(patient, payload);
                return patient;
            }
        }

        if (payload.getName() == null || payload.getSsn() == null) {
            throw new IllegalArgumentException("신규 환자는 이름과 주민번호가 필요합니다.");
        }

        Patient patient = Patient.builder()
                .name(payload.getName())
                .ssn(payload.getSsn())
                .address(payload.getAddress())
                .phone(payload.getPhone())
                .history(payload.getHistory())
                .build();
        return patientRepository.save(patient);
    }

    private void updatePatientInfo(Patient patient, MedicalRecordCreateRequest.PatientPayload payload) {
        if (payload.getAddress() != null) {
            patient.setAddress(payload.getAddress());
        }
        if (payload.getPhone() != null) {
            patient.setPhone(payload.getPhone());
        }
        if (payload.getHistory() != null) {
            patient.setHistory(payload.getHistory());
        }
    }

    private Set<Symptom> resolveSymptoms(List<MedicalRecordCreateRequest.SymptomPayload> payloads) {
        Set<Symptom> symptoms = new HashSet<>();
        if (payloads == null || payloads.isEmpty()) {
            return symptoms;
        }
        for (MedicalRecordCreateRequest.SymptomPayload payload : payloads) {
            if (payload.getId() != null) {
                Symptom symptom = symptomRepository
                        .findById(payload.getId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 증상입니다."));
                symptoms.add(symptom);
            } else if (payload.getName() != null) {
                Symptom symptom = Symptom.builder()
                        .name(payload.getName())
                        .bodyPart(payload.getBodyPart())
                        .build();
                symptoms.add(symptomRepository.save(symptom));
            }
        }
        return symptoms;
    }

    private Set<Treatment> resolveTreatments(List<MedicalRecordCreateRequest.TreatmentPayload> payloads) {
        Set<Treatment> treatments = new HashSet<>();
        if (payloads == null || payloads.isEmpty()) {
            return treatments;
        }
        for (MedicalRecordCreateRequest.TreatmentPayload payload : payloads) {
            if (payload.getId() != null) {
                Treatment treatment = treatmentRepository
                        .findById(payload.getId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 치료법입니다."));
                treatments.add(treatment);
            } else if (payload.getName() != null) {
                Treatment treatment = Treatment.builder()
                        .name(payload.getName())
                        .description(payload.getDescription())
                        .build();
                treatments.add(treatmentRepository.save(treatment));
            }
        }
        return treatments;
    }

    private Prescription createPrescription(
            MedicalRecord record, MedicalRecordCreateRequest.PrescriptionPayload payload) {
        if (payload.getMedicines() == null || payload.getMedicines().isEmpty()) {
            throw new IllegalArgumentException("처방 약 정보를 입력해 주세요.");
        }

        LocalDate issueDate = payload.getIssueDate() != null ? payload.getIssueDate() : record.getVisitDate();
        PrescriptionStatus status;
        if (payload.getStatus() != null) {
            status = payload.getStatus();
        } else if (payload.getPharmacyId() != null) {
            status = PrescriptionStatus.RECEIVED;
        } else {
            status = PrescriptionStatus.CREATED;
        }
        Pharmacy pharmacy = payload.getPharmacyId() == null
                ? null
                : pharmacyRepository
                        .findById(payload.getPharmacyId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약국입니다."));

        Prescription prescription = Prescription.builder()
                .issueDate(issueDate)
                .status(status)
                .medicalRecord(record)
                .pharmacy(pharmacy)
                .build();
        prescriptionRepository.save(prescription);

        Set<PrescriptionMedicine> medicines = payload.getMedicines().stream()
                .map(medicinePayload -> buildPrescriptionMedicine(prescription, medicinePayload))
                .collect(Collectors.toSet());

        prescription.setMedicines(medicines);
        return prescription;
    }

    private PrescriptionMedicine buildPrescriptionMedicine(
            Prescription prescription, MedicalRecordCreateRequest.PrescribedMedicinePayload payload) {
        Medicine medicine = resolveMedicine(payload);
        return new PrescriptionMedicine(
                prescription, medicine, payload.getDosage(), payload.getFrequency(), payload.getDays());
    }

    private Medicine resolveMedicine(MedicalRecordCreateRequest.PrescribedMedicinePayload payload) {
        Medicine medicine = null;
        if (payload.getMedicineId() != null) {
            return medicineRepository
                    .findById(payload.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 의약품입니다."));
        }

        if (payload.getName() == null) {
            throw new IllegalArgumentException("신규 의약품은 이름이 필요합니다.");
        }

        medicine = Medicine.builder()
                .name(payload.getName())
                .manufacturer(payload.getManufacturer())
                .efficacy(payload.getEfficacy())
                .build();
        return medicineRepository.save(medicine);
    }

    private MedicalRecordSummaryResponse toSummaryResponse(MedicalRecord record) {
        return MedicalRecordSummaryResponse.builder()
                .recordId(record.getId())
                .visitDate(record.getVisitDate())
                .diagnosis(record.getDiagnosis())
                .patient(toPatientSummary(record.getPatient()))
                .doctor(toDoctorSummary(record.getDoctor()))
                .build();
    }

    private MedicalRecordDetailResponse toDetailResponse(MedicalRecord record) {
        List<MedicalRecordDetailResponse.SymptomResponse> symptoms = record.getSymptoms().stream()
                .map(symptom -> MedicalRecordDetailResponse.SymptomResponse.builder()
                        .id(symptom.getId())
                        .name(symptom.getName())
                        .bodyPart(symptom.getBodyPart())
                        .build())
                .toList();

        List<MedicalRecordDetailResponse.TreatmentResponse> treatments = record.getTreatments().stream()
                .map(treatment -> MedicalRecordDetailResponse.TreatmentResponse.builder()
                        .id(treatment.getId())
                        .name(treatment.getName())
                        .description(treatment.getDescription())
                        .build())
                .toList();

        return MedicalRecordDetailResponse.builder()
                .recordId(record.getId())
                .visitDate(record.getVisitDate())
                .diagnosis(record.getDiagnosis())
                .patient(toPatientResponse(record.getPatient()))
                .doctor(toDoctorSummary(record.getDoctor()))
                .symptoms(symptoms)
                .treatments(treatments)
                .prescription(toPrescriptionResponse(record.getPrescription()))
                .build();
    }

    private MedicalRecordDetailResponse.PrescriptionResponse toPrescriptionResponse(Prescription prescription) {
        if (prescription == null) {
            return null;
        }

        List<MedicalRecordDetailResponse.PrescribedMedicineResponse> medicines = prescription.getMedicines().stream()
                .map(pm -> MedicalRecordDetailResponse.PrescribedMedicineResponse.builder()
                        .medicineId(pm.getMedicine().getId())
                        .name(pm.getMedicine().getName())
                        .manufacturer(pm.getMedicine().getManufacturer())
                        .efficacy(pm.getMedicine().getEfficacy())
                        .dosage(pm.getDosage())
                        .frequency(pm.getFrequency())
                        .days(pm.getDays())
                        .ingredients(pm.getMedicine().getIngredients().stream()
                                .map(ingredient -> MedicalRecordDetailResponse.IngredientResponse.builder()
                                        .id(ingredient.getId())
                                        .name(ingredient.getName())
                                        .build())
                                .toList())
                        .build())
                .toList();

        Pharmacy pharmacy = prescription.getPharmacy();

        return MedicalRecordDetailResponse.PrescriptionResponse.builder()
                .id(prescription.getId())
                .issueDate(prescription.getIssueDate())
                .status(prescription.getStatus())
                .pharmacyId(pharmacy != null ? pharmacy.getId() : null)
                .pharmacyName(pharmacy != null ? pharmacy.getName() : null)
                .medicines(medicines)
                .build();
    }

    private PatientResponse toPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .ssn(patient.getSsn())
                .address(patient.getAddress())
                .phone(patient.getPhone())
                .history(patient.getHistory())
                .build();
    }

    private MedicalRecordSummaryResponse.PersonSummary toPatientSummary(Patient patient) {
        return MedicalRecordSummaryResponse.PersonSummary.builder()
                .id(patient.getId())
                .name(patient.getName())
                .ssn(patient.getSsn())
                .phone(patient.getPhone())
                .build();
    }

    private MedicalRecordSummaryResponse.DoctorSummary toDoctorSummary(Doctor doctor) {
        return MedicalRecordSummaryResponse.DoctorSummary.builder()
                .id(doctor.getId())
                .name(doctor.getMember().getName())
                .hospitalName(doctor.getHospital() != null ? doctor.getHospital().getName() : null)
                .departmentName(
                        doctor.getDepartment() != null ? doctor.getDepartment().getName() : null)
                .build();
    }
}
