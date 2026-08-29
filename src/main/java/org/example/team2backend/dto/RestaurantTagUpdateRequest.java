package org.example.team2backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 맛집 태그 수정 요청.
 *
 * <p>부분 추가가 아니라 <b>전체 교체</b>입니다. 보낸 태그 목록이 그대로 최종 상태가 되고,
 * 빈 배열을 보내면 태그가 모두 지워집니다.
 */
@Getter
@NoArgsConstructor
public class RestaurantTagUpdateRequest {

    private List<String> tags;
}
