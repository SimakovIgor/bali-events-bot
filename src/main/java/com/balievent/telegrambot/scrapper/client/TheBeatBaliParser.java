package com.balievent.telegrambot.scrapper.client;

import com.balievent.telegrambot.scrapper.configuration.TheBeatBaliProperties;
import com.balievent.telegrambot.scrapper.mapper.EventMapper;
import com.balievent.telegrambot.scrapper.model.EventDto;
import com.balievent.telegrambot.scrapper.model.EventsPage;
import com.balievent.telegrambot.scrapper.model.RawEventHtml;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
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
    private final ObjectMapper objectMapper;
    private final TheBeatBaliProperties properties;
    private final EventMapper eventMapper;

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

    private String extractNonceFromScript(final String scriptContent) {
        final Matcher matcher = PATTERN.matcher(scriptContent);

        if (matcher.find()) {
            final var nonce = matcher.group(1);
            log.info("Nonce успешно найден: {}", nonce);
            return nonce;
        }

        log.info("Nonce не найден в скрипте tbe_calendar_ajax (длина скрипта = {} символов)", scriptContent.length());

        throw new IllegalStateException("Nonce not found in tbe_calendar_ajax script");
    }

    public List<EventDto> parseEventsFromHtml(final String html,
                                              final LocalDate date) {
        final var doc = Jsoup.parse(html);
        final var events = doc.select(".tbe-date-events-list > .tbe-date-event-item");
        return events.stream()
            .map(e -> new RawEventHtml(
                getText(e, ".tbe-event-title"),
                getText(e, ".tbe-event-duration"),
                getText(e, ".tbe-event-venue"),
                getAttr(e, ".tbe-event-actions a", "href"),
                getAttr(e, ".tbe-event-featured-img", "src")
            ))
            .map(raw -> eventMapper.rawToDto(raw, date))
            .toList();
    }

    private String getText(final Element el,
                           final String selector) {
        final var found = el.selectFirst(selector);
        return found != null
            ? found.text()
            : null;
    }

    private String getAttr(final Element el,
                           final String selector,
                           final String attr) {
        final var found = el.selectFirst(selector);
        return found != null
            ? found.attr(attr)
            : null;
    }

    /**
     * Разбор JSON-ответа: достаём HTML content + has_more + count
     */
    @SneakyThrows
    public EventsPage parseEventsPage(final String json) {
        final var root = objectMapper.readTree(json);
        final var data = root.path("data");

        return new EventsPage(
            data.path("content").asText(""),
            data.path("has_more").asBoolean(false),
            data.path("count").asInt(0)
        );
    }
}
