package db.team12.medical.dto;

import db.team12.medical.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private final String accessToken;
    private final MemberRole role;
    private final String name;
    private final DoctorProfile doctorProfile;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DoctorProfile {

        private final Long doctorId;
        private final Long hospitalId;
        private final String hospitalName;
        private final Long departmentId;
        private final String departmentName;
    }
}
