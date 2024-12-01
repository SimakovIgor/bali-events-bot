package com.balievent.telegrambot.scrapper.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Scrapper {
    THE_BEAT_BALI("https://thebeatbali.com/bali-events/");

    private final String rootName;

}
