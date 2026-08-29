package org.example.team2backend.dto;

import org.example.team2backend.entity.School;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    // 영문 대소문자, 숫자, 특수문자(.!@#&%)를 혼합하여 8~20자
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.!@#&%])[A-Za-z\\d.!@#&%]{8,20}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자(.!@#&%)를 혼합하여 8~20자로 입력해주세요"
    )
    private String password;

    @NotBlank
    private String passwordConfirm;

    @NotNull
    private School school;
}
