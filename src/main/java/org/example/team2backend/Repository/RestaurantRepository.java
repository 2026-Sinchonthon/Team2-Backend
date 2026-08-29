package org.example.team2backend.repository;

import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByKakaoPlaceId(String kakaoPlaceId);

    /**
     * 전체 맛집 목록: 완료가 기준치 이상인 맛집을 인기순으로 조회합니다.
     *
     * <p>완료가 하나도 없는 맛집은 join 결과에 행이 없어 자연히 제외됩니다
     * (기준치가 1 이상이므로 의도한 동작입니다).
     */
    @Query("""
            select r
            from Restaurant r
            join Check l on l.restaurant = r
            group by r
            having count(l) >= :minCheckCount
            order by count(l) desc, r.id asc
            """)
    List<Restaurant> findPopular(@Param("minCheckCount") long minCheckCount);

    /**
     * 학교별 맛집 목록: 전체 완료가 기준치 이상인 맛집 중, 해당 학교가
     * 학교별 완료 1위인 것만 그 학교 완료 수 기준 인기순으로 조회합니다.
     *
     * <p>"우리 학교가 다른 학교보다 더 좋아하는 맛집"을 보여주기 위한 것이라,
     * 해당 학교 완료가 많아도 다른 학교가 더 많으면 제외됩니다.
     * 1위가 동점이면 동점인 학교 모두에서 표시됩니다.
     */
    @Query("""
            select r
            from Restaurant r
            join Check l on l.restaurant = r
            where l.user.school = :school
              and (
                select count(total)
                from Check total
                where total.restaurant = r
              ) >= :minTotalCheckCount
              and (
                select count(mine)
                from Check mine
                where mine.restaurant = r
                  and mine.user.school = :school
              ) >= all (
                select count(other)
                from Check other
                where other.restaurant = r
                group by other.user.school
              )
            group by r
            order by count(l) desc, r.id asc
            """)
    List<Restaurant> findTopRankedBySchool(
            @Param("school") School school,
            @Param("minTotalCheckCount") long minTotalCheckCount
    );
}
