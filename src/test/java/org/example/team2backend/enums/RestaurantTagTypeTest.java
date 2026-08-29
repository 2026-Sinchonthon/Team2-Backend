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

        assertThat(json).isEqualTo("\"밥약\"");
    }

    @Test
    void deserializesFromKoreanLabel() throws Exception {
        RestaurantTagType tag = objectMapper.readValue("\"데이트\"", RestaurantTagType.class);

        assertThat(tag).isEqualTo(RestaurantTagType.DATE);
    }

    @Test
    void supportsAllLabels() throws Exception {
        assertThat(objectMapper.readValue("\"혼밥\"", RestaurantTagType.class))
                .isEqualTo(RestaurantTagType.SOLO_MEAL);
        assertThat(objectMapper.readValue("\"공강\"", RestaurantTagType.class))
                .isEqualTo(RestaurantTagType.FREE_PERIOD);
        assertThat(objectMapper.readValue("\"데이트\"", RestaurantTagType.class))
                .isEqualTo(RestaurantTagType.DATE);
        assertThat(objectMapper.readValue("\"해장\"", RestaurantTagType.class))
                .isEqualTo(RestaurantTagType.HANGOVER);
        assertThat(objectMapper.readValue("\"밥약\"", RestaurantTagType.class))
                .isEqualTo(RestaurantTagType.MEAL_PROMISE);
    }

    @Test
    void rejectsUnknownLabel() {
        assertThatThrownBy(() -> objectMapper.readValue("\"없는 태그\"", RestaurantTagType.class))
                .isInstanceOf(Exception.class);
    }
}
