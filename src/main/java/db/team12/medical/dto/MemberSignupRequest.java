package db.team12.medical.dto;

import db.team12.medical.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "회원 가입 요청 본문",
        example =
                """
                        {
                          "role": "DOCTOR",
                          "email": "doctor.jsh@example.com",
                          "name": "제승현",
                          "password": "password",
                          "passwordConfirm": "password",
                          "doctorProfile": {
                            "hospitalId": 1,
                            "departmentId": 3
                          }
                        }
                        """)
public class MemberSignupRequest {

    @NotNull
    @Schema(description = "가입 역할 (DOCTOR / PHARMACIST)", example = "DOCTOR")
    private MemberRole role;

    @NotBlank
    @Email
    @Schema(description = "로그인 아이디로 사용할 이메일", example = "doctor.jsh@example.com")
    private String email;

    @NotBlank
    @Schema(description = "사용자 이름", example = "제승현")
    private String name;

    @NotBlank
    @Schema(description = "로그인 비밀번호", example = "password")
    private String password;

    @NotBlank
    @Schema(description = "비밀번호 확인 값", example = "password")
    private String passwordConfirm;

    @Valid
    @Schema(
            description = "의사 가입 시 필수 정보 (PHARMACIST 역할일 경우 null 허용)",
            nullable = true)
    private DoctorProfilePayload doctorProfile;

    @Valid
    @Schema(
            description = "약사 가입 시 필수 정보 (DOCTOR 역할일 경우 null 허용)",
            nullable = true)
    private PharmacistProfilePayload pharmacistProfile;

    @Schema(hidden = true, accessMode = Schema.AccessMode.READ_ONLY)
    public boolean isDoctor() {
        return MemberRole.DOCTOR.equals(role);
    }

    @Schema(hidden = true, accessMode = Schema.AccessMode.READ_ONLY)
    public boolean isPharmacist() {
        return MemberRole.PHARMACIST.equals(role);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "의사 프로필 정보")
    public static class DoctorProfilePayload {

        @NotNull
        @Schema(description = "소속 병원 ID", example = "1")
        private Long hospitalId;

        @NotNull
        @Schema(description = "소속 진료과 ID", example = "3")
        private Long departmentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "약사 프로필 정보")
    public static class PharmacistProfilePayload {

        @NotNull
        @Schema(description = "소속 약국 ID", example = "2")
        private Long pharmacyId;
    }
}
