package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.scrapper.dto.EventDto;
import com.balievent.telegrambot.scrapper.dto.Scrapper;
import com.balievent.telegrambot.scrapper.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheBeatBaliScrapperService implements ScrapperService {

    public static final int MAX_MONTH_COUNT_WITH_EVENTS = 3;
    private static final By NEXT_MONTH_BUTTON = By.id("evcal_next");
    private static final By EVENT_CALENDAR_LIST = By.id("evcal_list");
    private static final String BY_TOPICS = "./child::*";

    private final UpdateEventService updateEventService;
    private final WebDriver webDriver;
    private final EventMapper eventMapper;

    @SuppressWarnings("PMD.ReturnCount")
    private static boolean isChildIdNotExists(final WebElement child) {
        if (child == null) {
            log.warn("Child element is null");
            return true;
        }

        try {
            final String externalId = child.getAttribute("id");
            if (!StringUtils.hasText(externalId)) {
                log.warn("Empty externalId, skipping this element");
                return true;
            }

            return false;
        } catch (StaleElementReferenceException e) {
            return true;
        }

    }

    @Override
    public String rootName() {
        return Scrapper.THE_BEAT_BALI.getRootName();
    }

    @Override
    public void process() {
        navigateToWebsite(webDriver);

        for (int i = 0; i < MAX_MONTH_COUNT_WITH_EVENTS; i++) {
            final List<WebElement> parsedEventList = getParsedEventList(webDriver);
            log.info("Month proceeding [{} / {}]... Month events size: {} ", i, MAX_MONTH_COUNT_WITH_EVENTS, parsedEventList.size() - 1);

            // Если объектов менее 2, то это только объект рекламы у которого нет ID
            if (parsedEventList.size() <= 2) {
                continue;
            }

            processEvents(parsedEventList);
            navigateToNextMonth(webDriver);
        }

        webDriver.quit();
    }

    private void processEvents(final List<WebElement> parsedEventList) {
        for (int i = 0; i < parsedEventList.size(); i++) {
            final WebElement event = parsedEventList.get(i);

            if (isChildIdNotExists(event)) {
                continue;
            }

            final EventDto eventDto = eventMapper.createEventDto(event, rootName());
            updateEventService.saveOrUpdate(eventDto);

            log.info("Event processed [{} / {}]", i, parsedEventList.size() - 1);
        }
    }

    private List<WebElement> getParsedEventList(final WebDriver webDriver) {
        return webDriver.findElement(EVENT_CALENDAR_LIST).findElements(By.xpath(BY_TOPICS));
    }

    private void navigateToNextMonth(final WebDriver webDriver) {
        log.info("Switching to the next month...");
        final WebElement nextButton = webDriver.findElement(NEXT_MONTH_BUTTON);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", nextButton);

        delay(15_000);
    }

    private void navigateToWebsite(final WebDriver webDriver) {
        webDriver.get(rootName());
        log.info("Web driver navigated to: " + rootName());
        delay(25_000);
    }

    private void delay(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
