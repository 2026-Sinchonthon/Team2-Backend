package org.example.team2backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO(로그인 담당): 실제 로그인/인증 붙을 때 이메일, 닉네임 등 필요한 필드로 채워주세요.
// RestaurantService/Like가 school 기준 집계를 쓰므로 school 필드는 유지되어야 합니다.
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String school;
}
