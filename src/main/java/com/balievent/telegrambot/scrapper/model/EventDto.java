package com.balievent.telegrambot.scrapper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    private String externalId;
    private String eventName;
    private String locationName; // unused
    private String locationAddress; // unused
    private String startDate;
    private String endDate; // unused
    private String eventUrl;
    private String imageUrl;
    private String serviceName; // unused
    private String coordinates; // unused
}
