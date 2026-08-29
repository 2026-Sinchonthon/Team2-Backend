package org.example.team2backend.enums;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantTagTypeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesToKoreanLabel() throws Exception {
        String json = objectMapper.writeValueAsString(RestaurantTagType.MEAL_PROMISE);

        assertThat(json).isEqualTo("\"밥약하기 좋은 맛집\"");
    }

    @Test
    void deserializesFromKoreanLabel() throws Exception {
        RestaurantTagType tag = objectMapper.readValue("\"가성비 좋은 맛집\"", RestaurantTagType.class);

        assertThat(tag).isEqualTo(RestaurantTagType.COST_EFFECTIVE);
    }

    @Test
    void rejectsUnknownLabel() {
        assertThatThrownBy(() -> objectMapper.readValue("\"없는 태그\"", RestaurantTagType.class))
                .isInstanceOf(Exception.class);
    }
}
