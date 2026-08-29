package org.example.team2backend.dto;

import lombok.Getter;

@Getter
public class RestaurantImageResponse {

    private final Long restaurantId;

    private final String imageUrl;

    public RestaurantImageResponse(Long restaurantId, String imageUrl) {
        this.restaurantId = restaurantId;
        this.imageUrl = imageUrl;
    }
}
