package org.example.team2backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "restaurant_checks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_restaurant",
                        columnNames = {"user_id", "restaurant_id"}
                )
        }
)
public class Check {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Check(User user, Restaurant restaurant) {
        this.user = user;
        this.restaurant = restaurant;
    }
}