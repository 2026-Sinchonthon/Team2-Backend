package org.example.team2backend.controller;

import jakarta.validation.Valid;
import org.example.team2backend.dto.CheckResponse;
import org.example.team2backend.dto.RestaurantDetailResponse;
import org.example.team2backend.dto.RestaurantListResponse;
import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.dto.RestaurantTagUpdateRequest;
import org.example.team2backend.dto.RestaurantTagUpdateResponse;
import org.example.team2backend.auth.AuthUser;
import org.example.team2backend.entity.School;
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
     * 전체 보기 (완료 10개 이상, 전체 인기순):
     * GET /api/restaurants
     *
     * 학교별 보기 (전체 완료 10개 이상 중 해당 학교가 학교별 완료 1위인 맛집, 학교 인기순):
     * GET /api/restaurants?university=SOGANG
     */
    @GetMapping
    public ApiResponse<RestaurantListResponse> getRestaurants(
            @RequestParam(required = false) String university
    ) {
        School filter = null;

        if (university != null && !university.isBlank()) {
            // 오타를 전체 목록으로 조용히 넘기면 프론트가 원인을 못 찾으므로 400으로 알립니다.
            filter = School.from(university)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "알 수 없는 학교입니다: " + university
                    ));
        }

        return ApiResponse.success(
                restaurantService.getRestaurants(filter)
        );
    }

    /**
     * 맛집 상세 조회
     *
     * GET /api/restaurants/{restaurantId}
     *
     * 로그인 상태면 그 사용자의 완료 여부(checked)가 함께 내려갑니다.
     * 비로그인이면 checked는 항상 false입니다.
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
     *
     * 로그인이 필요합니다. 등록 신청이 곧 완료 1개로 집계됩니다.
     */
    @PostMapping
    public ApiResponse<RestaurantResponse> createRestaurant(
            @Valid @RequestBody RestaurantRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.createOrGetRestaurant(request, authUser.getUserId())
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
     * 완료 추가
     *
     * POST /api/restaurants/{restaurantId}/checks
     *
     * 로그인이 필요합니다.
     */
    @PostMapping("/{restaurantId}/checks")
    public ApiResponse<CheckResponse> addCheck(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.addCheck(restaurantId, authUser.getUserId())
        );
    }

    /**
     * 완료 취소
     *
     * DELETE /api/restaurants/{restaurantId}/checks
     *
     * 로그인이 필요합니다.
     */
    @DeleteMapping("/{restaurantId}/checks")
    public ApiResponse<CheckResponse> removeCheck(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ApiResponse.success(
                restaurantService.removeCheck(restaurantId, authUser.getUserId())
        );
    }
}
