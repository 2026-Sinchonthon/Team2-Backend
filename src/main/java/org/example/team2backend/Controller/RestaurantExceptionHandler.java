package org.example.team2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * RestaurantController의 예외를 HTTP 상태로 변환합니다.
 *
 * <p>잘못된 요청(없는 학교명, 없는 맛집 등)이 500으로 나가면 프론트가 서버 장애와
 * 구분할 수 없으므로 400으로 내려줍니다.
 *
 * <p>공통 응답 포맷은 다른 담당자 영역과 겹칠 수 있어, 우선 이 컨트롤러 범위로만
 * 한정했습니다. 팀 공통 포맷이 정해지면 그쪽으로 옮깁니다.
 */
@RestControllerAdvice(assignableTypes = RestaurantController.class)
public class RestaurantExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", exception.getMessage()));
    }
}
