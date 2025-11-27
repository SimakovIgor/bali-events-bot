package com.balievent.telegrambot.scrapper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    private String eventName;
    private String locationName; // unused
    private String locationAddress; // unused
    private String time;
    private String endDate; // unused
    private String eventUrl;
    private String imageUrl;
    private String serviceName; // unused
    private LocalDate date;
}
