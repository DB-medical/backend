package db.team12.medical.service;

import db.team12.medical.domain.Department;
import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Hospital;
import db.team12.medical.domain.Member;
import db.team12.medical.domain.MemberRole;
import db.team12.medical.domain.Pharmacist;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.dto.LoginResponse;
import db.team12.medical.dto.MemberLoginRequest;
import db.team12.medical.dto.MemberSignupRequest;
import db.team12.medical.repository.DepartmentRepository;
import db.team12.medical.repository.HospitalRepository;
import db.team12.medical.repository.MemberRepository;
import db.team12.medical.repository.PharmacyRepository;
import db.team12.medical.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(MemberSignupRequest request) {
        validateSignupRequest(request);
        memberRepository
                .findByUsername(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 가입된 이메일입니다.");
                });

        Member member = Member.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .build();

        if (request.isDoctor()) {
            registerDoctor(member, request);
        } else if (request.isPharmacist()) {
            registerPharmacist(member, request);
        } else {
            throw new IllegalArgumentException("지원하지 않는 역할입니다.");
        }

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(MemberLoginRequest request) {
        Member member = memberRepository
                .findByUsername(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        MemberRole role = member.getRole();
        if (!role.equals(request.getRole())) {
            throw new IllegalArgumentException("요청한 역할과 계정 역할이 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(member);
        return LoginResponse.builder().accessToken(token).role(role).build();
    }

    private void validateSignupRequest(MemberSignupRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        if (request.isDoctor() && request.getDoctorProfile() == null) {
            throw new IllegalArgumentException("의사 프로필 정보가 필요합니다.");
        }
        if (request.isPharmacist() && request.getPharmacistProfile() == null) {
            throw new IllegalArgumentException("약사 프로필 정보가 필요합니다.");
        }
    }

    private void registerDoctor(Member member, MemberSignupRequest request) {
        MemberSignupRequest.DoctorProfilePayload payload = request.getDoctorProfile();
        Hospital hospital = hospitalRepository
                .findById(payload.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 병원입니다."));
        Department department = departmentRepository
                .findById(payload.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진료과입니다."));

        Doctor doctor = Doctor.builder()
                .member(member)
                .hospital(hospital)
                .department(department)
                .build();
        member.setDoctorProfile(doctor);
    }

    private void registerPharmacist(Member member, MemberSignupRequest request) {
        MemberSignupRequest.PharmacistProfilePayload payload = request.getPharmacistProfile();
        Pharmacy pharmacy = pharmacyRepository
                .findById(payload.getPharmacyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약국입니다."));

        Pharmacist pharmacist = Pharmacist.builder().member(member).pharmacy(pharmacy).build();
        member.setPharmacistProfile(pharmacist);
    }
}
