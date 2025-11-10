package db.team12.medical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "환자 기본 정보 응답")
public class PatientResponse {

    @Schema(description = "환자 ID", example = "1")
    private final Long id;

    @Schema(description = "환자 이름", example = "김하늘")
    private final String name;

    @Schema(description = "주민등록번호", example = "980101-2345678")
    private final String ssn;

    @Schema(description = "주소", example = "서울시 성북구 정릉로 11", nullable = true)
    private final String address;

    @Schema(description = "연락처", example = "010-1111-2222", nullable = true)
    private final String phone;

    @Schema(description = "과거력/특이사항", example = "천식으로 입원 이력 있음", nullable = true)
    private final String history;
}
