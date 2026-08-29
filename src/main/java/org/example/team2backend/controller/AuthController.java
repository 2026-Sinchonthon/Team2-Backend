package org.example.team2backend.controller;

import org.example.team2backend.dto.LoginRequest;
import org.example.team2backend.dto.SignupRequest;
import org.example.team2backend.dto.TokenResponse;
import org.example.team2backend.response.ApiResponse;
import org.example.team2backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "회원가입 · 로그인")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일이 이미 가입되어 있으면 409(EMAIL_ALREADY_EXISTS)입니다.")
    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signup(request));
    }

    @Operation(summary = "로그인", description = "이메일이 없거나 비밀번호가 틀려도 동일하게 401(INVALID_CREDENTIALS)을 반환합니다.")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    // refresh token 없이 access token만 쓰는 stateless 구조라 서버가 지울 상태가 없습니다.
    // 프론트에서 들고 있는 토큰을 지우면 그걸로 로그아웃이 끝납니다 — 이 엔드포인트는
    // 프론트 로그아웃 플로우가 항상 호출할 API를 갖도록 형태만 맞춰준 것입니다.
    @Operation(summary = "로그아웃", description = "서버에 별도로 무효화할 상태가 없어 항상 성공합니다. 실제 로그아웃은 프론트에서 토큰을 삭제하면 됩니다.")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.noContent();
    }
}
