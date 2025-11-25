package db.team12.medical.dto;

import db.team12.medical.domain.PrescriptionStatus;
import db.team12.medical.dto.MedicalRecordSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "처방전 상세 응답")
public class PrescriptionDetailResponse {

    @Schema(description = "처방전 ID", example = "10")
    private final Long prescriptionId;

    @Schema(description = "연계된 진료기록 ID", example = "5")
    private final Long medicalRecordId;

    @Schema(description = "발행일", example = "2024-08-01")
    private final LocalDate issueDate;

    @Schema(description = "처방 상태", example = "RECEIVED")
    private final PrescriptionStatus status;

    @Schema(description = "진단 내용", example = "상기도 감염 의심으로 대증치료 권고")
    private final String diagnosis;

    @Schema(description = "약국 ID", example = "3")
    private final Long pharmacyId;

    @Schema(description = "약국명", example = "다온온누리약국")
    private final String pharmacyName;

    @Schema(description = "환자 정보")
    private final PatientResponse patient;

    @Schema(description = "처방 의사 정보")
    private final MedicalRecordSummaryResponse.DoctorSummary doctor;

    @Schema(description = "처방 약 목록")
    private final List<PrescribedMedicineResponse> medicines;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "처방 약 응답 정보")
    public static class PrescribedMedicineResponse {

        @Schema(description = "의약품 ID", example = "1")
        private final Long medicineId;

        @Schema(description = "의약품명", example = "바이러스퀸정")
        private final String name;

        @Schema(description = "제조사", example = "한빛제약")
        private final String manufacturer;

        @Schema(description = "효능/효과", example = "호흡기 염증 완화")
        private final String efficacy;

        @Schema(description = "1회 투약량", example = "1정")
        private final String dosage;

        @Schema(description = "투약 빈도", example = "하루 2회")
        private final String frequency;

        @Schema(description = "투약 일수", example = "7")
        private final Integer days;

        @Schema(description = "의약품 성분 정보")
        private final List<IngredientResponse> ingredients;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "성분 응답 정보")
    public static class IngredientResponse {

        @Schema(description = "성분 ID", example = "2")
        private final Long id;

        @Schema(description = "성분명", example = "아세트아미노펜")
        private final String name;
    }
}
