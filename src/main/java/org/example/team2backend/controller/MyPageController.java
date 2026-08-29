package org.example.team2backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "마이페이지", description = "현재 사용자의 활동 내역을 조회합니다.")
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final RestaurantService restaurantService;

    /**
     * 내 활동 - 내가 완료/찜한 맛집 목록
     *
     * GET /api/mypage/restaurants/checks
     */
    @Operation(
            summary = "내가 완료한 맛집 목록 조회",
            description = """
                    현재 사용자가 완료한 맛집 목록을 최신 완료순으로 조회합니다.
                    로그인이 필요하며 토큰이 없거나 올바르지 않으면 401(UNAUTHORIZED)입니다.
                    사용자를 찾을 수 없으면 404(NOT_FOUND)입니다.
                    """
    )
    @GetMapping("/restaurants/checks")
    public ApiResponse<List<MyCheckedRestaurantResponse>> getMyCheckedRestaurants(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.getMyCheckedRestaurants(authUser.getUserId())
        );
    }
}
