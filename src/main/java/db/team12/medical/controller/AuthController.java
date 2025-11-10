package db.team12.medical.controller;

import db.team12.medical.dto.LoginResponse;
import db.team12.medical.dto.MemberLoginRequest;
import db.team12.medical.dto.MemberSignupRequest;
import db.team12.medical.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원 가입 및 로그인 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "의사 또는 약사 계정을 신규 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "요청 검증 실패",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<Void> signup(@RequestBody @Valid MemberSignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid MemberLoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
