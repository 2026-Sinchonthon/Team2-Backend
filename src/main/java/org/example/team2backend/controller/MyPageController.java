package org.example.team2backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.auth.AuthUser;
import org.example.team2backend.dto.MyCheckedRestaurantResponse;
import org.example.team2backend.response.ApiResponse;
import org.example.team2backend.service.RestaurantService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final RestaurantService restaurantService;

    /**
     * 내 활동 - 내가 완료/찜한 맛집 목록
     *
     * GET /api/mypage/restaurants/checks
     */
    @GetMapping("/restaurants/checks")
    public ApiResponse<List<MyCheckedRestaurantResponse>> getMyCheckedRestaurants(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.getMyCheckedRestaurants(authUser.getUserId())
        );
    }
}
