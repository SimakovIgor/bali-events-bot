package com.bali.events.balievents.service;

import com.bali.events.balievents.mapper.EventMapper;
import com.bali.events.balievents.model.EventDto;
import com.bali.events.balievents.model.Scrapper;
import com.bali.events.balievents.model.entity.Event;
import com.bali.events.balievents.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheBeatBaliScrapperService implements ScrapperService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public String rootName() {
        return Scrapper.THE_BEAT_BALI.getRootName();
    }

    @Override
    public void process() {
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(rootName());

        try {
            Thread.sleep(25000);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        WebElement element = webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/div/div/section[3]/div/div[2]/div/div[2]/div/div/div[4]"));
        List<WebElement> childs = element.findElements(By.xpath("./child::*"));

        int i = 0;
        for (WebElement child : childs) {
            //  if (child.getAttribute("style").equals("display: none;")) {
            //                continue;
            //  }

            String externalId = child.getAttribute("id");
            String eventName = child.findElement(By.xpath("p/a/span[3]/span[1]")).getAttribute("innerHTML");
            String locationName = child.findElement(By.xpath("p/a/span[3]/span[2]")).getAttribute("data-location_name");
            String locationAddress = child.findElement(By.xpath("p/a/span[3]/span[2]")).getAttribute("data-location_address");
            String startDate = child.findElement(By.xpath("div/meta[2]")).getAttribute("content");
            String endDate = child.findElement(By.xpath("div/meta[3]")).getAttribute("content");
            String eventUrl = child.findElement(By.xpath("div/a")).getAttribute("href");
            String imageUrl = child.findElement(By.xpath("p/a/span[1]/span[1]")).getAttribute("data-img");

            EventDto eventDto = EventDto.builder()
                .externalId(externalId)
                .eventName(eventName)
                .locationName(locationName)
                .locationAddress(locationAddress)
                .startDate(startDate)
                .endDate(endDate)
                .eventUrl(eventUrl)
                .imageUrl(imageUrl)
                .serviceName(rootName())
                .build();

            Event eventEntity = eventMapper.toEventEntity(eventDto);
            eventRepository.save(eventEntity);
            log.info("processed {} / {}", i++, childs.size());
        }

        webDriver.quit();

        log.info("TheBeatBaliScrapperService.process() finished successfully.");
    }
}
