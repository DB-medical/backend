package db.team12.medical.controller;

import db.team12.medical.dto.MedicineSummaryResponse;
import db.team12.medical.security.MemberPrincipal;
import db.team12.medical.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/medicines")
@Tag(name = "의약품", description = "의약품 검색 API")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    @Operation(summary = "의약품 검색", description = "의약품 이름으로 기존 등록된 의약품을 검색합니다.")
    public ResponseEntity<List<MedicineSummaryResponse>> searchMedicines(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(medicineService.searchMedicines(principal, keyword));
    }
}
