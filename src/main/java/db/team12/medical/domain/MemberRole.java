package db.team12.medical.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    DOCTOR("ROLE_DOCTOR"),
    PHARMACIST("ROLE_PHARMACIST");

    private final String authority;
}
