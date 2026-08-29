package org.example.team2backend.repository;

import org.example.team2backend.entity.Like;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    /**
     * 여러 맛집의 전체 좋아요 수를 한 번에 조회합니다.
     *
     * <p>반환 행: [restaurantId(Long), count(Long)]
     */
    @Query("""
            select l.restaurant.id, count(l)
            from Like l
            where l.restaurant.id in :restaurantIds
            group by l.restaurant.id
            """)
    List<Object[]> countTotalByRestaurantIds(
            @Param("restaurantIds") List<Long> restaurantIds
    );

    /**
     * 여러 맛집의 학교별 좋아요 수를 한 번에 조회합니다.
     *
     * <p>맛집마다 학교 수만큼 카운트 쿼리를 날리면 N+1이 되므로,
     * GROUP BY 한 번으로 모두 집계합니다.
     *
     * <p>반환 행: [restaurantId(Long), school(String), count(Long)]
     * 좋아요가 없는 학교는 행 자체가 없으므로, 호출부에서 0으로 채웁니다.
     */
    @Query("""
            select l.restaurant.id, l.user.school, count(l)
            from Like l
            where l.restaurant.id in :restaurantIds
            group by l.restaurant.id, l.user.school
            """)
    List<Object[]> countBySchoolForRestaurantIds(
            @Param("restaurantIds") List<Long> restaurantIds
    );

    /**
     * 특정 유저가 좋아요를 누른 맛집 id 목록입니다.
     *
     * <p>목록/상세의 liked 여부를 맛집마다 조회하지 않고 한 번에 판단합니다.
     */
    @Query("""
            select l.restaurant.id
            from Like l
            where l.user.id = :userId
              and l.restaurant.id in :restaurantIds
            """)
    List<Long> findLikedRestaurantIds(
            @Param("userId") Long userId,
            @Param("restaurantIds") List<Long> restaurantIds
    );
}
