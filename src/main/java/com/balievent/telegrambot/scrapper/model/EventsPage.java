package com.balievent.telegrambot.scrapper.model;

// простая обёртка под страницу событий из JSON
public record EventsPage(String content, boolean hasMore, int count) {

}
