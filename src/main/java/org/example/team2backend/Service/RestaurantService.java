package org.example.team2backend.service;

import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.entity.Like;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.User;
import org.example.team2backend.enums.University;
import org.example.team2backend.repository.LikeRepository;
import org.example.team2backend.repository.RestaurantRepository;
import org.example.team2backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    /**
     * 전체 보기에서 맛집으로 노출되기 위한 최소 좋아요 수.
     */
    private static final long MIN_TOTAL_LIKE_COUNT = 10;

    /**
     * 학교별 보기에서 노출되기 위한 해당 학교 최소 좋아요 수.
     */
    private static final long MIN_UNIVERSITY_LIKE_COUNT = 1;

    private final RestaurantRepository restaurantRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    /**
     * 맛집 목록을 조회합니다.
     *
     * <p>university가 없으면 전체 좋아요 {@value #MIN_TOTAL_LIKE_COUNT}개 이상인 맛집을
     * 전체 좋아요 순으로, 있으면 해당 학교 좋아요가 있는 맛집을 그 학교 좋아요 순으로
     * 반환합니다. 필터와 정렬은 모두 DB에서 처리합니다.
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurants(University university) {

        List<Restaurant> restaurants = (university == null)
                ? restaurantRepository.findPopular(MIN_TOTAL_LIKE_COUNT)
                : restaurantRepository.findPopularBySchool(
                        university.name(),
                        MIN_UNIVERSITY_LIKE_COUNT
                );

        if (restaurants.isEmpty()) {
            return List.of();
        }

        // 맛집마다 카운트 쿼리를 날리지 않도록 좋아요 집계를 한 번에 읽어옵니다.
        List<Long> restaurantIds = restaurants.stream()
                .map(Restaurant::getId)
                .toList();

        Map<Long, Long> totalLikes = loadTotalLikes(restaurantIds);
        Map<Long, Map<String, Long>> schoolLikes = loadSchoolLikes(restaurantIds);

        return restaurants.stream()
                .map(restaurant -> new RestaurantResponse(
                        restaurant,
                        totalLikes.getOrDefault(restaurant.getId(), 0L),
                        schoolLikes.getOrDefault(
                                restaurant.getId(),
                                emptySchoolLikes()
                        )
                ))
                .toList();
    }

    /**
     * 맛집별 전체 좋아요 수. 좋아요가 없는 맛집은 결과에 없으므로 호출부에서 0으로 채웁니다.
     */
    private Map<Long, Long> loadTotalLikes(List<Long> restaurantIds) {

        Map<Long, Long> totalLikes = new HashMap<>();

        for (Object[] row : likeRepository.countTotalByRestaurantIds(restaurantIds)) {
            totalLikes.put((Long) row[0], (Long) row[1]);
        }

        return totalLikes;
    }

    /**
     * 맛집별 · 학교별 좋아요 수. 좋아요가 없는 학교도 0으로 채워 항상 전체 학교를 반환합니다.
     */
    private Map<Long, Map<String, Long>> loadSchoolLikes(List<Long> restaurantIds) {

        Map<Long, Map<String, Long>> schoolLikes = new HashMap<>();

        for (Object[] row : likeRepository.countBySchoolForRestaurantIds(restaurantIds)) {
            Long restaurantId = (Long) row[0];
            String school = (String) row[1];
            Long count = (Long) row[2];

            Map<String, Long> counts = schoolLikes.computeIfAbsent(
                    restaurantId,
                    id -> emptySchoolLikes()
            );

            // User.school은 자유 문자열이라 University에 없는 값이 들어올 수 있습니다.
            // 그런 값은 집계에서 제외해 응답 형태를 학교 Enum으로 고정합니다.
            University.from(school)
                    .ifPresent(university -> counts.put(university.name(), count));
        }

        return schoolLikes;
    }

    private Map<String, Long> emptySchoolLikes() {

        Map<String, Long> counts = new LinkedHashMap<>();

        for (University university : University.values()) {
            counts.put(university.name(), 0L);
        }

        return counts;
    }

    public RestaurantResponse createOrGetRestaurant(
            RestaurantRequest request
    ) {

        Restaurant restaurant =
                restaurantRepository
                        .findByKakaoPlaceId(request.getKakaoPlaceId())
                        .orElseGet(() ->
                                restaurantRepository.save(
                                        new Restaurant(
                                                request.getKakaoPlaceId(),
                                                request.getName(),
                                                request.getAddress(),
                                                request.getLatitude(),
                                                request.getLongitude(),
                                                request.getCategory()
                                        )
                                )
                        );

        return toResponse(restaurant);
    }

    public void addLike(Long restaurantId, Long userId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new IllegalArgumentException("맛집을 찾을 수 없습니다.")
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        if (likeRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
        }

        Like like = new Like(user, restaurant);

        likeRepository.save(like);
    }

    public void removeLike(Long restaurantId, Long userId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new IllegalArgumentException("맛집을 찾을 수 없습니다.")
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Like like = likeRepository
                .findByUserAndRestaurant(user, restaurant)
                .orElseThrow(() ->
                        new IllegalArgumentException("좋아요를 누르지 않았습니다.")
                );

        likeRepository.delete(like);
    }

    /**
     * 맛집 한 건에 대한 응답. 집계 쿼리는 목록과 동일한 것을 재사용해
     * 학교별 좋아요 맵의 형태(항상 전체 학교 포함)를 목록 응답과 맞춥니다.
     */
    private RestaurantResponse toResponse(Restaurant restaurant) {

        List<Long> ids = List.of(restaurant.getId());

        return new RestaurantResponse(
                restaurant,
                loadTotalLikes(ids).getOrDefault(restaurant.getId(), 0L),
                loadSchoolLikes(ids).getOrDefault(
                        restaurant.getId(),
                        emptySchoolLikes()
                )
        );
    }
}