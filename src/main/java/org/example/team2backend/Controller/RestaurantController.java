package org.example.team2backend.controller;

import jakarta.validation.Valid;
import org.example.team2backend.dto.LikeResponse;
import org.example.team2backend.dto.RestaurantDetailResponse;
import org.example.team2backend.dto.RestaurantListResponse;
import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.dto.RestaurantTagUpdateRequest;
import org.example.team2backend.dto.RestaurantTagUpdateResponse;
import org.example.team2backend.auth.AuthUser;
import org.example.team2backend.response.ApiResponse;
import org.example.team2backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * 맛집 목록 조회
     *
     * 전체 보기 (좋아요 10개 이상, 전체 인기순):
     * GET /api/restaurants
     *
     * 카테고리별 보기:
     * GET /api/restaurants?category=한식
     */
    @GetMapping
    public ApiResponse<RestaurantListResponse> getRestaurants() {
        return ApiResponse.success(
                restaurantService.getRestaurants()
        );
    }

    /**
     * 맛집 상세 조회
     *
     * GET /api/restaurants/{restaurantId}
     *
     * 로그인 상태면 그 사용자의 좋아요 여부(liked)가 함께 내려갑니다.
     * 비로그인이면 liked는 항상 false입니다.
     */
    @GetMapping("/{restaurantId}")
    public ApiResponse<RestaurantDetailResponse> getRestaurantDetail(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long userId = (authUser == null) ? null : authUser.getUserId();

        return ApiResponse.success(
                restaurantService.getRestaurantDetail(restaurantId, userId)
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
            @Valid @RequestBody RestaurantRequest request
    ) {
        return ApiResponse.success(
                restaurantService.createRestaurant(request)
        );
    }

    /**
     * 맛집 태그 수정
     *
     * PATCH /api/restaurants/{restaurantId}
     *
     * 부분 추가가 아니라 전체 교체입니다.
     * 빈 배열을 보내면 태그가 모두 삭제됩니다.
     */
    @PatchMapping("/{restaurantId}")
    public ApiResponse<RestaurantTagUpdateResponse> updateTags(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantTagUpdateRequest request
    ) {
        return ApiResponse.success(
                restaurantService.updateTags(restaurantId, request)
        );
    }

    /**
     * 좋아요 추가
     *
     * POST /api/restaurants/{restaurantId}/likes
     *
     * 로그인이 필요합니다.
     */
    @PostMapping("/{restaurantId}/likes")
    public ApiResponse<LikeResponse> addLike(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.addLike(restaurantId, authUser.getUserId())
        );
    }

    /**
     * 좋아요 취소
     *
     * DELETE /api/restaurants/{restaurantId}/likes
     *
     * 로그인이 필요합니다.
     */
    @DeleteMapping("/{restaurantId}/likes")
    public ApiResponse<LikeResponse> removeLike(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.removeLike(restaurantId, authUser.getUserId())
        );
    }
}
