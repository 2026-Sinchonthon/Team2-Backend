package org.example.team2backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.team2backend.dto.CheckResponse;
import org.example.team2backend.dto.RestaurantDetailResponse;
import org.example.team2backend.dto.RestaurantImageResponse;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "맛집", description = "맛집 조회, 등록, 태그 수정, 완료 처리를 제공합니다.")
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
    @Operation(
            summary = "맛집 목록 조회",
            description = """
                    전체 보기에서는 완료 10개 이상인 맛집을 전체 인기순으로 조회합니다.
                    university를 전달하면 전체 완료 10개 이상 중 해당 학교가 학교별 완료 1위인 맛집만 학교 인기순으로 조회합니다.
                    university 값이 올바르지 않으면 400(BAD_REQUEST)입니다.
                    """
    )
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
    @Operation(
            summary = "맛집 상세 조회",
            description = """
                    맛집 상세 정보와 태그, 전체 완료 수, 학교별 완료 수를 조회합니다.
                    로그인 상태면 현재 사용자의 완료 여부(checked)가 함께 내려갑니다.
                    비로그인이면 checked는 항상 false입니다.
                    맛집이 없으면 404(NOT_FOUND)입니다.
                    """
    )
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
    @Operation(
            summary = "맛집 등록",
            description = """
                    카카오 검색 결과의 식당을 신촌세끼 맛집으로 등록합니다.
                    이미 등록된 맛집이면 기존 맛집을 그대로 사용합니다.
                    등록 신청은 곧 완료 1개로 집계됩니다.
                    로그인이 필요하며 토큰이 없거나 올바르지 않으면 401(UNAUTHORIZED)입니다.
                    사용자를 찾을 수 없으면 404(NOT_FOUND)입니다.
                    """
    )
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
    @Operation(
            summary = "맛집 태그 수정",
            description = """
                    맛집의 태그를 전체 교체합니다.
                    부분 추가가 아니라 요청으로 보낸 태그 목록이 최종 태그가 됩니다.
                    빈 배열을 보내면 태그가 모두 삭제됩니다.
                    맛집이 없으면 404(NOT_FOUND)입니다.
                    잘못된 태그 값이면 400(BAD_REQUEST)입니다.
                    """
    )
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
     * 맛집 사진 등록/교체
     *
     * POST /api/restaurants/{restaurantId}/image (multipart/form-data, key: image)
     *
     * jpg/png/webp만 허용됩니다. 기존 사진이 있으면 교체되고 이전 사진은 S3에서 삭제됩니다.
     * 로그인이 필요합니다.
     */
    @PostMapping("/{restaurantId}/image")
    public ApiResponse<RestaurantImageResponse> updateImage(
            @PathVariable Long restaurantId,
            @RequestParam("image") MultipartFile image
    ) {
        return ApiResponse.success(
                restaurantService.updateImage(restaurantId, image)
        );
    }

    /**
     * 완료 추가
     *
     * POST /api/restaurants/{restaurantId}/checks
     *
     * 로그인이 필요합니다.
     */
    @Operation(
            summary = "완료 추가",
            description = """
                    현재 사용자가 해당 맛집에 완료를 추가합니다.
                    로그인이 필요하며 토큰이 없거나 올바르지 않으면 401(UNAUTHORIZED)입니다.
                    이미 완료를 누른 맛집이면 400(BAD_REQUEST)입니다.
                    맛집 또는 사용자가 없으면 404(NOT_FOUND)입니다.
                    """
    )
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
    @Operation(
            summary = "완료 취소",
            description = """
                    현재 사용자가 해당 맛집의 완료를 취소합니다.
                    로그인이 필요하며 토큰이 없거나 올바르지 않으면 401(UNAUTHORIZED)입니다.
                    완료를 누르지 않은 맛집이면 400(BAD_REQUEST)입니다.
                    맛집 또는 사용자가 없으면 404(NOT_FOUND)입니다.
                    """
    )
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
