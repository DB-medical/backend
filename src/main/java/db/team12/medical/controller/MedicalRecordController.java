package db.team12.medical.controller;

import db.team12.medical.dto.MedicalRecordCreateRequest;
import db.team12.medical.dto.MedicalRecordDetailResponse;
import db.team12.medical.dto.MedicalRecordSummaryResponse;
import db.team12.medical.dto.PatientResponse;
import db.team12.medical.security.MemberPrincipal;
import db.team12.medical.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medical-records")
@Tag(name = "진료 기록", description = "진료기록 작성 및 조회 API")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @Operation(
            summary = "진료 기록 작성",
            description = "환자 정보 및 증상/치료/처방 입력을 받아 한 번에 의무기록을 생성합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = @Content(schema = @Schema(implementation = MedicalRecordDetailResponse.class))),
        @ApiResponse(
                responseCode = "400",
                description = "검증 실패",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<MedicalRecordDetailResponse> create(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody MedicalRecordCreateRequest request) {
        MedicalRecordDetailResponse response = medicalRecordService.createRecord(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "진료 기록 전체 조회", description = "등록된 모든 진료기록을 최근 방문일 순으로 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = MedicalRecordSummaryResponse.class))))
    public ResponseEntity<List<MedicalRecordSummaryResponse>> getAll(
            @AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(medicalRecordService.getAllRecords(principal));
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "진료 기록 상세 조회", description = "단건 진료기록과 증상/치료/처방 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = MedicalRecordDetailResponse.class)))
    public ResponseEntity<MedicalRecordDetailResponse> getDetail(
            @AuthenticationPrincipal MemberPrincipal principal, @PathVariable Long recordId) {
        return ResponseEntity.ok(medicalRecordService.getRecord(principal, recordId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "환자별 진료 기록 조회", description = "특정 환자의 모든 진료기록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = MedicalRecordSummaryResponse.class))))
    public ResponseEntity<List<MedicalRecordSummaryResponse>> getByPatient(
            @AuthenticationPrincipal MemberPrincipal principal, @PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatient(principal, patientId));
    }

    @GetMapping("/patients/{ssn}")
    @Operation(summary = "환자 기본 정보 조회", description = "주민등록번호로 기존 환자 정보를 불러옵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PatientResponse.class)))
    public ResponseEntity<PatientResponse> getPatientBySsn(
            @AuthenticationPrincipal MemberPrincipal principal, @PathVariable String ssn) {
        return ResponseEntity.ok(medicalRecordService.getPatientBySsn(principal, ssn));
    }

    @GetMapping("/patients")
    @Operation(summary = "환자 이름 검색", description = "이름 키워드로 환자 목록을 검색합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PatientResponse.class))))
    public ResponseEntity<List<PatientResponse>> searchPatientsByName(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(name = "name", required = false, defaultValue = "") String name) {
        return ResponseEntity.ok(medicalRecordService.searchPatientsByName(principal, name));
    }
}
