package org.example.team2backend.dto;

import org.example.team2backend.entity.School;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @Schema(description = "비밀번호. 영문 대소문자, 숫자, 특수문자(.!@#&%)를 혼합하여 8~20자", example = "Password1!")
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.!@#&%])[A-Za-z\\d.!@#&%]{8,20}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자(.!@#&%)를 혼합하여 8~20자로 입력해주세요"
    )
    private String password;

    @Schema(description = "비밀번호 확인. password와 값이 같아야 함", example = "Password1!")
    @NotBlank
    private String passwordConfirm;

    @Schema(description = "학교")
    @NotNull
    private School school;
}
