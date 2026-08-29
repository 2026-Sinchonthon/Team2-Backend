package org.example.team2backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kakao 장소 ID
    @Column(unique = true, nullable = false)
    private String kakaoPlaceId;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    // 맛집 상세 화면에 노출되는 설명
    @Column(length = 1000)
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Restaurant(
            String kakaoPlaceId,
            String name,
            String address,
            Double latitude,
            Double longitude
    ) {
        this.kakaoPlaceId = kakaoPlaceId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}