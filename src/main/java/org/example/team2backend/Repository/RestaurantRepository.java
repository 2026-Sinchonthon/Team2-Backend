package org.example.team2backend.repository;

import org.example.team2backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * 전체 좋아요 수가 10개 이상인 맛집만 조회하고
     * 좋아요 수가 많은 순서로 정렬합니다.
     */
    @Query("""
            select r
            from Restaurant r
            join Like l on l.restaurant = r
            group by r
            having count(l) >= :minLikeCount
            order by count(l) desc, r.id asc
            """)
    List<Restaurant> findPopular(
            @Param("minLikeCount") long minLikeCount
    );
}