package db.team12.medical.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrescriptionStatus {
    RECEIVED("접수"),
    DISPENSING("조제 중"),
    COMPLETED("조제 완료");

    private final String value;
}
