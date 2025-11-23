package com.balievent.telegrambot.scrapper.model;

public record RawEventHtml(
    String eventName,
    String startDate,
    String locationName,
    String eventUrl,
    String imageUrl
) {

}
