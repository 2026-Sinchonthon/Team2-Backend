package org.example.team2backend.exception;

/**
 * 요청한 리소스가 없을 때 던집니다. 404로 변환됩니다.
 *
 * <p>잘못된 입력(400)과 구분하기 위한 것으로, 없는 맛집 · 없는 사용자처럼
 * "요청 자체는 올바르지만 대상이 없는" 경우에 사용합니다.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
