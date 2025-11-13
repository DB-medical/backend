package db.team12.medical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "처방전 전송 요청")
public class PrescriptionDispatchRequest {

    @NotNull
    @Schema(description = "전송 대상 약국 ID", example = "3")
    private Long pharmacyId;
}
