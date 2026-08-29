package org.example.team2backend.controller;

import org.example.team2backend.dto.RestaurantListResponse;
import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.enums.University;
import org.example.team2backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * 전체 보기 (좋아요 10개 이상, 전체 인기순):
     * GET /api/restaurants
     *
     * 학교별 보기 (해당 학교 좋아요 1개 이상, 학교 인기순):
     * GET /api/restaurants?university=SOGANG
     */
    @GetMapping
    public ResponseEntity<RestaurantListResponse> getRestaurants(
            @RequestParam(required = false) String university
    ) {
        University filter = null;

        if (university != null && !university.isBlank()) {
            // 오타를 전체 목록으로 조용히 넘기면 프론트가 원인을 못 찾으므로 400으로 알립니다.
            filter = University.from(university)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "알 수 없는 학교입니다: " + university
                    ));
        }

        return ResponseEntity.ok(
                restaurantService.getRestaurants(filter)
        );
    }

    /**
     * 카카오 검색 결과의 식당을
     * 신촌세끼에 등록
     *
     * POST /api/restaurants
     */
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @RequestBody RestaurantRequest request
    ) {
        return ResponseEntity.ok(
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
    public ResponseEntity<Void> addLike(
            @PathVariable Long restaurantId,
            @RequestParam Long userId
    ) {
        restaurantService.addLike(
                restaurantId,
                userId
        );

        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping("/{restaurantId}/likes")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long restaurantId,
            @RequestParam Long userId
    ) {
        restaurantService.removeLike(
                restaurantId,
                userId
        );

        return ResponseEntity.ok().build();
    }
}