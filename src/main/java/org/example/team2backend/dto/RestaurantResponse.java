package org.example.team2backend.dto;

import org.example.team2backend.entity.Restaurant;
import lombok.Getter;

import java.util.Map;

@Getter
public class RestaurantResponse {

    private Long restaurantId;

    private String kakaoPlaceId;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    private long checkCount;

    private Map<String, Long> checkCountByUniversity;

    public RestaurantResponse(
            Restaurant restaurant,
            long checkCount,
            Map<String, Long> checkCountByUniversity
    ) {
        this.restaurantId = restaurant.getId();
        this.kakaoPlaceId = restaurant.getKakaoPlaceId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.checkCount = checkCount;
        this.checkCountByUniversity = checkCountByUniversity;
    }
}
