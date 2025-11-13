package db.team12.medical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import db.team12.medical.domain.Department;
import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Hospital;
import db.team12.medical.domain.Member;
import db.team12.medical.domain.MemberRole;
import db.team12.medical.domain.Pharmacist;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.domain.PrescriptionStatus;
import db.team12.medical.dto.MedicalRecordCreateRequest;
import db.team12.medical.dto.MedicalRecordDetailResponse;
import db.team12.medical.dto.PrescriptionDetailResponse;
import db.team12.medical.dto.PrescriptionDispatchRequest;
import db.team12.medical.dto.PrescriptionStatusUpdateRequest;
import db.team12.medical.dto.PrescriptionSummaryResponse;
import db.team12.medical.repository.DepartmentRepository;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.HospitalRepository;
import db.team12.medical.repository.MemberRepository;
import db.team12.medical.repository.PharmacistRepository;
import db.team12.medical.repository.PharmacyRepository;
import db.team12.medical.security.MemberPrincipal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PrescriptionServiceTest {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private PharmacistRepository pharmacistRepository;

    private MemberPrincipal doctorPrincipal;
    private MemberPrincipal otherDoctorPrincipal;
    private MemberPrincipal pharmacistPrincipal;
    private Long pharmacyId;
    private Long otherPharmacyId;

    @BeforeEach
    void setUp() {
        Hospital hospital = hospitalRepository.save(
                Hospital.builder().name("중앙병원").address("서울시 중구").phone("02-1000-2000").build());
        Department department =
                departmentRepository.save(Department.builder().name("내과").hospital(hospital).build());

        doctorPrincipal = registerDoctor("doctor-pres@test.com", "처방의", hospital, department);
        otherDoctorPrincipal = registerDoctor("doctor2@test.com", "다른의", hospital, department);

        Pharmacy primaryPharmacy = pharmacyRepository.save(
                Pharmacy.builder().name("연계약국").address("서울시 강남구").phone("02-3000-4000").build());
        pharmacyId = primaryPharmacy.getId();
        Pharmacy secondaryPharmacy = pharmacyRepository.save(
                Pharmacy.builder().name("다른약국").address("서울시 서초구").phone("02-5000-6000").build());
        otherPharmacyId = secondaryPharmacy.getId();

        pharmacistPrincipal = registerPharmacist("pharmacist@test.com", "조제약사", primaryPharmacy);
    }

    private MemberPrincipal registerDoctor(
            String email, String name, Hospital hospital, Department department) {
        Member doctorMember = memberRepository.save(
                Member.builder().username(email).password("password").name(name).role(MemberRole.DOCTOR).build());
        Doctor doctor = doctorRepository.save(
                Doctor.builder().member(doctorMember).hospital(hospital).department(department).build());
        doctorMember.setDoctorProfile(doctor);
        return new MemberPrincipal(doctorMember);
    }

    private MemberPrincipal registerPharmacist(String email, String name, Pharmacy pharmacy) {
        Member pharmacistMember = memberRepository.save(
                Member.builder().username(email).password("password").name(name).role(MemberRole.PHARMACIST).build());
        Pharmacist pharmacist =
                pharmacistRepository.save(Pharmacist.builder().member(pharmacistMember).pharmacy(pharmacy).build());
        pharmacistMember.setPharmacistProfile(pharmacist);
        return new MemberPrincipal(pharmacistMember);
    }

    @Test
    @DisplayName("약국에 배정된 처방전만 목록으로 조회한다")
    void getPrescriptions_returnsOnlyAssignedPharmacy() {
        medicalRecordService.createRecord(doctorPrincipal, request("김환자", "901010-1000000", pharmacyId, "콧물약"));
        medicalRecordService.createRecord(
                doctorPrincipal, request("박다른", "920202-2000000", otherPharmacyId, "위장약"));

        List<PrescriptionSummaryResponse> prescriptions =
                prescriptionService.getPrescriptions(pharmacistPrincipal);

        assertThat(prescriptions).hasSize(1);
        assertThat(prescriptions.get(0).getPatient().getName()).isEqualTo("김환자");
        assertThat(prescriptions.get(0).getDiagnosis()).contains("콧물");
    }

    @Test
    @DisplayName("의사는 본인이 작성한 처방전 목록만 조회한다")
    void getPrescriptions_asDoctor() {
        medicalRecordService.createRecord(doctorPrincipal, request("본인환자", "900101-1234567", pharmacyId, "해열제"));
        medicalRecordService.createRecord(
                otherDoctorPrincipal, request("다른환자", "910202-7654321", pharmacyId, "진통제"));

        List<PrescriptionSummaryResponse> myPrescriptions =
                prescriptionService.getPrescriptions(doctorPrincipal);
        List<PrescriptionSummaryResponse> otherPrescriptions =
                prescriptionService.getPrescriptions(otherDoctorPrincipal);

        assertThat(myPrescriptions).hasSize(1);
        assertThat(myPrescriptions.get(0).getPatient().getName()).isEqualTo("본인환자");
        assertThat(otherPrescriptions).hasSize(1);
        assertThat(otherPrescriptions.get(0).getPatient().getName()).isEqualTo("다른환자");
    }

    @Test
    @DisplayName("처방전 상세 조회 시 약품 목록을 포함해 반환한다")
    void getPrescription_returnsDetail() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("이상세", "881212-3000000", pharmacyId, "감기약"));
        Long prescriptionId = created.getPrescription().getId();

        PrescriptionDetailResponse detail =
                prescriptionService.getPrescription(pharmacistPrincipal, prescriptionId);

        assertThat(detail.getPrescriptionId()).isEqualTo(prescriptionId);
        assertThat(detail.getPharmacyId()).isEqualTo(pharmacyId);
        assertThat(detail.getMedicines()).hasSize(1);
        assertThat(detail.getMedicines().get(0).getName()).isEqualTo("감기약");
        assertThat(detail.getPatient().getName()).isEqualTo("이상세");
    }

    @Test
    @DisplayName("의사는 본인이 작성한 처방전 상세를 조회할 수 있다")
    void getPrescription_asDoctor() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("의사환자", "931212-4444444", pharmacyId, "기침약"));

        PrescriptionDetailResponse detail =
                prescriptionService.getPrescription(doctorPrincipal, created.getPrescription().getId());

        assertThat(detail.getPatient().getName()).isEqualTo("의사환자");
        assertThat(detail.getDiagnosis()).contains("기침약");
    }

    @Test
    @DisplayName("다른 약국 처방전 상세 조회 시 예외가 발생한다")
    void getPrescription_invalidPharmacy() {
        MedicalRecordDetailResponse other = medicalRecordService.createRecord(
                doctorPrincipal, request("남환자", "850505-1234567", otherPharmacyId, "소화제"));
        Long prescriptionId = other.getPrescription().getId();

        assertThatThrownBy(() -> prescriptionService.getPrescription(pharmacistPrincipal, prescriptionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 약국");
    }

    @Test
    @DisplayName("의사는 타 의사의 처방전을 열람할 수 없다")
    void getPrescription_forbiddenDoctor() {
        MedicalRecordDetailResponse other = medicalRecordService.createRecord(
                otherDoctorPrincipal, request("타환자", "840909-5555555", pharmacyId, "소염제"));

        assertThatThrownBy(
                        () -> prescriptionService.getPrescription(doctorPrincipal, other.getPrescription().getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인이 작성하지 않은");
    }

    @Test
    @DisplayName("의사는 처방전을 선택한 약국으로 전송할 수 있다")
    void dispatchPrescription_success() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("전송환자", "910101-2222222", null, "진통제"));

        PrescriptionDetailResponse dispatched = prescriptionService.dispatchPrescription(
                doctorPrincipal,
                created.getPrescription().getId(),
                PrescriptionDispatchRequest.builder().pharmacyId(pharmacyId).build());

        assertThat(dispatched.getPharmacyId()).isEqualTo(pharmacyId);
        assertThat(dispatched.getStatus()).isNotNull();
    }

    @Test
    @DisplayName("약사는 처방전을 전송할 수 없다")
    void dispatchPrescription_onlyDoctor() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("약사불가", "930303-1111111", null, "수면제"));

        assertThatThrownBy(() -> prescriptionService.dispatchPrescription(
                        pharmacistPrincipal,
                        created.getPrescription().getId(),
                        PrescriptionDispatchRequest.builder().pharmacyId(pharmacyId).build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("의사 계정만");
    }

    @Test
    @DisplayName("의사는 타 의사의 처방전을 전송할 수 없다")
    void dispatchPrescription_forbiddenDoctor() {
        MedicalRecordDetailResponse other = medicalRecordService.createRecord(
                otherDoctorPrincipal, request("다른의사", "920202-4444444", null, "해열제"));

        assertThatThrownBy(() -> prescriptionService.dispatchPrescription(
                        doctorPrincipal,
                        other.getPrescription().getId(),
                        PrescriptionDispatchRequest.builder().pharmacyId(pharmacyId).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인이 작성하지 않은");
    }

    @Test
    @DisplayName("존재하지 않는 약국으로는 전송할 수 없다")
    void dispatchPrescription_invalidPharmacy() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("없는약국", "910909-9999999", null, "소염진통제"));

        assertThatThrownBy(() -> prescriptionService.dispatchPrescription(
                        doctorPrincipal,
                        created.getPrescription().getId(),
                        PrescriptionDispatchRequest.builder().pharmacyId(9999L).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 약국");
    }

    @Test
    @DisplayName("약사는 순차적으로 조제 상태를 변경할 수 있다")
    void updateStatus_successFlow() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("조제환자", "900505-1111111", pharmacyId, "감기약"));

        PrescriptionDetailResponse dispensing = prescriptionService.updateStatus(
                pharmacistPrincipal,
                created.getPrescription().getId(),
                PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.DISPENSING).build());
        assertThat(dispensing.getStatus()).isEqualTo(PrescriptionStatus.DISPENSING);

        PrescriptionDetailResponse completed = prescriptionService.updateStatus(
                pharmacistPrincipal,
                created.getPrescription().getId(),
                PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.COMPLETED).build());
        assertThat(completed.getStatus()).isEqualTo(PrescriptionStatus.COMPLETED);
    }

    @Test
    @DisplayName("허용되지 않는 상태 전환은 거부된다")
    void updateStatus_invalidTransition() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("잘못전환", "940101-5555555", pharmacyId, "해열제"));

        assertThatThrownBy(() -> prescriptionService.updateStatus(
                        pharmacistPrincipal,
                        created.getPrescription().getId(),
                        PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.COMPLETED).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RECEIVED");
    }

    @Test
    @DisplayName("타 약국 약사는 상태를 변경할 수 없다")
    void updateStatus_wrongPharmacy() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("다른약국환자", "930707-6666666", pharmacyId, "치통약"));
        MemberPrincipal otherPharmacist =
                registerPharmacist("otherpharm@test.com", "다른약사", pharmacyRepository.findById(otherPharmacyId).orElseThrow());

        assertThatThrownBy(() -> prescriptionService.updateStatus(
                        otherPharmacist,
                        created.getPrescription().getId(),
                        PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.DISPENSING).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 약국");
    }

    @Test
    @DisplayName("완료된 처방전은 다시 변경할 수 없다")
    void updateStatus_completedLocked() {
        MedicalRecordDetailResponse created =
                medicalRecordService.createRecord(doctorPrincipal, request("완료환자", "950505-7777777", pharmacyId, "수면제"));

        prescriptionService.updateStatus(
                pharmacistPrincipal,
                created.getPrescription().getId(),
                PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.DISPENSING).build());
        prescriptionService.updateStatus(
                pharmacistPrincipal,
                created.getPrescription().getId(),
                PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.COMPLETED).build());

        assertThatThrownBy(() -> prescriptionService.updateStatus(
                        pharmacistPrincipal,
                        created.getPrescription().getId(),
                        PrescriptionStatusUpdateRequest.builder().status(PrescriptionStatus.DISPENSING).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 조제가 완료된");
    }

    private MedicalRecordCreateRequest request(String name, String ssn, Long pharmacyId, String medicineName) {
        return MedicalRecordCreateRequest.builder()
                .visitDate(LocalDate.now())
                .diagnosis(medicineName + " 처방 필요")
                .patient(MedicalRecordCreateRequest.PatientPayload.builder()
                        .name(name)
                        .ssn(ssn)
                        .address("서울시")
                        .phone("010-0000-0000")
                        .build())
                .prescription(MedicalRecordCreateRequest.PrescriptionPayload.builder()
                        .pharmacyId(pharmacyId)
                        .medicines(List.of(MedicalRecordCreateRequest.PrescribedMedicinePayload.builder()
                                .name(medicineName)
                                .dosage("1정")
                                .frequency("하루 3회")
                                .days(5)
                                .build()))
                        .build())
                .build();
    }
}
