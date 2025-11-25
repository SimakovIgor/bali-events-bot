package com.balievent.telegrambot.scrapper.model;

public record RawEventHtml(
    String eventName,
    String time,
    String locationName,
    String eventUrl,
    String imageUrl
) {

}
