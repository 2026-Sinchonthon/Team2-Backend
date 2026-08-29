package org.example.team2backend.repository;

import org.example.team2backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCategory(String category);

    Optional<Restaurant> findByKakaoPlaceId(String kakaoPlaceId);

    /**
     * 전체 맛집 목록: 좋아요가 기준치 이상인 맛집을 인기순으로 조회합니다.
     *
     * <p>좋아요가 하나도 없는 맛집은 join 결과에 행이 없어 자연히 제외됩니다
     * (기준치가 1 이상이므로 의도한 동작입니다).
     */
    @Query("""
            select r
            from Restaurant r
            join Like l on l.restaurant = r
            group by r
            having count(l) >= :minLikeCount
            order by count(l) desc, r.id asc
            """)
    List<Restaurant> findPopular(@Param("minLikeCount") long minLikeCount);

    /**
     * 학교별 맛집 목록: 해당 학교 학생의 좋아요가 기준치 이상인 맛집을
     * 그 학교 좋아요 수 기준 인기순으로 조회합니다.
     *
     * <p>전체 좋아요가 기준에 못 미쳐도 해당 학교에서 인기가 있으면 포함됩니다.
     */
    @Query("""
            select r
            from Restaurant r
            join Like l on l.restaurant = r
            where l.user.school = :school
            group by r
            having count(l) >= :minLikeCount
            order by count(l) desc, r.id asc
            """)
    List<Restaurant> findPopularBySchool(
            @Param("school") String school,
            @Param("minLikeCount") long minLikeCount
    );
}