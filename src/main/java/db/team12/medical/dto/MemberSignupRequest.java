package db.team12.medical.dto;

import db.team12.medical.domain.MemberRole;
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
public class MemberSignupRequest {

    @NotNull
    private MemberRole role;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirm;

    @Valid
    private DoctorProfilePayload doctorProfile;

    @Valid
    private PharmacistProfilePayload pharmacistProfile;

    public boolean isDoctor() {
        return MemberRole.DOCTOR.equals(role);
    }

    public boolean isPharmacist() {
        return MemberRole.PHARMACIST.equals(role);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorProfilePayload {

        @NotNull
        private Long hospitalId;

        @NotNull
        private Long departmentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PharmacistProfilePayload {

        @NotNull
        private Long pharmacyId;
    }
}
