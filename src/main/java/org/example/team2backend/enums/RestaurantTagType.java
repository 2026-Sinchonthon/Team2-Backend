package org.example.team2backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Optional;

/**
 * 맛집에 붙을 수 있는 태그. 이 다섯 가지로 고정합니다.
 *
 * <p>요청/응답 모두 {@link #label}(한글 문구) 그대로 주고받습니다.
 * JSON 변환은 {@link #label}을 기준으로 하므로, 프론트는 Enum 상수명을
 * 알 필요 없이 이 문구만 다루면 됩니다.
 */
public enum RestaurantTagType {

    SOLO_MEAL("혼밥"),
    FREE_PERIOD("공강"),
    DATE("데이트"),
    HANGOVER("해장"),
    MEAL_PROMISE("밥약");

    private final String label;

    RestaurantTagType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    /**
     * 한글 라벨을 RestaurantTagType으로 변환합니다.
     *
     * <p>서비스 계층에서 요청 바디의 태그 문구를 검증할 때 사용합니다.
     * 정해진 다섯 문구가 아니면 예외 대신 빈 Optional을 반환하므로,
     * 호출부에서 "잘못된 태그" 처리를 결정할 수 있습니다.
     */
    public static Optional<RestaurantTagType> from(String label) {
        if (label == null || label.isBlank()) {
            return Optional.empty();
        }

        String trimmed = label.trim();

        return Arrays.stream(values())
                .filter(type -> type.label.equals(trimmed))
                .findFirst();
    }

    /**
     * Jackson이 요청 바디의 한글 문구를 역직렬화할 때 사용합니다.
     *
     * <p>정해진 세 문구가 아니면 요청 단계에서 바로 400으로 이어지도록
     * 예외를 던집니다({@code GlobalExceptionHandler}가 처리).
     */
    @JsonCreator
    public static RestaurantTagType fromJson(String label) {
        return from(label)
                .orElseThrow(() -> new IllegalArgumentException(
                        "알 수 없는 태그입니다: " + label
                ));
    }
}
