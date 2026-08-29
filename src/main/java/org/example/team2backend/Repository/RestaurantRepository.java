package org.example.team2backend.repository;

import org.example.team2backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCategory(String category);

    Optional<Restaurant> findByKakaoPlaceId(String kakaoPlaceId);
}