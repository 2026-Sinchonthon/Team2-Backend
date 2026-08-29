package org.example.team2backend.service;

import org.example.team2backend.dto.RestaurantRequest;
import org.example.team2backend.dto.RestaurantResponse;
import org.example.team2backend.entity.Like;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.School;
import org.example.team2backend.entity.User;
import org.example.team2backend.repository.LikeRepository;
import org.example.team2backend.repository.RestaurantRepository;
import org.example.team2backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurants(String category) {

        List<Restaurant> restaurants;

        if (category == null || category.isBlank()) {
            restaurants = restaurantRepository.findAll();
        } else {
            restaurants = restaurantRepository.findByCategory(category);
        }

        return restaurants.stream()
                .map(this::toResponse)
                .toList();
    }

    public RestaurantResponse createOrGetRestaurant(
            RestaurantRequest request
    ) {

        Restaurant restaurant =
                restaurantRepository
                        .findByKakaoPlaceId(request.getKakaoPlaceId())
                        .orElseGet(() ->
                                restaurantRepository.save(
                                        new Restaurant(
                                                request.getKakaoPlaceId(),
                                                request.getName(),
                                                request.getAddress(),
                                                request.getLatitude(),
                                                request.getLongitude(),
                                                request.getCategory()
                                        )
                                )
                        );

        return toResponse(restaurant);
    }

    public void addLike(Long restaurantId, Long userId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new IllegalArgumentException("맛집을 찾을 수 없습니다.")
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        if (likeRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
        }

        Like like = new Like(user, restaurant);

        likeRepository.save(like);
    }

    public void removeLike(Long restaurantId, Long userId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new IllegalArgumentException("맛집을 찾을 수 없습니다.")
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Like like = likeRepository
                .findByUserAndRestaurant(user, restaurant)
                .orElseThrow(() ->
                        new IllegalArgumentException("좋아요를 누르지 않았습니다.")
                );

        likeRepository.delete(like);
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {

        long totalLikeCount =
                likeRepository.countByRestaurant(restaurant);

        Map<String, Long> schoolLikes = new HashMap<>();

        for (School school : School.values()) {
            schoolLikes.put(
                    school.name(),
                    likeRepository.countByRestaurantAndUserSchool(
                            restaurant,
                            school
                    )
            );
        }

        return new RestaurantResponse(
                restaurant,
                totalLikeCount,
                schoolLikes
        );
    }
}