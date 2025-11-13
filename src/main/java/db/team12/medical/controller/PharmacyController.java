package db.team12.medical.controller;

import db.team12.medical.dto.PharmacySearchResponse;
import db.team12.medical.security.MemberPrincipal;
import db.team12.medical.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pharmacies")
@Tag(name = "약국", description = "약국 검색 API")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @GetMapping
    @Operation(summary = "약국 검색", description = "이름 또는 주소 키워드로 약국을 검색합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PharmacySearchResponse.class))))
    public ResponseEntity<List<PharmacySearchResponse>> search(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(pharmacyService.search(keyword, size));
    }
}
