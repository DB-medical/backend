package db.team12.medical.service;

import static org.assertj.core.api.Assertions.assertThat;

import db.team12.medical.domain.Department;
import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Hospital;
import db.team12.medical.domain.Member;
import db.team12.medical.domain.MemberRole;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.dto.MedicalRecordCreateRequest;
import db.team12.medical.dto.MedicalRecordDetailResponse;
import db.team12.medical.dto.MedicalRecordSummaryResponse;
import db.team12.medical.dto.PatientResponse;
import db.team12.medical.repository.DepartmentRepository;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.HospitalRepository;
import db.team12.medical.repository.MemberRepository;
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
class MedicalRecordServiceTest {

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

    private MemberPrincipal doctorPrincipal;
    private Long pharmacyId;

    @BeforeEach
    void setUp() {
        // 의사-병원-약국 기본 데이터 생성
        Hospital hospital = hospitalRepository.save(
                Hospital.builder().name("테스트병원").address("서울시 어딘가").phone("02-0000-0000").build());
        Department department =
                departmentRepository.save(Department.builder().name("호흡기내과").hospital(hospital).build());

        Member member = memberRepository.save(Member.builder()
                .username("doctor@test.com")
                .password("password")
                .name("테스트의사")
                .role(MemberRole.DOCTOR)
                .build());
        Doctor doctor = doctorRepository.save(
                Doctor.builder().member(member).hospital(hospital).department(department).build());
        member.setDoctorProfile(doctor);

        Pharmacy pharmacy = pharmacyRepository.save(Pharmacy.builder()
                .name("테스트약국")
                .address("서울특별시 종로구")
                .phone("02-1111-1111")
                .hospital(hospital)
                .build());
        pharmacyId = pharmacy.getId();

        doctorPrincipal = new MemberPrincipal(member);
    }

    @Test
    @DisplayName("처방전을 포함하여 진료 기록을 생성하면 상세 응답을 반환한다")
    void createRecord_withPrescription_success() {
        MedicalRecordCreateRequest request = MedicalRecordCreateRequest.builder()
                .visitDate(LocalDate.now())
                .diagnosis("상기도 감염으로 항히스타민 처방")
                .patient(MedicalRecordCreateRequest.PatientPayload.builder()
                        .name("신규 환자")
                        .ssn("900101-1234567")
                        .address("서울시 동작구")
                        .phone("010-0000-0000")
                        .history("비염 기왕력")
                        .build())
                .symptoms(List.of(MedicalRecordCreateRequest.SymptomPayload.builder()
                        .name("코막힘")
                        .bodyPart("호흡기")
                        .build()))
                .treatments(List.of(MedicalRecordCreateRequest.TreatmentPayload.builder()
                        .name("온습포")
                        .description("온찜질 및 수분 섭취 권고")
                        .build()))
                .prescription(MedicalRecordCreateRequest.PrescriptionPayload.builder()
                        .pharmacyId(pharmacyId)
                        .medicines(List.of(MedicalRecordCreateRequest.PrescribedMedicinePayload.builder()
                                .name("콜드케어정")
                                .dosage("1정")
                                .frequency("하루 3회")
                                .days(5)
                                .build()))
                        .build())
                .build();

        MedicalRecordDetailResponse response = medicalRecordService.createRecord(doctorPrincipal, request);

        assertThat(response.getPatient().getName()).isEqualTo("신규 환자");
        assertThat(response.getSymptoms()).hasSize(1);
        assertThat(response.getTreatments()).hasSize(1);
        assertThat(response.getPrescription()).isNotNull();
        assertThat(response.getPrescription().getMedicines()).hasSize(1);
        assertThat(response.getPrescription().getMedicines().get(0).getIngredients()).isEmpty();
    }

    @Test
    @DisplayName("환자별 진료 기록 조회 시 해당 환자의 기록만 반환한다")
    void getRecordsByPatient_filtersProperly() {
        MedicalRecordDetailResponse created = medicalRecordService.createRecord(
                doctorPrincipal,
                baseRequest("김환자", "900101-1000000", "기침"));

        medicalRecordService.createRecord(
                doctorPrincipal,
                baseRequest("다른 환자", "920202-2000000", "두통"));

        List<MedicalRecordSummaryResponse> records =
                medicalRecordService.getRecordsByPatient(doctorPrincipal, created.getPatient().getId());

        assertThat(records).hasSize(1);
        assertThat(records.get(0).getPatient().getName()).isEqualTo("김환자");
    }

    @Test
    @DisplayName("주민번호로 기존 환자 정보를 불러온다")
    void getPatientBySsn_returnsPatient() {
        MedicalRecordDetailResponse created = medicalRecordService.createRecord(
                doctorPrincipal,
                baseRequest("홍길동", "880303-3333333", "발열"));

        PatientResponse patient =
                medicalRecordService.getPatientBySsn(doctorPrincipal, created.getPatient().getSsn());

        assertThat(patient.getId()).isEqualTo(created.getPatient().getId());
        assertThat(patient.getName()).isEqualTo("홍길동");
    }

    // 반복되는 기본 요청 생성을 위한 헬퍼
    private MedicalRecordCreateRequest baseRequest(String name, String ssn, String symptom) {
        return MedicalRecordCreateRequest.builder()
                .visitDate(LocalDate.now())
                .diagnosis(symptom + " 증세 관찰")
                .patient(MedicalRecordCreateRequest.PatientPayload.builder()
                        .name(name)
                        .ssn(ssn)
                        .address("서울시 서초구")
                        .phone("010-2222-3333")
                        .build())
                .symptoms(List.of(MedicalRecordCreateRequest.SymptomPayload.builder()
                        .name(symptom)
                        .bodyPart("전신")
                        .build()))
                .treatments(List.of(MedicalRecordCreateRequest.TreatmentPayload.builder()
                        .name("휴식 권고")
                        .description("수분 섭취 및 휴식")
                        .build()))
                .build();
    }
}
