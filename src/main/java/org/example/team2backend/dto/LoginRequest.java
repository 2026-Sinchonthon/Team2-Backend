package org.example.team2backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "이메일", example = "test@example.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "비밀번호", example = "Password1!")
    @NotBlank
    private String password;
}
