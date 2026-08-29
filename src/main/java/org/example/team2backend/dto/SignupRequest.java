package org.example.team2backend.dto;

import org.example.team2backend.entity.School;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @Schema(description = "이름", example = "홍길동")
    @NotBlank
    private String name;

    @Schema(description = "이메일", example = "test@example.com")
    @NotBlank
    @Email
    private String email;

    // 형식 검증(대소문자/숫자/특수문자 조합, 길이)은 프론트에서 하고 백엔드는 존재 여부만 확인합니다.
    @Schema(description = "비밀번호", example = "Password1!")
    @NotBlank
    private String password;

    @Schema(description = "비밀번호 확인. password와 값이 같아야 함", example = "Password1!")
    @NotBlank
    private String passwordConfirm;

    @Schema(description = "학교")
    @NotNull
    private School school;
}
