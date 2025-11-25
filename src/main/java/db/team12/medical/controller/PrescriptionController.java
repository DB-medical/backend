package db.team12.medical.controller;

import db.team12.medical.dto.PrescriptionDetailResponse;
import db.team12.medical.dto.PrescriptionDispatchRequest;
import db.team12.medical.dto.PrescriptionStatusUpdateRequest;
import db.team12.medical.dto.PrescriptionSummaryResponse;
import db.team12.medical.security.MemberPrincipal;
import db.team12.medical.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prescriptions")
@Tag(name = "처방전", description = "약국/약사용 처방전 조회 API")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    @Operation(summary = "약국별 처방전 목록 조회", description = "로그인한 약사의 약국으로 전송된 처방전 목록을 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PrescriptionSummaryResponse.class))))
    public ResponseEntity<List<PrescriptionSummaryResponse>> getPrescriptions(
            @AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(prescriptionService.getPrescriptions(principal));
    }

    @GetMapping("/{prescriptionId}")
    @Operation(summary = "처방전 상세 조회", description = "처방 약 목록과 환자/의사 정보를 포함한 상세 정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PrescriptionDetailResponse.class)))
    public ResponseEntity<PrescriptionDetailResponse> getPrescription(
            @AuthenticationPrincipal MemberPrincipal principal, @PathVariable Long prescriptionId) {
        return ResponseEntity.ok(prescriptionService.getPrescription(principal, prescriptionId));
    }

    @PostMapping("/{prescriptionId}/dispatch")
    @Operation(summary = "처방전을 약국으로 전송", description = "특정 처방전을 선택한 약국으로 전송합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "전송 성공",
            content = @Content(schema = @Schema(implementation = PrescriptionDetailResponse.class)))
    public ResponseEntity<PrescriptionDetailResponse> dispatch(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long prescriptionId,
            @Valid @RequestBody PrescriptionDispatchRequest request) {
        return ResponseEntity.ok(prescriptionService.dispatchPrescription(principal, prescriptionId, request));
    }

    @PatchMapping("/{prescriptionId}/status")
    @Operation(summary = "처방전 조제 상태 갱신", description = "약사가 조제 진행 상태를 갱신합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "상태 변경 성공",
            content = @Content(schema = @Schema(implementation = PrescriptionDetailResponse.class)))
    public ResponseEntity<PrescriptionDetailResponse> updateStatus(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long prescriptionId,
            @Valid @RequestBody PrescriptionStatusUpdateRequest request) {
        return ResponseEntity.ok(prescriptionService.updateStatus(principal, prescriptionId, request));
    }
}
