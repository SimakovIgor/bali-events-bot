package com.bali.events.balievents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final TheBeatBaliScrapperService theBeatBaliScrapperService;

    @Scheduled(fixedDelay = 30000)
    public void scheduleTask() {

        log.info("Scheduled task started!"); // логика работы программы начинается отсюда !!!!
        theBeatBaliScrapperService.process();

        // Генерируем случайную задержку от 1 до 2 минут
        final long randomDelay = getRandomDelay();
        try {
            Thread.sleep(randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while sleeping", e);
        }
    }

    private long getRandomDelay() {
        return ThreadLocalRandom.current().nextLong(60000) + 60000;
    }
}
