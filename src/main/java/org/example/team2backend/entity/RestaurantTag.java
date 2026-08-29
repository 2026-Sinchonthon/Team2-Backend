package org.example.team2backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.team2backend.enums.RestaurantTagType;

/**
 * 맛집에 붙는 태그. {@link RestaurantTagType} 다섯 가지로 고정됩니다.
 *
 * <p>맛집 하나에 여러 태그가 붙습니다. 태그 수정은 기존 태그를 모두 지우고
 * 새로 넣는 방식이라, 같은 맛집에 같은 태그가 중복 저장되지 않도록
 * (restaurant_id, tag_name)에 UNIQUE 제약을 둡니다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "restaurant_tags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_restaurant_tag",
                        columnNames = {"restaurant_id", "tag_name"}
                )
        }
)
public class RestaurantTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_name", nullable = false)
    private RestaurantTagType tagName;

    public RestaurantTag(Restaurant restaurant, RestaurantTagType tagName) {
        this.restaurant = restaurant;
        this.tagName = tagName;
    }
}
