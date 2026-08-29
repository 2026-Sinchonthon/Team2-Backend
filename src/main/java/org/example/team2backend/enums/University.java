package org.example.team2backend.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 신촌 지역 대학교.
 *
 * <p>User.school은 로그인 담당 영역이라 String 타입을 유지하므로,
 * 비교/집계 시에는 {@link #name()} 값을 사용합니다.
 */
public enum University {

    SOGANG,
    YONSEI,
    EWHA,
    HONGIK,
    MYONGJI;

    /**
     * 문자열을 University로 변환합니다. 대소문자를 구분하지 않습니다.
     *
     * <p>목록 조회의 university 파라미터처럼 외부 입력을 다룰 때 사용합니다.
     * 알 수 없는 값이면 예외 대신 빈 Optional을 반환하므로, 호출부에서
     * "잘못된 학교명" 처리를 결정할 수 있습니다.
     */
    public static Optional<University> from(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(university -> university.name().equalsIgnoreCase(value.trim()))
                .findFirst();
    }
}
