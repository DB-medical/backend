package db.team12.medical.dto;

import db.team12.medical.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
        description = "로그인 요청 본문",
        example =
                """
                        {
                          "email": "doctor.jsh@example.com",
                          "password": "password",
                          "role": "DOCTOR"
                        }
                        """)
public class MemberLoginRequest {

    @NotBlank
    @Email
    @Schema(description = "로그인 계정 이메일", example = "doctor.jsh@example.com")
    private String email;

    @NotBlank
    @Schema(description = "로그인 비밀번호", example = "password")
    private String password;

    @NotNull
    @Schema(description = "로그인 역할 (DOCTOR / PHARMACIST)", example = "DOCTOR")
    private MemberRole role;
}
