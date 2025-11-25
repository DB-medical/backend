package db.team12.medical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import db.team12.medical.domain.Department;
import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Hospital;
import db.team12.medical.domain.Member;
import db.team12.medical.domain.MemberRole;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.dto.PharmacySearchResponse;
import db.team12.medical.repository.DepartmentRepository;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.HospitalRepository;
import db.team12.medical.repository.MemberRepository;
import db.team12.medical.repository.PharmacyRepository;
import db.team12.medical.security.MemberPrincipal;
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
class PharmacyServiceTest {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private MemberPrincipal doctorPrincipal;
    private Hospital doctorHospital;

    @BeforeEach
    void setUp() {
        doctorHospital = hospitalRepository.save(Hospital.builder()
                .name("테스트메디컬센터")
                .address("서울시 테스트로 1")
                .phone("02-111-2222")
                .build());
        Department department = departmentRepository.save(Department.builder()
                .name("테스트진료과")
                .hospital(doctorHospital)
                .build());
        Member member = memberRepository.save(Member.builder()
                .username("test-doctor+" + System.nanoTime() + "@example.com")
                .password("{noop}password")
                .name("테스트의사")
                .role(MemberRole.DOCTOR)
                .build());
        doctorRepository.save(Doctor.builder()
                .member(member)
                .hospital(doctorHospital)
                .department(department)
                .build());
        doctorPrincipal = new MemberPrincipal(member);

        pharmacyRepository.save(Pharmacy.builder()
                .name("한빛온누리약국")
                .address("서울시 성동구 왕십리로")
                .phone("02-100-2000")
                .hospital(doctorHospital)
                .build());
        pharmacyRepository.save(Pharmacy.builder()
                .name("왕십리중앙약국")
                .address("서울시 성동구 무학로")
                .phone("02-300-4000")
                .hospital(doctorHospital)
                .build());
    }

    @Test
    @DisplayName("키워드로 약국을 검색한다")
    void search_returnsMatchedPharmacies() {
        List<PharmacySearchResponse> responses = pharmacyService.search(doctorPrincipal, "왕십리", 5);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).contains("약국");
        assertThat(responses.get(0).getHospitalId()).isEqualTo(doctorHospital.getId());
    }

    @Test
    @DisplayName("검색어 없이 조회할 수 없다")
    void search_requiresKeyword() {
        assertThatThrownBy(() -> pharmacyService.search(doctorPrincipal, " ", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색어");
    }
}
