package com.balievent.telegrambot.scrapper.client;

import com.balievent.telegrambot.scrapper.configuration.TheBeatBaliProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class TheBeatBaliParser {

    // Ищем именно nonce внутри tbe_calendar_ajax
    private static final Pattern PATTERN = Pattern.compile(
        "var\\s+tbe_calendar_ajax\\s*=\\s*\\{[^}]*\"nonce\":\"([^\"]+)\"",
        Pattern.DOTALL
    );
    private final TheBeatBaliProperties properties;

    @SneakyThrows
    public String loadCalendarNonce() {
        final var doc = Jsoup.connect(properties.getBaseUrl() + "/bali-events/")
            .userAgent("Mozilla/5.0")   // чтобы не казаться ботом
            .timeout(20_000)
            .get();

        final var script = doc.getElementById("tbe-calendar-js-extra");

        if (script == null) {
            throw new IllegalStateException("Script with id 'tbe-calendar-js-extra' not found");
        }

        final var scriptContent = script.html();
        return extractNonceFromScript(scriptContent);
    }

    private String extractNonceFromScript(String scriptContent) {
        final Matcher matcher = PATTERN.matcher(scriptContent);

        if (matcher.find()) {
            final var nonce = matcher.group(1);
            log.info("Nonce успешно найден: {}", nonce);
            return nonce;
        }

        log.info("Nonce не найден в скрипте tbe_calendar_ajax (длина скрипта = {} символов)", scriptContent.length());

        throw new IllegalStateException("Nonce not found in tbe_calendar_ajax script");
    }
}
