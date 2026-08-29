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
import org.example.team2backend.entity.School;
import org.example.team2backend.entity.User;
import org.example.team2backend.enums.RestaurantTagType;
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
     * 신촌세끼에 맛집으로 노출되기 위한 최소 전체 좋아요 수.
     */
    private static final long MIN_TOTAL_LIKE_COUNT = 10;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTagRepository restaurantTagRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    /**
     * 전체 좋아요 수가 10개 이상인 맛집만 조회합니다.
     *
     * <p>좋아요 수가 많은 순서로 정렬됩니다.
     */
    @Transactional(readOnly = true)
    public RestaurantListResponse getRestaurants() {

        List<Restaurant> restaurants =
                restaurantRepository.findPopular(
                        MIN_TOTAL_LIKE_COUNT
                );

        if (restaurants.isEmpty()) {
            return RestaurantListResponse.of(List.of());
        }

        List<Long> restaurantIds = restaurants.stream()
                .map(Restaurant::getId)
                .toList();

        Map<Long, Long> totalLikes =
                loadTotalLikes(restaurantIds);

        List<RestaurantListItem> items = restaurants.stream()
                .map(restaurant ->
                        RestaurantListItem.of(
                                restaurant,
                                totalLikes.getOrDefault(
                                        restaurant.getId(),
                                        0L
                                )
                        )
                )
                .toList();

        return RestaurantListResponse.of(items);
    }

    /**
     * 여러 맛집의 전체 좋아요 수를 한 번에 조회합니다.
     */
    private Map<Long, Long> loadTotalLikes(
            List<Long> restaurantIds
    ) {

        Map<Long, Long> totalLikes = new HashMap<>();

        for (Object[] row :
                likeRepository.countTotalByRestaurantIds(restaurantIds)) {

            totalLikes.put(
                    (Long) row[0],
                    (Long) row[1]
            );
        }

        return totalLikes;
    }

    /**
     * 여러 맛집의 학교별 좋아요 수를 한 번에 조회합니다.
     */
    private Map<Long, Map<String, Long>> loadSchoolLikes(
            List<Long> restaurantIds
    ) {

        Map<Long, Map<String, Long>> schoolLikes =
                new HashMap<>();

        for (Object[] row :
                likeRepository.countBySchoolForRestaurantIds(
                        restaurantIds
                )) {

            Long restaurantId = (Long) row[0];
            School school = (School) row[1];
            Long count = (Long) row[2];

            Map<String, Long> counts =
                    schoolLikes.computeIfAbsent(
                            restaurantId,
                            id -> emptySchoolLikes()
                    );

            counts.put(
                    school.name(),
                    count
            );
        }

        return schoolLikes;
    }

    /**
     * 학교별 좋아요가 없는 경우에도 모든 학교를 0으로 채웁니다.
     */
    private Map<String, Long> emptySchoolLikes() {

        Map<String, Long> counts =
                new LinkedHashMap<>();

        for (School school : School.values()) {
            counts.put(
                    school.name(),
                    0L
            );
        }

        return counts;
    }

    /**
     * 카카오 검색 결과의 식당을 신촌세끼 DB에 등록합니다.
     */
    public RestaurantResponse createRestaurant(
            RestaurantRequest request
    ) {

        Restaurant restaurant =
                restaurantRepository.save(
                        new Restaurant(
                                request.getKakaoPlaceId(),
                                request.getName(),
                                request.getAddress(),
                                request.getLatitude(),
                                request.getLongitude(),
                                request.getCategory()
                        )
                );

        return toResponse(restaurant);
    }

    /**
     * 맛집 상세를 조회합니다.
     *
     * <p>로그인하지 않은 경우 liked는 false입니다.
     */
    @Transactional(readOnly = true)
    public RestaurantDetailResponse getRestaurantDetail(
            Long restaurantId,
            Long userId
    ) {

        Restaurant restaurant =
                findRestaurant(restaurantId);

        List<RestaurantTagType> tags =
                restaurantTagRepository
                        .findByRestaurant(restaurant)
                        .stream()
                        .map(RestaurantTag::getTagName)
                        .toList();

        List<Long> ids =
                List.of(restaurant.getId());

        boolean liked =
                userId != null
                        && !likeRepository
                        .findLikedRestaurantIds(
                                userId,
                                ids
                        )
                        .isEmpty();

        return new RestaurantDetailResponse(
                restaurant,
                tags,
                loadTotalLikes(ids)
                        .getOrDefault(
                                restaurant.getId(),
                                0L
                        ),
                loadSchoolLikes(ids)
                        .getOrDefault(
                                restaurant.getId(),
                                emptySchoolLikes()
                        ),
                liked
        );
    }

    /**
     * 맛집 태그를 전체 교체합니다.
     */
    public RestaurantTagUpdateResponse updateTags(
            Long restaurantId,
            RestaurantTagUpdateRequest request
    ) {

        Restaurant restaurant =
                findRestaurant(restaurantId);

        List<RestaurantTagType> tags =
                (request.getTags() == null)
                        ? List.of()
                        : request.getTags()
                        .stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        restaurantTagRepository
                .deleteByRestaurant(restaurant);

        restaurantTagRepository.flush();

        List<RestaurantTag> saved =
                restaurantTagRepository.saveAll(
                        tags.stream()
                                .map(tag ->
                                        new RestaurantTag(
                                                restaurant,
                                                tag
                                        )
                                )
                                .toList()
                );

        return new RestaurantTagUpdateResponse(
                restaurant.getId(),
                saved.stream()
                        .map(RestaurantTag::getTagName)
                        .toList()
        );
    }

    /**
     * 좋아요 추가.
     */
    public LikeResponse addLike(
            Long restaurantId,
            Long userId
    ) {

        Restaurant restaurant =
                findRestaurant(restaurantId);

        User user =
                findUser(userId);

        if (likeRepository.existsByUserAndRestaurant(
                user,
                restaurant
        )) {

            throw new IllegalArgumentException(
                    "이미 좋아요를 눌렀습니다."
            );
        }

        likeRepository.save(
                new Like(user, restaurant)
        );

        return buildLikeResponse(
                restaurant,
                true
        );
    }

    /**
     * 좋아요 취소.
     */
    public LikeResponse removeLike(
            Long restaurantId,
            Long userId
    ) {

        Restaurant restaurant =
                findRestaurant(restaurantId);

        User user =
                findUser(userId);

        Like like =
                likeRepository
                        .findByUserAndRestaurant(
                                user,
                                restaurant
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "좋아요를 누르지 않았습니다."
                                )
                        );

        likeRepository.delete(like);

        return buildLikeResponse(
                restaurant,
                false
        );
    }

    /**
     * 좋아요 변경 직후의 좋아요 집계를 반환합니다.
     */
    private LikeResponse buildLikeResponse(
            Restaurant restaurant,
            boolean liked
    ) {

        likeRepository.flush();

        List<Long> ids =
                List.of(restaurant.getId());

        return new LikeResponse(
                restaurant.getId(),
                liked,
                loadTotalLikes(ids)
                        .getOrDefault(
                                restaurant.getId(),
                                0L
                        ),
                loadSchoolLikes(ids)
                        .getOrDefault(
                                restaurant.getId(),
                                emptySchoolLikes()
                        )
        );
    }

    /**
     * 맛집을 조회합니다.
     */
    private Restaurant findRestaurant(
            Long restaurantId
    ) {

        return restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "맛집을 찾을 수 없습니다."
                        )
                );
    }

    /**
     * 사용자를 조회합니다.
     */
    private User findUser(
            Long userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "사용자를 찾을 수 없습니다."
                        )
                );
    }

    /**
     * 맛집 등록 응답을 생성합니다.
     */
    private RestaurantResponse toResponse(
            Restaurant restaurant
    ) {

        List<Long> ids =
                List.of(restaurant.getId());

        return new RestaurantResponse(
                restaurant,
                loadTotalLikes(ids)
                        .getOrDefault(
                                restaurant.getId(),
                                0L
                        ),
                loadSchoolLikes(ids)
                        .getOrDefault(
                                restaurant.getId(),
                                emptySchoolLikes()
                        )
        );
    }
}