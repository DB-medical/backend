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
}
