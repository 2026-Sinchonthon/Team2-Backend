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

    private long likeCount;

    private Map<String, Long> likeCountByUniversity;

    public RestaurantResponse(
            Restaurant restaurant,
            long likeCount,
            Map<String, Long> likeCountByUniversity
    ) {
        this.restaurantId = restaurant.getId();
        this.kakaoPlaceId = restaurant.getKakaoPlaceId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.likeCount = likeCount;
        this.likeCountByUniversity = likeCountByUniversity;
    }
}
