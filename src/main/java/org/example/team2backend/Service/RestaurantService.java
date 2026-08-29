package org.example.team2backend.service;

import org.example.team2backend.dto.LikeResponse;
import org.example.team2backend.dto.RestaurantDetailResponse;
import org.example.team2backend.dto.RestaurantListItem;
import org.example.team2backend.dto.RestaurantListResponse;
import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.dto.RestaurantTagUpdateRequest;
import org.example.team2backend.dto.RestaurantTagUpdateResponse;
import org.example.team2backend.entity.Like;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.RestaurantTag;
import org.example.team2backend.entity.User;
import org.example.team2backend.enums.University;
import org.example.team2backend.exception.NotFoundException;
import org.example.team2backend.repository.LikeRepository;
import org.example.team2backend.repository.RestaurantRepository;
import org.example.team2backend.repository.RestaurantTagRepository;
import org.example.team2backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    /**
     * 전체 보기에서 맛집으로 노출되기 위한 최소 좋아요 수.
     */
    private static final long MIN_TOTAL_LIKE_COUNT = 10;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTagRepository restaurantTagRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    /**
     * 맛집 목록을 조회합니다.
     *
     * <p>어느 쪽이든 전체 좋아요 {@value #MIN_TOTAL_LIKE_COUNT}개 이상이 전제입니다.
     * university가 없으면 전체 좋아요 순으로, 있으면 그 학교가 학교별 좋아요 1위인
     * 맛집만 해당 학교 좋아요 순으로 반환합니다. 필터와 정렬은 모두 DB에서 처리합니다.
     */
    @Transactional(readOnly = true)
    public RestaurantListResponse getRestaurants(University university) {

        List<Restaurant> restaurants = (university == null)
                ? restaurantRepository.findPopular(MIN_TOTAL_LIKE_COUNT)
                : restaurantRepository.findTopRankedBySchool(
                        university.name(),
                        MIN_TOTAL_LIKE_COUNT
                );

        if (restaurants.isEmpty()) {
            return (university == null)
                    ? RestaurantListResponse.of(List.of())
                    : RestaurantListResponse.ofUniversity(university, List.of());
        }

        // 맛집마다 카운트 쿼리를 날리지 않도록 좋아요 집계를 한 번에 읽어옵니다.
        List<Long> restaurantIds = restaurants.stream()
                .map(Restaurant::getId)
                .toList();

        Map<Long, Long> totalLikes = loadTotalLikes(restaurantIds);

        if (university == null) {
            List<RestaurantListItem> items = restaurants.stream()
                    .map(restaurant -> RestaurantListItem.of(
                            restaurant,
                            totalLikes.getOrDefault(restaurant.getId(), 0L)
                    ))
                    .toList();

            return RestaurantListResponse.of(items);
        }

        Map<Long, Map<String, Long>> schoolLikes = loadSchoolLikes(restaurantIds);

        List<RestaurantListItem> items = restaurants.stream()
                .map(restaurant -> RestaurantListItem.ofUniversity(
                        restaurant,
                        totalLikes.getOrDefault(restaurant.getId(), 0L),
                        schoolLikes
                                .getOrDefault(restaurant.getId(), emptySchoolLikes())
                                .getOrDefault(university.name(), 0L)
                ))
                .toList();

        return RestaurantListResponse.ofUniversity(university, items);
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
                                                request.getLongitude()
                                        )
                                )
                        );

        return toResponse(restaurant);
    }

    /**
     * 맛집 상세를 조회합니다.
     *
     * <p>userId가 null이면(로그인 전) liked는 항상 false입니다.
     * 로그인 연동 시 이 인자를 SecurityContext에서 얻도록 바꾸면 됩니다.
     */
    @Transactional(readOnly = true)
    public RestaurantDetailResponse getRestaurantDetail(
            Long restaurantId,
            Long userId
    ) {
        Restaurant restaurant = findRestaurant(restaurantId);

        List<String> tags = restaurantTagRepository.findByRestaurant(restaurant)
                .stream()
                .map(RestaurantTag::getTagName)
                .toList();

        List<Long> ids = List.of(restaurant.getId());

        boolean liked = userId != null
                && !likeRepository.findLikedRestaurantIds(userId, ids).isEmpty();

        return new RestaurantDetailResponse(
                restaurant,
                tags,
                loadTotalLikes(ids).getOrDefault(restaurant.getId(), 0L),
                loadSchoolLikes(ids).getOrDefault(
                        restaurant.getId(),
                        emptySchoolLikes()
                ),
                liked
        );
    }

    /**
     * 맛집의 태그를 교체합니다. 기존 태그를 모두 지우고 요청받은 태그로 새로 넣습니다.
     *
     * <p>중복 태그는 (restaurant_id, tag_name) UNIQUE 제약에 걸리므로 미리 걸러내고,
     * 앞뒤 공백은 제거합니다. 빈 문자열은 태그로 저장하지 않습니다.
     */
    public RestaurantTagUpdateResponse updateTags(
            Long restaurantId,
            RestaurantTagUpdateRequest request
    ) {
        Restaurant restaurant = findRestaurant(restaurantId);

        List<String> tags = (request.getTags() == null)
                ? List.of()
                : request.getTags().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .distinct()
                        .toList();

        // 기존 태그를 먼저 지우고 새로 넣습니다. 같은 트랜잭션 안에서 delete가
        // insert보다 늦게 나가면 UNIQUE 제약에 걸리므로 flush로 순서를 보장합니다.
        restaurantTagRepository.deleteByRestaurant(restaurant);
        restaurantTagRepository.flush();

        List<RestaurantTag> saved = restaurantTagRepository.saveAll(
                tags.stream()
                        .map(tag -> new RestaurantTag(restaurant, tag))
                        .toList()
        );

        return new RestaurantTagUpdateResponse(
                restaurant.getId(),
                saved.stream().map(RestaurantTag::getTagName).toList()
        );
    }

    public LikeResponse addLike(Long restaurantId, Long userId) {

        Restaurant restaurant = findRestaurant(restaurantId);
        User user = findUser(userId);

        if (likeRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
        }

        likeRepository.save(new Like(user, restaurant));

        return buildLikeResponse(restaurant, true);
    }

    public LikeResponse removeLike(Long restaurantId, Long userId) {

        Restaurant restaurant = findRestaurant(restaurantId);
        User user = findUser(userId);

        Like like = likeRepository
                .findByUserAndRestaurant(user, restaurant)
                .orElseThrow(() ->
                        new IllegalArgumentException("좋아요를 누르지 않았습니다.")
                );

        likeRepository.delete(like);

        return buildLikeResponse(restaurant, false);
    }

    /**
     * 좋아요 변경 직후의 집계를 담아 응답을 만듭니다.
     *
     * <p>save/delete는 트랜잭션 커밋 시점에 반영되므로, 집계 쿼리가 변경 전 값을
     * 읽지 않도록 먼저 flush합니다.
     */
    private LikeResponse buildLikeResponse(Restaurant restaurant, boolean liked) {

        likeRepository.flush();

        List<Long> ids = List.of(restaurant.getId());

        return new LikeResponse(
                restaurant.getId(),
                liked,
                loadTotalLikes(ids).getOrDefault(restaurant.getId(), 0L),
                loadSchoolLikes(ids).getOrDefault(
                        restaurant.getId(),
                        emptySchoolLikes()
                )
        );
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new NotFoundException("맛집을 찾을 수 없습니다.")
                );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("사용자를 찾을 수 없습니다.")
                );
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