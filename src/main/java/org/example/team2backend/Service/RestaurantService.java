package org.example.team2backend.service;

import org.example.team2backend.dto.CheckResponse;
import org.example.team2backend.dto.MyCheckedRestaurantResponse;
import org.example.team2backend.dto.RestaurantDetailResponse;
import org.example.team2backend.dto.RestaurantListItem;
import org.example.team2backend.dto.RestaurantListResponse;
import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.dto.RestaurantTagUpdateRequest;
import org.example.team2backend.dto.RestaurantTagUpdateResponse;
import org.example.team2backend.entity.Check;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.RestaurantTag;
import org.example.team2backend.entity.School;
import org.example.team2backend.entity.User;
import org.example.team2backend.enums.RestaurantTagType;
import org.example.team2backend.exception.NotFoundException;
import org.example.team2backend.repository.CheckRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    /**
     * 전체 보기에서 맛집으로 노출되기 위한 최소 완료 수.
     */
    private static final long MIN_TOTAL_CHECK_COUNT = 10;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTagRepository restaurantTagRepository;
    private final CheckRepository checkRepository;
    private final UserRepository userRepository;

    /**
     * 맛집 목록을 조회합니다.
     *
     * <p>어느 쪽이든 전체 완료 {@value #MIN_TOTAL_CHECK_COUNT}개 이상이 전제입니다.
     * university가 없으면 전체 완료 순으로, 있으면 그 학교가 학교별 완료 1위인
     * 맛집만 해당 학교 완료 순으로 반환합니다. 필터와 정렬은 모두 DB에서 처리합니다.
     */
    @Transactional(readOnly = true)
    public RestaurantListResponse getRestaurants(School school) {

        List<Restaurant> restaurants = (school == null)
                ? restaurantRepository.findPopular(MIN_TOTAL_CHECK_COUNT)
                : restaurantRepository.findTopRankedBySchool(
                        school,
                        MIN_TOTAL_CHECK_COUNT
                );

        if (restaurants.isEmpty()) {
            return (school == null)
                    ? RestaurantListResponse.of(List.of())
                    : RestaurantListResponse.ofUniversity(school, List.of());
        }

        // 맛집마다 카운트 쿼리를 날리지 않도록 완료 집계를 한 번에 읽어옵니다.
        List<Long> restaurantIds = restaurants.stream()
                .map(Restaurant::getId)
                .toList();

        Map<Long, Long> totalChecks = loadTotalChecks(restaurantIds);

        if (school == null) {
            List<RestaurantListItem> items = restaurants.stream()
                    .map(restaurant -> RestaurantListItem.of(
                            restaurant,
                            totalChecks.getOrDefault(restaurant.getId(), 0L)
                    ))
                    .toList();

            return RestaurantListResponse.of(items);
        }

        Map<Long, Map<String, Long>> schoolChecks = loadSchoolChecks(restaurantIds);

        List<RestaurantListItem> items = restaurants.stream()
                .map(restaurant -> RestaurantListItem.ofUniversity(
                        restaurant,
                        totalChecks.getOrDefault(restaurant.getId(), 0L),
                        schoolChecks
                                .getOrDefault(restaurant.getId(), emptySchoolChecks())
                                .getOrDefault(school.name(), 0L)
                ))
                .toList();

        return RestaurantListResponse.ofUniversity(school, items);
    }

    /**
     * 맛집별 전체 완료 수. 완료가 없는 맛집은 결과에 없으므로 호출부에서 0으로 채웁니다.
     */
    private Map<Long, Long> loadTotalChecks(List<Long> restaurantIds) {

        Map<Long, Long> totalChecks = new HashMap<>();

        for (Object[] row : checkRepository.countTotalByRestaurantIds(restaurantIds)) {
            totalChecks.put((Long) row[0], (Long) row[1]);
        }

        return totalChecks;
    }

    /**
     * 맛집별 · 학교별 완료 수. 완료가 없는 학교도 0으로 채워 항상 전체 학교를 반환합니다.
     */
    private Map<Long, Map<String, Long>> loadSchoolChecks(List<Long> restaurantIds) {

        Map<Long, Map<String, Long>> schoolChecks = new HashMap<>();

        for (Object[] row : checkRepository.countBySchoolForRestaurantIds(restaurantIds)) {
            Long restaurantId = (Long) row[0];
            School school = (School) row[1];
            Long count = (Long) row[2];

            Map<String, Long> counts = schoolChecks.computeIfAbsent(
                    restaurantId,
                    id -> emptySchoolChecks()
            );

            counts.put(school.name(), count);
        }

        return schoolChecks;
    }

    private Map<String, Long> emptySchoolChecks() {

        Map<String, Long> counts = new LinkedHashMap<>();

        for (School school : School.values()) {
            counts.put(school.name(), 0L);
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
     * <p>userId가 null이면(로그인 전) checked는 항상 false입니다.
     */
    @Transactional(readOnly = true)
    public RestaurantDetailResponse getRestaurantDetail(
            Long restaurantId,
            Long userId
    ) {
        Restaurant restaurant = findRestaurant(restaurantId);

        List<RestaurantTagType> tags = restaurantTagRepository.findByRestaurant(restaurant)
                .stream()
                .map(RestaurantTag::getTagName)
                .toList();

        List<Long> ids = List.of(restaurant.getId());

        boolean checked = userId != null
                && !checkRepository.findCheckedRestaurantIds(userId, ids).isEmpty();

        return new RestaurantDetailResponse(
                restaurant,
                tags,
                loadTotalChecks(ids).getOrDefault(restaurant.getId(), 0L),
                loadSchoolChecks(ids).getOrDefault(
                        restaurant.getId(),
                        emptySchoolChecks()
                ),
                checked
        );
    }

    /**
     * 맛집의 태그를 교체합니다. 기존 태그를 모두 지우고 요청받은 태그로 새로 넣습니다.
     *
     * <p>태그는 {@link RestaurantTagType} 다섯 가지로 고정되어 있고, 요청 역직렬화
     * 단계에서 이미 검증되었습니다. 여기서는 중복만 제거합니다
     * (restaurant_id, tag_name) UNIQUE 제약에 걸리지 않도록).
     */
    public RestaurantTagUpdateResponse updateTags(
            Long restaurantId,
            RestaurantTagUpdateRequest request
    ) {
        Restaurant restaurant = findRestaurant(restaurantId);

        List<RestaurantTagType> tags = (request.getTags() == null)
                ? List.of()
                : request.getTags().stream()
                        .filter(Objects::nonNull)
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

    public CheckResponse addCheck(Long restaurantId, Long userId) {

        Restaurant restaurant = findRestaurant(restaurantId);
        User user = findUser(userId);

        if (checkRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new IllegalArgumentException("이미 완료를 눌렀습니다.");
        }

        checkRepository.save(new Check(user, restaurant));

        return buildCheckResponse(restaurant, true);
    }

    public CheckResponse removeCheck(Long restaurantId, Long userId) {

        Restaurant restaurant = findRestaurant(restaurantId);
        User user = findUser(userId);

        Check check = checkRepository
                .findByUserAndRestaurant(user, restaurant)
                .orElseThrow(() ->
                        new IllegalArgumentException("완료를 누르지 않았습니다.")
                );

        checkRepository.delete(check);

        return buildCheckResponse(restaurant, false);
    }

    /**
     * 마이페이지에서 현재 사용자가 완료/찜한 맛집 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<MyCheckedRestaurantResponse> getMyCheckedRestaurants(Long userId) {

        findUser(userId);

        List<Check> checks = checkRepository.findByUserIdWithRestaurantOrderByCreatedAtDesc(userId);

        if (checks.isEmpty()) {
            return List.of();
        }

        List<Restaurant> restaurants = checks.stream()
                .map(Check::getRestaurant)
                .toList();

        Map<Long, List<RestaurantTagType>> tagsByRestaurantId = loadTagsByRestaurantId(
                restaurants.stream()
                        .map(Restaurant::getId)
                        .toList()
        );

        return restaurants.stream()
                .map(restaurant -> new MyCheckedRestaurantResponse(
                        restaurant,
                        tagsByRestaurantId.getOrDefault(restaurant.getId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<RestaurantTagType>> loadTagsByRestaurantId(List<Long> restaurantIds) {

        if (restaurantIds.isEmpty()) {
            return Map.of();
        }

        return restaurantTagRepository.findByRestaurantIdIn(restaurantIds)
                .stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getRestaurant().getId(),
                        Collectors.mapping(RestaurantTag::getTagName, Collectors.toList())
                ));
    }

    /**
     * 완료 변경 직후의 집계를 담아 응답을 만듭니다.
     *
     * <p>save/delete는 트랜잭션 커밋 시점에 반영되므로, 집계 쿼리가 변경 전 값을
     * 읽지 않도록 먼저 flush합니다.
     */
    private CheckResponse buildCheckResponse(Restaurant restaurant, boolean checked) {

        checkRepository.flush();

        List<Long> ids = List.of(restaurant.getId());

        return new CheckResponse(
                restaurant.getId(),
                checked,
                loadTotalChecks(ids).getOrDefault(restaurant.getId(), 0L),
                loadSchoolChecks(ids).getOrDefault(
                        restaurant.getId(),
                        emptySchoolChecks()
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
     * 학교별 완료 맵의 형태(항상 전체 학교 포함)를 목록 응답과 맞춥니다.
     */
    private RestaurantResponse toResponse(Restaurant restaurant) {

        List<Long> ids = List.of(restaurant.getId());

        return new RestaurantResponse(
                restaurant,
                loadTotalChecks(ids).getOrDefault(restaurant.getId(), 0L),
                loadSchoolChecks(ids).getOrDefault(
                        restaurant.getId(),
                        emptySchoolChecks()
                )
        );
    }
}
