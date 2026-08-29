package org.example.team2backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.team2backend.entity.Restaurant;
import lombok.Getter;

/**
 * 맛집 목록의 항목 하나. 지도 핀과 하단 목록에 함께 쓰입니다.
 *
 * <p>학교별 조회일 때만 universityCheckCount가 채워지고, 전체 조회에서는
 * null이라 응답에서 아예 빠집니다.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantListItem {

    private final Long restaurantId;

    private final String name;

    private final Double latitude;

    private final Double longitude;

    private final long checkCount;

    private final Long universityCheckCount;

    private RestaurantListItem(
            Restaurant restaurant,
            long checkCount,
            Long universityCheckCount
    ) {
        this.restaurantId = restaurant.getId();
        this.name = restaurant.getName();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.checkCount = checkCount;
        this.universityCheckCount = universityCheckCount;
    }

    /**
     * 전체 조회용. universityCheckCount 없이 전체 완료 수만 담습니다.
     */
    public static RestaurantListItem of(Restaurant restaurant, long checkCount) {
        return new RestaurantListItem(restaurant, checkCount, null);
    }

    /**
     * 학교별 조회용. 전체 완료 수와 해당 학교 완료 수를 함께 담습니다.
     */
    public static RestaurantListItem ofUniversity(
            Restaurant restaurant,
            long checkCount,
            long universityCheckCount
    ) {
        return new RestaurantListItem(restaurant, checkCount, universityCheckCount);
    }
}
