package com.balievent.telegrambot.scrapper.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Component
@ConfigurationProperties(prefix = "com.balievent.calendar.range")
public class CalendarRangeProperties {

    private LocalDate end;
}
