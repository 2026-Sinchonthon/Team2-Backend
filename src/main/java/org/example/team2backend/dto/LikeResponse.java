package org.example.team2backend.dto;

import lombok.Getter;

import java.util.Map;

/**
 * 좋아요 추가/취소 응답.
 *
 * <p>갱신된 좋아요 수를 함께 내려주므로 프론트가 목록을 다시 조회하지 않아도
 * 화면을 갱신할 수 있습니다.
 */
@Getter
public class LikeResponse {

    private final Long restaurantId;

    /** 이 요청 후 현재 사용자의 좋아요 상태. 추가면 true, 취소면 false. */
    private final boolean liked;

    private final long likeCount;

    /** 학교별 좋아요 수. 좋아요가 없는 학교도 0으로 포함됩니다. */
    private final Map<String, Long> likeCountByUniversity;

    public LikeResponse(
            Long restaurantId,
            boolean liked,
            long likeCount,
            Map<String, Long> likeCountByUniversity
    ) {
        this.restaurantId = restaurantId;
        this.liked = liked;
        this.likeCount = likeCount;
        this.likeCountByUniversity = likeCountByUniversity;
    }
}
