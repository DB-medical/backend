package db.team12.medical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "진료 기록 요약 응답")
public class MedicalRecordSummaryResponse {

    @Schema(description = "진료 기록 ID", example = "1")
    private final Long recordId;

    @Schema(description = "방문일", example = "2024-08-01")
    private final LocalDate visitDate;

    @Schema(description = "진단 내용", example = "계절성 천식 악화")
    private final String diagnosis;

    @Schema(description = "환자 정보")
    private final PersonSummary patient;

    @Schema(description = "담당 의사 정보")
    private final DoctorSummary doctor;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "인물 요약 정보")
    public static class PersonSummary {

        @Schema(description = "ID", example = "5")
        private final Long id;

        @Schema(description = "이름", example = "김하늘")
        private final String name;

        @Schema(description = "주민번호", example = "900101-1234567")
        private final String ssn;

        @Schema(description = "연락처", example = "010-1234-5678")
        private final String phone;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "의사 요약 정보")
    public static class DoctorSummary {

        @Schema(description = "의사 ID", example = "9")
        private final Long id;

        @Schema(description = "의사명", example = "김하늘")
        private final String name;

        @Schema(description = "소속 병원명", example = "서울 의료원")
        private final String hospitalName;

        @Schema(description = "소속 진료과 명", example = "내과", nullable = true)
        private final String departmentName;
    }
}
