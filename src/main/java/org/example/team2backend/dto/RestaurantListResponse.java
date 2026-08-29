package org.example.team2backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.team2backend.enums.University;
import lombok.Getter;

import java.util.List;

/**
 * 맛집 목록 조회 응답.
 *
 * <p>명세상 배열이 아니라 객체로 감싸며, 학교별 조회일 때만 university가 붙습니다.
 * 프론트는 restaurants 배열의 순서를 그대로 지도 핀과 하단 목록에 사용합니다.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantListResponse {

    private final String university;

    private final List<RestaurantListItem> restaurants;

    private RestaurantListResponse(
            String university,
            List<RestaurantListItem> restaurants
    ) {
        this.university = university;
        this.restaurants = restaurants;
    }

    public static RestaurantListResponse of(List<RestaurantListItem> restaurants) {
        return new RestaurantListResponse(null, restaurants);
    }

    public static RestaurantListResponse ofUniversity(
            University university,
            List<RestaurantListItem> restaurants
    ) {
        return new RestaurantListResponse(university.name(), restaurants);
    }
}
