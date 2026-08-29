package org.example.team2backend.dto;

import lombok.Getter;

import java.util.Map;

/**
 * 완료 추가/취소 응답.
 *
 * <p>갱신된 완료 수를 함께 내려주므로 프론트가 목록을 다시 조회하지 않아도
 * 화면을 갱신할 수 있습니다.
 */
@Getter
public class CheckResponse {

    private final Long restaurantId;

    /** 이 요청 후 현재 사용자의 완료 상태. 추가면 true, 취소면 false. */
    private final boolean checked;

    private final long checkCount;

    /** 학교별 완료 수. 완료가 없는 학교도 0으로 포함됩니다. */
    private final Map<String, Long> checkCountByUniversity;

    public CheckResponse(
            Long restaurantId,
            boolean checked,
            long checkCount,
            Map<String, Long> checkCountByUniversity
    ) {
        this.restaurantId = restaurantId;
        this.checked = checked;
        this.checkCount = checkCount;
        this.checkCountByUniversity = checkCountByUniversity;
    }
}
