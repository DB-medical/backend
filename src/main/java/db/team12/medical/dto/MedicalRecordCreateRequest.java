package db.team12.medical.dto;

import db.team12.medical.domain.PrescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
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
        description = "진료 기록 작성 요청 본문",
        example =
                """
                        {
                          "visitDate": "2024-08-01",
                          "diagnosis": "상기도 감염 의심으로 대증 치료",
                          "patient": {
                            "id": 1,
                            "name": "김하늘",
                            "ssn": "980101-2345678",
                            "address": "서울시 성북구 정릉로 11",
                            "phone": "010-1111-2222",
                            "history": "천식으로 입원 이력 있음"
                          },
                          "symptoms": [
                            { "id": 1 },
                            { "name": "콧물", "bodyPart": "호흡기" }
                          ],
                          "treatments": [
                            { "id": 2 },
                            { "name": "수액 요법", "description": "탈수 예방" }
                          ],
                          "prescription": {
                            "issueDate": "2024-08-01",
                            "status": "RECEIVED",
                            "pharmacyId": 1,
                            "medicines": [
                              {
                                "medicineId": 1,
                                "dosage": "1정",
                                "frequency": "하루 2회",
                                "days": 7
                              }
                            ]
                          }
                        }
                        """)
public class MedicalRecordCreateRequest {

    @NotNull
    @Schema(description = "방문 일자", example = "2024-08-01")
    private LocalDate visitDate;

    @NotBlank
    @Schema(description = "진단 내용", example = "상기도 감염 의심으로 대증 치료")
    private String diagnosis;

    @NotNull
    @Valid
    @Schema(description = "환자 기본 정보")
    private PatientPayload patient;

    @Valid
    @Schema(description = "진료 시 기록된 증상 목록", nullable = true)
    private List<SymptomPayload> symptoms;

    @Valid
    @Schema(description = "적용한 치료법 목록", nullable = true)
    private List<TreatmentPayload> treatments;

    @Valid
    @Schema(description = "처방전 정보 (선택)", nullable = true)
    private PrescriptionPayload prescription;

    // 환자 입력 필드 (신규/기존 공통)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "환자 입력 정보")
    public static class PatientPayload {

        @Schema(description = "기존 환자 ID (신규 등록 시 null 허용)", example = "1")
        private Long id;

        @Schema(description = "주민등록번호", example = "980101-2345678")
        private String ssn;

        @Schema(description = "환자 이름", example = "김하늘")
        private String name;

        @Schema(description = "주소", example = "서울시 성북구 정릉로 11")
        private String address;

        @Schema(description = "연락처", example = "010-1111-2222")
        private String phone;

        @Schema(description = "과거력/특이사항", example = "천식으로 입원 이력 있음")
        private String history;
    }

    // 증상 입력 필드
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "증상 입력 정보")
    public static class SymptomPayload {

        @Schema(description = "기존 증상 ID", example = "1")
        private Long id;

        @Schema(description = "증상명", example = "발열")
        private String name;

        @Schema(description = "신체 부위", example = "전신")
        private String bodyPart;
    }

    // 치료 입력 필드
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "치료법 입력 정보")
    public static class TreatmentPayload {

        @Schema(description = "기존 치료법 ID", example = "1")
        private Long id;

        @Schema(description = "치료명", example = "흡입 스테로이드 교육")
        private String name;

        @Schema(description = "설명", example = "흡입기 사용법 교육")
        private String description;
    }

    // 처방전 전체 입력
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "처방전 입력 정보")
    public static class PrescriptionPayload {

        @Schema(description = "처방전 발행일 (미지정 시 방문일 사용)", example = "2024-08-01")
        private LocalDate issueDate;

        @Schema(description = "처방전 상태", example = "RECEIVED")
        private PrescriptionStatus status;

        @Schema(description = "전송 대상 약국 ID", example = "1")
        private Long pharmacyId;

        @Valid
        @Schema(description = "처방 약 목록", nullable = true)
        private List<PrescribedMedicinePayload> medicines;
    }

    // 처방 약 입력 (성분 정보는 기존 마스터에서 자동 매핑)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "처방 약 입력 정보")
    public static class PrescribedMedicinePayload {

        @Schema(description = "기존 의약품 ID", example = "1")
        private Long medicineId;

        @Schema(description = "의약품명 (신규 등록 시 필수)", example = "바이러스퀸정")
        private String name;

        @Schema(description = "제조사", example = "한빛제약")
        private String manufacturer;

        @Schema(description = "효능/효과", example = "호흡기 염증 완화")
        private String efficacy;

        @Schema(description = "1회 투약량", example = "1정")
        private String dosage;

        @Schema(description = "투약 빈도", example = "하루 2회")
        private String frequency;

        @Schema(description = "투약 일수", example = "7")
        private Integer days;
    }
}
