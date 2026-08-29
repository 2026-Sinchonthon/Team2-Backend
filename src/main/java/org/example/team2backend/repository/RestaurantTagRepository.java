package org.example.team2backend.repository;

import org.example.team2backend.entity.Restaurant;
import org.example.team2backend.entity.RestaurantTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTagRepository extends JpaRepository<RestaurantTag, Long> {

    List<RestaurantTag> findByRestaurant(Restaurant restaurant);

    /**
     * 여러 맛집의 태그를 한 번에 조회합니다.
     *
     * <p>목록 조회에서 맛집마다 태그를 따로 읽으면 N+1이 되므로,
     * id 목록으로 한 번에 가져와 서비스에서 맛집별로 묶습니다.
     */
    List<RestaurantTag> findByRestaurantIdIn(List<Long> restaurantIds);

    /**
     * 태그 수정 시 기존 태그를 모두 지웁니다.
     *
     * <p>파생 삭제 쿼리는 엔티티를 하나씩 select 후 delete 하므로,
     * 삭제 건수가 많지 않은 태그에는 적합합니다.
     */
    void deleteByRestaurant(Restaurant restaurant);
}
