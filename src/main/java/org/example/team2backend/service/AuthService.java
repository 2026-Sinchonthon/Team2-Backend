package org.example.team2backend.service;

import org.example.team2backend.auth.AuthErrorCode;
import org.example.team2backend.auth.JwtTokenProvider;
import org.example.team2backend.dto.LoginRequest;
import org.example.team2backend.dto.SignupRequest;
import org.example.team2backend.dto.TokenResponse;
import org.example.team2backend.entity.User;
import org.example.team2backend.exception.BusinessException;
import org.example.team2backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.createLocalUser(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getSchool()
        );

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        return new TokenResponse(jwtTokenProvider.createAccessToken(user.getId()));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return new TokenResponse(jwtTokenProvider.createAccessToken(user.getId()));
    }
}
