package org.example.team2backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TokenResponse {

    @Schema(description = "액세스 토큰. Authorization: Bearer {accessToken} 헤더로 사용")
    private final String accessToken;

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
