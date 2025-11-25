package db.team12.medical.service;

import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Ingredient;
import db.team12.medical.domain.MedicalRecord;
import db.team12.medical.domain.Medicine;
import db.team12.medical.domain.Patient;
import db.team12.medical.domain.Pharmacist;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.domain.Prescription;
import db.team12.medical.domain.PrescriptionMedicine;
import db.team12.medical.domain.PrescriptionStatus;
import db.team12.medical.dto.MedicalRecordSummaryResponse;
import db.team12.medical.dto.PatientResponse;
import db.team12.medical.dto.PrescriptionDetailResponse;
import db.team12.medical.dto.PrescriptionDispatchRequest;
import db.team12.medical.dto.PrescriptionStatusUpdateRequest;
import db.team12.medical.dto.PrescriptionSummaryResponse;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.PharmacistRepository;
import db.team12.medical.repository.PharmacyRepository;
import db.team12.medical.repository.PrescriptionRepository;
import db.team12.medical.security.MemberPrincipal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrescriptionService {

    private final PharmacistRepository pharmacistRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PharmacyRepository pharmacyRepository;

    public List<PrescriptionSummaryResponse> getPrescriptions(MemberPrincipal principal) {
        Doctor doctor = findDoctor(principal);
        if (doctor != null) {
            return prescriptionRepository
                    .findAllByMedicalRecordDoctorIdOrderByIssueDateDesc(doctor.getId())
                    .stream()
                    .map(this::toSummaryResponse)
                    .toList();
        }

        Pharmacy pharmacy = getPharmacy(principal);
        return prescriptionRepository
                .findAllByPharmacyIdOrderByIssueDateDesc(pharmacy.getId())
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public PrescriptionDetailResponse getPrescription(MemberPrincipal principal, Long prescriptionId) {
        Prescription prescription = prescriptionRepository
                .findDetailById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("처방전을 찾을 수 없습니다."));

        Doctor doctor = findDoctor(principal);
        if (doctor != null) {
            if (!prescription.getMedicalRecord().getDoctor().getId().equals(doctor.getId())) {
                throw new IllegalArgumentException("본인이 작성하지 않은 처방전은 조회할 수 없습니다.");
            }
            return toDetailResponse(prescription);
        }

        Pharmacy pharmacy = getPharmacy(principal);
        ensurePharmacyOwner(prescription, pharmacy);
        return toDetailResponse(prescription);
    }

    @Transactional
    public PrescriptionDetailResponse dispatchPrescription(
            MemberPrincipal principal, Long prescriptionId, PrescriptionDispatchRequest request) {
        Doctor doctor = findDoctor(principal);
        if (doctor == null) {
            throw new IllegalStateException("의사 계정만 처방전을 전송할 수 있습니다.");
        }
        Prescription prescription = prescriptionRepository
                .findDetailById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("처방전을 찾을 수 없습니다."));
        if (!prescription.getMedicalRecord().getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalArgumentException("본인이 작성하지 않은 처방전은 전송할 수 없습니다.");
        }
        Pharmacy pharmacy = pharmacyRepository
                .findById(request.getPharmacyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약국입니다."));
        if (prescription.getStatus() != PrescriptionStatus.CREATED || prescription.getPharmacy() != null) {
            throw new IllegalArgumentException("이미 약국으로 전송된 처방전입니다.");
        }
        if (pharmacy.getHospital() == null
                || doctor.getHospital() == null
                || !pharmacy.getHospital().getId().equals(doctor.getHospital().getId())) {
            throw new IllegalArgumentException("자신의 병원과 연결된 약국만 선택할 수 있습니다.");
        }

        prescription.setPharmacy(pharmacy);
        prescription.setStatus(PrescriptionStatus.RECEIVED);
        return toDetailResponse(prescription);
    }

    @Transactional
    public PrescriptionDetailResponse updateStatus(
            MemberPrincipal principal, Long prescriptionId, PrescriptionStatusUpdateRequest request) {
        Pharmacy pharmacy = getPharmacy(principal);
        Prescription prescription = prescriptionRepository
                .findDetailById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("처방전을 찾을 수 없습니다."));
        ensurePharmacyOwner(prescription, pharmacy);
        validateStatusTransition(prescription.getStatus(), request.getStatus());
        prescription.setStatus(request.getStatus());
        return toDetailResponse(prescription);
    }

    private Doctor findDoctor(MemberPrincipal principal) {
        return doctorRepository.findByMemberId(principal.getId()).orElse(null);
    }

    private Pharmacy getPharmacy(MemberPrincipal principal) {
        Pharmacist pharmacist = pharmacistRepository
                .findByMemberId(principal.getId())
                .orElseThrow(() -> new IllegalStateException("약사 계정만 해당 기능을 사용할 수 있습니다."));
        Pharmacy pharmacy = pharmacist.getPharmacy();
        if (pharmacy == null) {
            throw new IllegalStateException("약국 정보가 연결되지 않은 계정입니다.");
        }
        return pharmacy;
    }

    private void ensurePharmacyOwner(Prescription prescription, Pharmacy pharmacy) {
        if (prescription.getPharmacy() == null
                || !prescription.getPharmacy().getId().equals(pharmacy.getId())) {
            throw new IllegalArgumentException("해당 약국으로 전송된 처방전이 아닙니다.");
        }
    }

    private void validateStatusTransition(PrescriptionStatus current, PrescriptionStatus target) {
        if (target == null) {
            throw new IllegalArgumentException("변경할 상태를 입력해 주세요.");
        }
        if (current == target) {
            return;
        }
        switch (current) {
            case CREATED -> throw new IllegalArgumentException("약국으로 전송되지 않은 처방전은 약사가 변경할 수 없습니다.");
            case RECEIVED -> {
                if (target != PrescriptionStatus.DISPENSING) {
                    throw new IllegalArgumentException("RECEIVED 상태에서는 DISPENSING 으로만 변경할 수 있습니다.");
                }
            }
            case DISPENSING -> {
                if (target != PrescriptionStatus.COMPLETED) {
                    throw new IllegalArgumentException("DISPENSING 상태에서는 COMPLETED 로만 변경할 수 있습니다.");
                }
            }
            case COMPLETED -> throw new IllegalArgumentException("이미 조제가 완료된 처방전입니다.");
        }
    }

    private PrescriptionSummaryResponse toSummaryResponse(Prescription prescription) {
        MedicalRecord record = prescription.getMedicalRecord();
        return PrescriptionSummaryResponse.builder()
                .prescriptionId(prescription.getId())
                .medicalRecordId(record.getId())
                .issueDate(prescription.getIssueDate())
                .status(prescription.getStatus())
                .diagnosis(record.getDiagnosis())
                .patient(toPersonSummary(record.getPatient()))
                .doctor(toDoctorSummary(record.getDoctor()))
                .build();
    }

    private PrescriptionSummaryResponse.PersonSummary toPersonSummary(Patient patient) {
        return PrescriptionSummaryResponse.PersonSummary.builder()
                .id(patient.getId())
                .name(patient.getName())
                .build();
    }

    private PrescriptionSummaryResponse.DoctorSummary toDoctorSummary(Doctor doctor) {
        return PrescriptionSummaryResponse.DoctorSummary.builder()
                .id(doctor.getId())
                .name(doctor.getMember().getName())
                .hospitalName(doctor.getHospital() != null ? doctor.getHospital().getName() : null)
                .departmentName(doctor.getDepartment() != null ? doctor.getDepartment().getName() : null)
                .build();
    }

    private PrescriptionDetailResponse toDetailResponse(Prescription prescription) {
        MedicalRecord record = prescription.getMedicalRecord();
        Pharmacy pharmacy = prescription.getPharmacy();
        List<PrescriptionDetailResponse.PrescribedMedicineResponse> medicines = prescription.getMedicines().stream()
                .sorted(Comparator.comparing(pm -> pm.getMedicine().getId()))
                .map(this::toMedicineResponse)
                .toList();

        return PrescriptionDetailResponse.builder()
                .prescriptionId(prescription.getId())
                .medicalRecordId(record.getId())
                .issueDate(prescription.getIssueDate())
                .status(prescription.getStatus())
                .diagnosis(record.getDiagnosis())
                .pharmacyId(pharmacy != null ? pharmacy.getId() : null)
                .pharmacyName(pharmacy != null ? pharmacy.getName() : null)
                .patient(toPatientResponse(record.getPatient()))
                .doctor(MedicalRecordSummaryResponse.DoctorSummary.builder()
                        .id(record.getDoctor().getId())
                        .name(record.getDoctor().getMember().getName())
                        .hospitalName(record.getDoctor().getHospital() != null
                                ? record.getDoctor().getHospital().getName()
                                : null)
                        .departmentName(record.getDoctor().getDepartment() != null
                                ? record.getDoctor().getDepartment().getName()
                                : null)
                        .build())
                .medicines(medicines)
                .build();
    }

    private PrescriptionDetailResponse.PrescribedMedicineResponse toMedicineResponse(
            PrescriptionMedicine prescriptionMedicine) {
        Medicine medicine = prescriptionMedicine.getMedicine();
        List<PrescriptionDetailResponse.IngredientResponse> ingredients = medicine.getIngredients().stream()
                .sorted(Comparator.comparing(Ingredient::getId))
                .map(ingredient -> PrescriptionDetailResponse.IngredientResponse.builder()
                        .id(ingredient.getId())
                        .name(ingredient.getName())
                        .build())
                .toList();

        return PrescriptionDetailResponse.PrescribedMedicineResponse.builder()
                .medicineId(medicine.getId())
                .name(medicine.getName())
                .manufacturer(medicine.getManufacturer())
                .efficacy(medicine.getEfficacy())
                .dosage(prescriptionMedicine.getDosage())
                .frequency(prescriptionMedicine.getFrequency())
                .days(prescriptionMedicine.getDays())
                .ingredients(ingredients)
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
}
