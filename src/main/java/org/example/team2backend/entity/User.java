package org.example.team2backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private School school;

    @Builder
    private User(String email, String password, String name, School school) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.school = school;
    }

    public static User createLocalUser(String email, String encodedPassword, String name, School school) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .school(school)
                .build();
    }
}
