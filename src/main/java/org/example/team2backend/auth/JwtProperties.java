package org.example.team2backend.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

// application.yml의 jwt.* 설정값. secret은 dev/prod에서 반드시 환경변수 JWT_SECRET으로 주입.
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenValidity
) {
}
