package org.example.team2backend.dto;

import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.enums.RestaurantTagType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 맛집 상세 조회 응답. 지도 핀이나 하단 목록에서 맛집을 선택했을 때
 * 설명바에 표시할 정보를 담습니다.
 */
@Getter
public class RestaurantDetailResponse {

    private final Long restaurantId;

    private final String name;

    private final String address;

    private final Double latitude;

    private final Double longitude;

    private final String description;

    private final List<RestaurantTagType> tags;

    private final long likeCount;

    /** 학교별 좋아요 수. 좋아요가 없는 학교도 0으로 포함됩니다. */
    private final Map<String, Long> likeCountByUniversity;

    /** 현재 사용자의 좋아요 여부. userId를 넘기지 않으면 false입니다. */
    private final boolean liked;

    private final LocalDateTime createdAt;

    public RestaurantDetailResponse(
            Restaurant restaurant,
            List<RestaurantTagType> tags,
            long likeCount,
            Map<String, Long> likeCountByUniversity,
            boolean liked
    ) {
        this.restaurantId = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.description = restaurant.getDescription();
        this.tags = tags;
        this.likeCount = likeCount;
        this.likeCountByUniversity = likeCountByUniversity;
        this.liked = liked;
        this.createdAt = restaurant.getCreatedAt();
    }
}
