package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.scrapper.client.TheBeatBaliClient;
import com.balievent.telegrambot.scrapper.client.TheBeatBaliParser;
import com.balievent.telegrambot.scrapper.configuration.CalendarRangeProperties;
import com.balievent.telegrambot.scrapper.dto.EventDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheBeatBaliScrapperService implements ScrapperService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ObjectMapper objectMapper;
    private final CalendarRangeProperties calendarRangeProperties;
    private final TheBeatBaliClient beatBaliClientClient;
    private final TheBeatBaliParser beatBaliParser;

    @Override
    public void process() {
        final var nonce = beatBaliParser.loadCalendarNonce();
        fetchEventsRange(
            calendarRangeProperties.getStart(),
            calendarRangeProperties.getEnd(),
            nonce
        );
    }

    /**
     * Обход диапазона дат, по каждой дате — загрузка всех страниц с событиями
     */
    @SneakyThrows
    public void fetchEventsRange(LocalDate start,
                                 LocalDate end,
                                 String nonce) {
        var current = start;

        while (!current.isAfter(end)) {
            final var dateStr = current.format(DATE_TIME_FORMATTER);
            log.info(">>> Загружаем события на дату: {}", dateStr);

            final var events = fetchEventsForDate(current, nonce);

            if (events.isEmpty()) {
                log.info("Нет событий на дату {}", dateStr);
                current = current.plusDays(1);
                randomDelay();
                continue;
            }

            log.info("Всего событий на дату {}: {}", dateStr, events.size());

            for (var e : events) {
                log.info(" - {} @ {}", e.getEventName(), e.getStartDate());
            }

            current = current.plusDays(1);
            randomDelay();
        }
    }

    @SneakyThrows
    private void randomDelay() {
        int delay = ThreadLocalRandom.current().nextInt(820, 1630);
        Thread.sleep(delay);
    }

    /**
     * Парсинг HTML-блока с событиями
     */
    public List<EventDto> parseEventsFromHtml(String html) {
        final var doc = Jsoup.parse(html);

        final var events = doc.select(".tbe-date-events-list > .tbe-date-event-item");

        final List<EventDto> result = new ArrayList<>();
        for (var event : events) {
            EventDto info = new EventDto();

            info.setEventName(event.selectFirst(".tbe-event-title") != null
                ? event.selectFirst(".tbe-event-title").text()
                : null);

            info.setStartDate(event.selectFirst(".tbe-event-duration") != null
                ? event.selectFirst(".tbe-event-duration").text()
                : null);

            info.setLocationName(event.selectFirst(".tbe-event-venue") != null
                ? event.selectFirst(".tbe-event-venue").text()
                : null);

            info.setEventUrl(event.selectFirst(".tbe-event-actions a") != null
                ? event.selectFirst(".tbe-event-actions a").attr("href")
                : null);

            info.setImageUrl(event.selectFirst(".tbe-event-featured-img") != null
                ? event.selectFirst(".tbe-event-featured-img").attr("src")
                : null);

            result.add(info);
        }

        return result;
    }

    /**
     * Разбор JSON-ответа: достаём HTML content + has_more + count
     */
    @SneakyThrows
    private EventsPage parseEventsPage(String json) {
        final JsonNode root = objectMapper.readTree(json);
        final JsonNode data = root.path("data");

        final String content = data.path("content").asText("");
        final boolean hasMore = data.path("has_more").asBoolean(false);
        final int count = data.path("count").asInt(0);

        return new EventsPage(content, hasMore, count);
    }

    /**
     * Загрузка всех событий за один день с учётом пагинации (page / has_more)
     */
    public List<EventDto> fetchEventsForDate(LocalDate date,
                                             String nonce) {
        final List<EventDto> allEvents = new ArrayList<>();

        final var dateStr = date.format(DATE_TIME_FORMATTER);
        int page = 1;
        boolean loadMore = false;

        while (true) {
            log.info("Загружаем события на дату {} страница {}", dateStr, page);

            final var json = beatBaliClientClient.loadEventsJson(dateStr, nonce, page, loadMore);
            final var eventsPage = parseEventsPage(json);

            if (eventsPage.content() == null || eventsPage.content().isBlank()) {
                log.info("Пустой контент для даты {} страницы {}, прекращаем загрузку", dateStr, page);
                break;
            }

            final var pageEvents = parseEventsFromHtml(eventsPage.content());
            if (pageEvents.isEmpty()) {
                log.info("Нет событий в HTML для даты {} страницы {}, прекращаем загрузку", dateStr, page);
                break;
            }

            log.info("Получено событий на дату {} страница {}: {}", dateStr, page, pageEvents.size());
            allEvents.addAll(pageEvents);

            if (!eventsPage.hasMore()) {
                log.info("has_more=false для даты {}, дальше страниц нет", dateStr);
                break;
            }

            page++;
            loadMore = true;
            randomDelay();
        }
        return allEvents;
    }

    // простая обёртка под страницу событий из JSON
    private record EventsPage(String content, boolean hasMore, int count) {

    }
}
