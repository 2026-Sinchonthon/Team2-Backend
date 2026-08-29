package org.example.team2backend.repository;

import org.example.team2backend.entity.Like;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndRestaurant(
            User user,
            Restaurant restaurant
    );

    boolean existsByUserAndRestaurant(
            User user,
            Restaurant restaurant
    );

    long countByRestaurant(Restaurant restaurant);

    long countByRestaurantAndUserSchool(
            Restaurant restaurant,
            String school
    );
}
