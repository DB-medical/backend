package db.team12.medical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "약국 검색 응답")
public class PharmacySearchResponse {

    @Schema(description = "약국 ID", example = "3")
    private final Long id;

    @Schema(description = "약국명", example = "다온온누리약국")
    private final String name;

    @Schema(description = "주소", example = "서울특별시 종로구 율곡로 123")
    private final String address;

    @Schema(description = "연락처", example = "02-123-4567")
    private final String phone;

    @Schema(description = "연계된 병원 ID", example = "1")
    private final Long hospitalId;

    @Schema(description = "연계된 병원명", example = "서울의료원")
    private final String hospitalName;
}
