package db.team12.medical.dto;

import db.team12.medical.domain.PrescriptionStatus;
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
@Schema(description = "처방전 조제 상태 갱신 요청")
public class PrescriptionStatusUpdateRequest {

    @NotNull
    @Schema(description = "변경할 상태", example = "DISPENSING")
    private PrescriptionStatus status;
}
