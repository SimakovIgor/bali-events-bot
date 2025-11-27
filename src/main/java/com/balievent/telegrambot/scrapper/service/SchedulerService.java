package com.balievent.telegrambot.scrapper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final TheBeatBaliScrapperService theBeatBaliScrapperService;

    /**
     * Запускаем задачу каждые 10 часов.
     * Логика работы программы начинается отсюда
     */
    //@Scheduled(fixedDelay = 36_000_000L)
    public void scheduleTask() {

        log.info("Scheduled task started!");
        theBeatBaliScrapperService.process();

        final long randomDelay = getRandomDelay();
        try {
            Thread.sleep(randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while sleeping", e);
        }
    }

    /**
     * Генерируем случайную задержку от 1 до 2 минут.
     *
     * @return - случайная задержка
     */
    private long getRandomDelay() {
        return ThreadLocalRandom.current().nextLong(60_000) + 60_000;
    }
}
