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
import java.util.Random;

import static com.bali.events.balievents.support.SeleniumUtils.getAttributeByClass;
import static com.bali.events.balievents.support.SeleniumUtils.getAttributeByXpath;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheBeatBaliScrapperService implements ScrapperService {

    private static final By BY_EVENT_WRAPPER = By.xpath("/html/body/div[1]/div[2]/div/div/main/article/div/div/section[3]/div/div[2]/div/div[2]/div/div/div[4]");

    private static final By BY_BUTTON_WRAPPER = By.xpath("/html/body/div[1]/div[2]/div/div/main/article/div/div/section[3]/div/div[2]/div/div[2]/div/div/div[1]");

    private static final By BY_BUTTON_ACCESS = By.xpath("/html/body/div[11]/div[2]/div[1]/div[2]/div[2]/button[2]/p");

    private static final String BY_CHILDS = "./child::*";
    private static final String BY_BUTTON = "evcal_next";
    private final UpdateEventService updateEventService;

    @Override
    public String rootName() {
        return Scrapper.THE_BEAT_BALI.getRootName();                            // здесь указывается сайт который мы будем считывать
    }

    @Override
    public void process() {                                                     // 1-й метод  для скачивания первого сайта (для другого сайта нужно писать другую логику аналогичного метода)
        final WebDriver webDriver = new ChromeDriver();                         // открываем браузер
        webDriver.get(rootName());                                              // Устанавливаем корневой домен скачиваемого сайта, и сайт появляется в открытом браузере (метод может зависнуть а может проскочить и тогда следующим шагом делаем задержку )

        try {
            Thread.sleep(25000);                                           // делаем задержку в 25 секунд, чтобы страница сайта загрузилась в наш открытый браузер
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        WebElement nextButton = webDriver.findElement(BY_BUTTON_WRAPPER)        // блок где находится кнопка следующего месяца
            .findElement(By.id(BY_BUTTON));                                     //  Кнопка следующего месяца

        Random random = new Random();
        int randomNumber = random.nextInt(8) + 1;                         // числа от 1 до 9 -> сколько месяцев информации скачиваем с сайта

        List<WebElement> childs = null;                                         // СОБЫТИЯ -> читаем список событий из загруженного правого блока
        for (int i = 0; i < randomNumber; i++) {                                // цикл на два года вперед

            childs = webDriver.findElement(BY_EVENT_WRAPPER)                    // получаем группу
                .findElements(By.xpath(BY_CHILDS));                             // набор топиков

            int ii = 1;
            if (childs.size() > 1) {                                            // если объектов менее 1 то это только объект рекламы у каторого нет ID
                for (WebElement child : childs) {                               // цикл по всем элементам полученного блока
                    try {
                        final String externalId = child.getAttribute("id");
                        if (externalId == null || externalId.isEmpty()) {       // пропустить и перейти к следующей записи
                            log.warn("Empty externalId, skipping this element");
                            continue;
                        }
                        final String eventName = getAttributeByClass(child, "evcal_event_title", "innerHTML");
                        final String locationName = getAttributeByClass(child, "evcal_event_title", "innerHTML");
                        final String locationAddress = getAttributeByClass(child, "event_location_attrs", "data-location_address");
                        final String startDate = getAttributeByXpath(child, "div/meta[2]", "content");
                        final String endDate = getAttributeByXpath(child, "div/meta[3]", "content");
                        final String eventUrl = getAttributeByXpath(child, "div/a", "href");
                        final String imageUrl = getAttributeByClass(child, "ev_ftImg", "data-img");
                        final String coordinates = getAttributeByClass(child, "evcal_location", "data-latlng");

                        final EventDto eventDto = EventDto.builder()                // создаем и заполняем структуру полей (назовем это 'DTO')
                            .externalId(externalId)
                            .eventName(eventName)
                            .locationName(locationName)
                            .locationAddress(locationAddress)
                            .startDate(startDate)
                            .endDate(endDate)
                            .eventUrl(eventUrl)
                            .imageUrl(imageUrl)
                            .serviceName(rootName())                                // в том числе и поле где будет хранится корневое имя сайта (Пример: 'https://thebeatbali.com/bali-events/') в таблица event поле service_name
                            .coordinates(coordinates)
                            .build();

                        updateEventService.saveOrUpdate(eventDto);                  //  Открываем транзакцию для изменения базы данных и передаем туда наше DTO для сохранения в базе данных
                    } catch (NoSuchElementException e) {
                        log.error("Error processing element: {}", e.getMessage());
                    }
                    log.info("processed {} / {}", ii++, childs.size());
                }
            } else {
                break; // выйти из цикла, если if (childs.size() < 2) {
            }
            try {
                // Этот сайт запрашивает у вас согласие на использование ваших данных
                // Кнопка появляется рандомно и блокирует все другие нажатия
                // но по ней можно попробовать кликнуть, даже если ее нет, мы просто попадаем в NoSuchElementException
                WebElement specialButton = webDriver.findElement(BY_BUTTON_ACCESS);
                specialButton.click();

                try {
                    Thread.sleep(2000);                                        // делаем задержку в 2 секунды, чтобы страница сайта загрузилась в нашем открытом браузере
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }

            } catch (NoSuchElementException e) {
                // Если кнопка не найдена, можно выполнить другие действия
            }
            nextButton.click();                                                     // переходим на следующий месяц

            try {
                Thread.sleep(25000);                                           // делаем задержку в 25 секунд, чтобы страница сайта загрузилась в наш открытый браузер
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

        }
        webDriver.quit();                                                            // закрываем окно
    }
}
