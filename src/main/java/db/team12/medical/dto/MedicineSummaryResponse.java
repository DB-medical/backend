package db.team12.medical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "의약품 검색 응답")
public class MedicineSummaryResponse {

    @Schema(description = "의약품 ID", example = "12")
    private final Long id;

    @Schema(description = "의약품명", example = "타이레놀정 500mg")
    private final String name;

    @Schema(description = "제조사", example = "존슨앤드존슨")
    private final String manufacturer;

    @Schema(description = "효능/효과", example = "해열·진통")
    private final String efficacy;
}
