package org.example.team2backend.dto;

import lombok.Getter;
import org.example.team2backend.enums.RestaurantTagType;

import java.util.List;

/**
 * 맛집 태그 수정 응답. 교체된 최종 태그 목록을 돌려줍니다.
 */
@Getter
public class RestaurantTagUpdateResponse {

    private final Long restaurantId;

    private final List<RestaurantTagType> tags;

    private final String message;

    public RestaurantTagUpdateResponse(Long restaurantId, List<RestaurantTagType> tags) {
        this.restaurantId = restaurantId;
        this.tags = tags;
        this.message = "맛집 정보가 수정되었습니다.";
    }
}
