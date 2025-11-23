package com.balievent.telegrambot.scrapper.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import javax.annotation.Nullable;

@UtilityClass
public final class ZoneUtils {

    public static final ZoneId BALI_ZONE = ZoneId.of("Asia/Makassar");
    public static final ZoneOffset BALI_OFFSET = BALI_ZONE.getRules().getOffset(Instant.now());

    public static OffsetDateTime nowInBali() {
        return OffsetDateTime.now(BALI_ZONE);
    }

    @Nullable
    public static OffsetDateTime toBaliOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return OffsetDateTime.of(localDateTime, BALI_OFFSET);
    }

    @Nullable
    public static OffsetDateTime parseToBaliOffset(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        LocalDateTime ldt = LocalDateTime.parse(dateTimeString);
        return OffsetDateTime.of(ldt, BALI_OFFSET);
    }
}

