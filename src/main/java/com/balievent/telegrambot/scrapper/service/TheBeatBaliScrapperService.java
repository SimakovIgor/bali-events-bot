package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.scrapper.dto.EventDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheBeatBaliScrapperService implements ScrapperService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String WP_ADMIN_ADMIN_AJAX_PHP = "https://thebeatbali.com/wp-admin/admin-ajax.php";
    public static final String COM_BALI_EVENTS = "https://thebeatbali.com/bali-events/";
    // Ищем именно nonce внутри tbe_calendar_ajax
    public static final Pattern PATTERN = Pattern.compile("var\\s+tbe_calendar_ajax\\s*=\\s*\\{[^}]*\"nonce\":\"([^\"]+)\"", Pattern.DOTALL);
    public final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper;

    @Override
    public void process() {
        final var nonce = loadCalendarNonce();
        fetchEventsRange(
            LocalDate.of(2025, 11, 1),
            LocalDate.of(2025, 11, 30),
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

    public String extractHtmlContent(String json) throws Exception {
        final var root = objectMapper.readTree(json);
        final var contentNode = root.path("data").path("content");

        if (contentNode.isMissingNode() || contentNode.isNull()) {
            throw new IllegalStateException("No data.content in response");
        }

        return contentNode.asText(); // здесь HTML с дивами tbe-date-event-item
    }

    /**
     * HTTP-запрос за событиями для конкретной даты и страницы
     */
    @SneakyThrows
    public String loadEventsJson(String date,
                                 String nonce,
                                 int page,
                                 boolean loadMore) {

        final var body = "action=" + URLEncoder.encode("tbe_get_date_events", UTF_8)
            + "&nonce=" + URLEncoder.encode(nonce, UTF_8)
            + "&date=" + URLEncoder.encode(date, UTF_8)
            + "&show_past=yes"
            + "&page=" + page
            + "&load_more=" + loadMore
            + "&filters%5Bcategory%5D="
            + "&filters%5Bvenue%5D="
            + "&filters%5Blocation%5D="
            + "&filters%5Bshow_past%5D=yes";

        final var request = HttpRequest.newBuilder()
            .uri(URI.create(WP_ADMIN_ADMIN_AJAX_PHP))
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .header("Accept", "application/json, */*")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Bad status: " + response.statusCode());
        }

        return response.body();
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

            final var json = loadEventsJson(dateStr, nonce, page, loadMore);
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

    @SneakyThrows
    public String loadCalendarNonce() {
        final var doc = Jsoup.connect(COM_BALI_EVENTS)
            .userAgent("Mozilla/5.0")   // чтобы не казаться ботом
            .timeout(10_000)
            .get();

        final var script = doc.getElementById("tbe-calendar-js-extra");

        if (script == null) {
            throw new IllegalStateException("Script with id 'tbe-calendar-js-extra' not found");
        }

        final var scriptContent = script.html();
        return extractNonceFromScript(scriptContent);
    }

    // простая обёртка под страницу событий из JSON
    private record EventsPage(String content, boolean hasMore, int count) {

    }
}
