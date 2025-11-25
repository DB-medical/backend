package db.team12.medical.dto;

import db.team12.medical.domain.PrescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "처방전 요약 응답")
public class PrescriptionSummaryResponse {

    @Schema(description = "처방전 ID", example = "10")
    private final Long prescriptionId;

    @Schema(description = "연계된 진료기록 ID", example = "5")
    private final Long medicalRecordId;

    @Schema(description = "발행일", example = "2024-08-01")
    private final LocalDate issueDate;

    @Schema(description = "처방 상태", example = "RECEIVED")
    private final PrescriptionStatus status;

    @Schema(description = "진단 요약", example = "상기도 감염 의심")
    private final String diagnosis;

    @Schema(description = "환자 정보")
    private final PersonSummary patient;

    @Schema(description = "처방 의사 정보")
    private final DoctorSummary doctor;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "요약 인적 정보")
    public static class PersonSummary {

        @Schema(description = "ID", example = "1")
        private final Long id;

        @Schema(description = "이름", example = "김하늘")
        private final String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "의사 요약 정보")
    public static class DoctorSummary {

        @Schema(description = "ID", example = "3")
        private final Long id;

        @Schema(description = "이름", example = "이정민")
        private final String name;

        @Schema(description = "소속 병원명", example = "서울 의료원", nullable = true)
        private final String hospitalName;

        @Schema(description = "소속 진료과명", example = "소아과", nullable = true)
        private final String departmentName;
    }
}
