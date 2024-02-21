package com.bali.events.balievents.service;

import com.bali.events.balievents.model.EventDto;
import com.bali.events.balievents.model.Scrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bali.events.balievents.support.SeleniumUtils.getAttributeByClass;
import static com.bali.events.balievents.support.SeleniumUtils.getAttributeByXpath;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheBeatBaliScrapperService implements ScrapperService {

    private static final By BY_EVENT_WRAPPER = By.xpath("/html/body/div[1]/div[2]/div/div/main/article/div/div/section[3]/div/div[2]/div/div[2]/div/div/div[4]");
    private static final String BY_CHILDS = "./child::*";
    private final UpdateEventService updateEventService;

    @Override
    public String rootName() {
        return Scrapper.THE_BEAT_BALI.getRootName();
    }

    @Override
    public void process() {
        final WebDriver webDriver = new ChromeDriver();
        webDriver.get(rootName());

        try {
            Thread.sleep(25000);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        final List<WebElement> childs = webDriver.findElement(BY_EVENT_WRAPPER)
            .findElements(By.xpath(BY_CHILDS));

        int i = 1;
        for (WebElement child : childs) {
            try {
                final String externalId = child.getAttribute("id");
                final String eventName = getAttributeByClass(child, "evcal_event_title", "innerHTML");
                final String locationName = getAttributeByClass(child, "evcal_event_title", "innerHTML");
                final String locationAddress = getAttributeByClass(child, "event_location_attrs", "data-location_address");
                final String startDate = getAttributeByXpath(child, "div/meta[2]", "content");
                final String endDate = getAttributeByXpath(child, "div/meta[3]", "content");
                final String eventUrl = getAttributeByXpath(child, "div/a", "href");
                final String imageUrl = getAttributeByClass(child, "ev_ftImg", "data-img");
                final String coordinates = getAttributeByClass(child, "evcal_location", "data-latlng");

                final EventDto eventDto = EventDto.builder()
                    .externalId(externalId)
                    .eventName(eventName)
                    .locationName(locationName)
                    .locationAddress(locationAddress)
                    .startDate(startDate)
                    .endDate(endDate)
                    .eventUrl(eventUrl)
                    .imageUrl(imageUrl)
                    .serviceName(rootName())
                    .coordinates(coordinates)
                    .build();

                updateEventService.saveOrUpdate(eventDto);
            } catch (NoSuchElementException e) {
                log.error("Error processing element: {}", e.getMessage());
            }
            log.info("processed {} / {}", i++, childs.size());
        }

        webDriver.quit();
    }

}
