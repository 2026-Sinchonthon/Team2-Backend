package org.example.team2backend.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// 인증된 사용자 정보. JWT에서 꺼낸 userId만 담습니다(DB 조회 없음).
public record AuthUser(Long userId) implements UserDetails {

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }
}
