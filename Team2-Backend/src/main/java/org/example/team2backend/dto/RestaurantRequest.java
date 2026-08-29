package org.example.team2backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantRequest {

    private String kakaoPlaceId;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    private String category;
}