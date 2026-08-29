package org.example.team2backend.service;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.dto.MyProfileResponse;
import org.example.team2backend.entity.User;
import org.example.team2backend.exception.NotFoundException;
import org.example.team2backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 마이페이지 상단에 보여줄 내 정보(이름 · 학교)를 조회합니다.
     */
    public MyProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("사용자를 찾을 수 없습니다.")
                );

        return new MyProfileResponse(user);
    }
}
