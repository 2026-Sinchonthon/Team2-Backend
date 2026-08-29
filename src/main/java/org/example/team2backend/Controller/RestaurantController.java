package org.example.team2backend.controller;

import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.response.ApiResponse;
import org.example.team2backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * 맛집 목록 조회
     *
     * GET /api/restaurants
     *
     * 카테고리 필터:
     * GET /api/restaurants?category=한식
     */
    @GetMapping
    public ApiResponse<List<RestaurantResponse>> getRestaurants(
            @RequestParam(required = false) String category
    ) {
        return ApiResponse.success(
                restaurantService.getRestaurants(category)
        );
    }

    /**
     * 카카오 검색 결과의 식당을
     * 신촌세끼에 등록
     *
     * POST /api/restaurants
     */
    @PostMapping
    public ApiResponse<RestaurantResponse> createRestaurant(
            @RequestBody RestaurantRequest request
    ) {
        return ApiResponse.success(
                restaurantService.createOrGetRestaurant(request)
        );
    }

    /**
     * 좋아요 추가
     *
     * POST /api/restaurants/{restaurantId}/likes?userId=1
     *
     * ※ 로그인/JWT 연결 전 임시 방식
     */
    @PostMapping("/{restaurantId}/likes")
    public ApiResponse<Void> addLike(
            @PathVariable Long restaurantId,
            @RequestParam Long userId
    ) {
        restaurantService.addLike(
                restaurantId,
                userId
        );

        return ApiResponse.noContent();
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping("/{restaurantId}/likes")
    public ApiResponse<Void> removeLike(
            @PathVariable Long restaurantId,
            @RequestParam Long userId
    ) {
        restaurantService.removeLike(
                restaurantId,
                userId
        );

        return ApiResponse.noContent();
    }
}