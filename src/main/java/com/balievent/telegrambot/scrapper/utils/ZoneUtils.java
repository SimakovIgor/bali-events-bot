package com.balievent.telegrambot.scrapper.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.annotation.Nullable;

@UtilityClass
public final class ZoneUtils {

    public static final ZoneId BALI_ZONE = ZoneId.of("Asia/Makassar");

    /**
     * Текущее локальное время Бали как LocalDateTime
     */
    public static LocalDateTime nowInBali() {
        return LocalDateTime.now(BALI_ZONE);
    }

    /**
     * Преобразование LocalDateTime к LocalDateTime в таймзоне Бали.
     * Важно: LocalDateTime не хранит смещение, поэтому мы трактуем входное время как локальное,
     * переносим его через ZonedDateTime.
     */
    @Nullable
    public static LocalDateTime toBali(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(BALI_ZONE)
            .toLocalDateTime();
    }

    /**
     * Парсит строку в LocalDateTime и трактует её как локальную для Бали
     */
    @Nullable
    public static LocalDateTime parseToBaliLocal(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString);
    }
}

