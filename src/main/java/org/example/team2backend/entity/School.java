package org.example.team2backend.entity;

import java.util.Arrays;
import java.util.Optional;

public enum School {
    HONGIK,
    EWHA,
    YONSEI,
    SOGANG,
    MYONGJI;

    /**
     * 문자열을 School로 변환합니다. 대소문자를 구분하지 않습니다.
     *
     * <p>목록 조회의 school 파라미터처럼 외부 입력을 다룰 때 사용합니다.
     * 알 수 없는 값이면 예외 대신 빈 Optional을 반환하므로, 호출부에서
     * "잘못된 학교명" 처리를 결정할 수 있습니다.
     */
    public static Optional<School> from(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(school -> school.name().equalsIgnoreCase(value.trim()))
                .findFirst();
    }
}
