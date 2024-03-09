package com.balievent.telegrambot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TelegramButton {
    SHOW_MONTH_FULL("show_month_full", "Show more ➕", false),
    SHOW_MONTH_LESS("show_month_less", "Show less ➖", false),
    FIRST_EVENTS_PAGE("first_events_page", "<< [1/%s]", true),
    PREVIOUS_EVENTS_PAGE("previous_events_page", "< [%s/%s]", true),
    NEXT_EVENTS_PAGE("next_events_page", "> [%s/%s]", true),
    LAST_EVENTS_PAGE("last_events_page", ">> [%s/%s]", true),
    PREVIOUS_MONTH_PAGE("previous_month_page", "%s <️", false),
    BACK_TO_MONTH_EVENT_PAGE("lets_go", "Back to month 📅", false),
    NEXT_MONTH_PAGE("next_month_page", ">️ %s", false),
    LETS_GO("lets_go", "Let's go! 🚀", false);

    private final String callbackData;
    private final String text;
    private final boolean includeMedia;

    public String formatDescription(final Object... args) {
        return String.format(text, args);
    }

}
