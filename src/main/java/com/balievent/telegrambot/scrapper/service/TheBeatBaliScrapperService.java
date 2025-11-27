package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.scrapper.client.TheBeatBaliClient;
import com.balievent.telegrambot.scrapper.client.TheBeatBaliParser;
import com.balievent.telegrambot.scrapper.configuration.CalendarRangeProperties;
import com.balievent.telegrambot.scrapper.model.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

    private final CalendarRangeProperties calendarRangeProperties;
    private final TheBeatBaliClient beatBaliClientClient;
    private final TheBeatBaliParser beatBaliParser;
    private final UpdateEventService updateEventService;

    @Override
    public void process() {
        final var nonce = beatBaliParser.loadCalendarNonce();
        fetchEventsRange(LocalDate.now(), calendarRangeProperties.getEnd(), nonce);
    }

    /**
     * Обход диапазона дат, по каждой дате — загрузка всех страниц с событиями
     */
    private void fetchEventsRange(final LocalDate start,
                                  final LocalDate end,
                                  final String nonce) {
        var current = start;

        while (!current.isAfter(end)) {
            final var date = current.format(DATE_TIME_FORMATTER);
            log.info(">>> Загружаем события на дату: {}", date);

            final var events = fetchEventsForDate(current, nonce);

            if (events.isEmpty()) {
                log.info("Нет событий на дату {}", date);
            } else {
                log.info("Получено событий {} шт", events.size());
                events.forEach(e -> log.info(" - {} @ {}", e.getEventName(), e.getTime()));

                // todo: save to db
            }

            randomDelay();
            current = current.plusDays(1);
        }
    }

    @SneakyThrows
    private void randomDelay() {
        final var delay = ThreadLocalRandom.current().nextInt(820, 1630);
        Thread.sleep(delay);
    }

    /**
     * Загрузка всех событий за один день с учётом пагинации (page / has_more)
     */
    public List<EventDto> fetchEventsForDate(final LocalDate date,
                                             final String nonce) {
        final List<EventDto> allEvents = new ArrayList<>();
        int page = 1;
        boolean loadMore = false;

        final var dateStr = date.format(DATE_TIME_FORMATTER);

        while (true) {
            log.info("Загружаем события на дату {} страница {}", dateStr, page);

            final var json = beatBaliClientClient.loadEventsJson(dateStr, nonce, page, loadMore);
            final var eventsPage = beatBaliParser.parseEventsPage(json);

            if (eventsPage.content() == null || eventsPage.content().isBlank()) {
                log.info("Пустой контент для даты {} страницы {}, прекращаем загрузку", dateStr, page);
                break;
            }

            final var pageEvents = beatBaliParser.parseEventsFromHtml(eventsPage.content(), date);
            if (pageEvents.isEmpty()) {
                log.info("Нет событий в HTML для даты {} страницы {}, прекращаем загрузку", dateStr, page);
                break;
            }

            log.info("Получено событий на дату {} страница {}: {}", dateStr, page, pageEvents.size());
            allEvents.addAll(pageEvents);
            allEvents.forEach(updateEventService::saveOrUpdate);

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
}
