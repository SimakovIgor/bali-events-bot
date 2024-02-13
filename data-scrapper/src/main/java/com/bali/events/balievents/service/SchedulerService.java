package com.bali.events.balievents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final TheBeatBaliScrapperService theBeatBaliScrapperService;

    @Scheduled(fixedDelay = 30000)
    public void scheduleTask() {
        log.info("Scheduled task started!");
        theBeatBaliScrapperService.process();
    }
}
