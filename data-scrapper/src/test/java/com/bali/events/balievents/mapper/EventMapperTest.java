package com.bali.events.balievents.mapper;

import com.bali.events.balievents.mapper.impl.EventMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventMapperTest {

    private final EventMapper eventMapper = new EventMapperImpl();

    static Stream<Arguments> prepareToLocalDateTimeSuccess() {
        return Stream.of(
            Arguments.of("2024-1-2T11:50+08:00", LocalDateTime.of(2024, 1, 2, 11, 50)),
            Arguments.of("2024-12-13T22:30+08:00", LocalDateTime.of(2024, 12, 13, 22, 30))
        );
    }

    @ParameterizedTest
    @MethodSource("prepareToLocalDateTimeSuccess")
    void toLocalDateTimeSuccess(final String fromDateTime, final LocalDateTime expectedDateTime) {
        assertThat(eventMapper.toLocalDateTime(fromDateTime))
            .isEqualTo(expectedDateTime);
    }
}
