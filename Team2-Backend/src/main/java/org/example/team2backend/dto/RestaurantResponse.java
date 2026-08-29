package org.example.team2backend.dto;

import org.example.team2backend.entity.Restaurant;
import lombok.Getter;

import java.util.Map;

@Getter
public class RestaurantResponse {

    private Long id;

    private String kakaoPlaceId;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    private String category;

    private long totalLikeCount;

    private Map<String, Long> schoolLikes;

    public RestaurantResponse(
            Restaurant restaurant,
            long totalLikeCount,
            Map<String, Long> schoolLikes
    ) {
        this.id = restaurant.getId();
        this.kakaoPlaceId = restaurant.getKakaoPlaceId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.category = restaurant.getCategory();
        this.totalLikeCount = totalLikeCount;
        this.schoolLikes = schoolLikes;
    }
}