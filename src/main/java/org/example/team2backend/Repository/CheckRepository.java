package org.example.team2backend.repository;

import org.example.team2backend.entity.Check;
import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CheckRepository extends JpaRepository<Check, Long> {

    Optional<Check> findByUserAndRestaurant(
            User user,
            Restaurant restaurant
    );

    boolean existsByUserAndRestaurant(
            User user,
            Restaurant restaurant
    );

    @Query("""
            select l
            from Check l
            join fetch l.restaurant
            where l.user.id = :userId
            order by l.createdAt desc
            """)
    List<Check> findByUserIdWithRestaurantOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );

    /**
     * 여러 맛집의 전체 완료 수를 한 번에 조회합니다.
     *
     * <p>반환 행: [restaurantId(Long), count(Long)]
     */
    @Query("""
            select l.restaurant.id, count(l)
            from Check l
            where l.restaurant.id in :restaurantIds
            group by l.restaurant.id
            """)
    List<Object[]> countTotalByRestaurantIds(
            @Param("restaurantIds") List<Long> restaurantIds
    );

    /**
     * 여러 맛집의 학교별 완료 수를 한 번에 조회합니다.
     *
     * <p>맛집마다 학교 수만큼 카운트 쿼리를 날리면 N+1이 되므로,
     * GROUP BY 한 번으로 모두 집계합니다.
     *
     * <p>반환 행: [restaurantId(Long), school(String), count(Long)]
     * 완료가 없는 학교는 행 자체가 없으므로, 호출부에서 0으로 채웁니다.
     */
    @Query("""
            select l.restaurant.id, l.user.school, count(l)
            from Check l
            where l.restaurant.id in :restaurantIds
            group by l.restaurant.id, l.user.school
            """)
    List<Object[]> countBySchoolForRestaurantIds(
            @Param("restaurantIds") List<Long> restaurantIds
    );

    /**
     * 특정 유저가 완료를 누른 맛집 id 목록입니다.
     *
     * <p>목록/상세의 checked 여부를 맛집마다 조회하지 않고 한 번에 판단합니다.
     */
    @Query("""
            select l.restaurant.id
            from Check l
            where l.user.id = :userId
              and l.restaurant.id in :restaurantIds
            """)
    List<Long> findCheckedRestaurantIds(
            @Param("userId") Long userId,
            @Param("restaurantIds") List<Long> restaurantIds
    );
}
