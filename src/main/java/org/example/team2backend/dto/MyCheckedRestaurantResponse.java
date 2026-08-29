package org.example.team2backend.dto;

import lombok.Getter;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.enums.RestaurantTagType;

import java.util.List;

@Getter
public class MyCheckedRestaurantResponse {

    private final Long restaurantId;

    private final String name;

    private final String address;

    private final Double latitude;

    private final Double longitude;

    private final String description;

    private final List<RestaurantTagType> tags;

    public MyCheckedRestaurantResponse(
            Restaurant restaurant,
            List<RestaurantTagType> tags
    ) {
        this.restaurantId = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.description = restaurant.getDescription();
        this.tags = tags;
    }
}
