package com.balievent.telegrambot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TelegramButton {
    SHOW_MONTH_FULL("show_month_full", "Show more ➕"),
    SHOW_MONTH_LESS("show_month_less", "Show less ➖"),
    FIRST_EVENTS_PAGE("first_events_page", "<< [1/%s]"),
    PREVIOUS_EVENTS_PAGE("previous_events_page", "< [%s/%s]"),
    NEXT_EVENTS_PAGE("next_events_page", "> [%s/%s]"),
    LAST_EVENTS_PAGE("last_events_page", ">> [%s/%s]"),
    PREVIOUS_MONTH_PAGE("previous_month_page", "%s"),
    MONTH_EVENTS_PAGE("month_events_page", "Back to month 📅"),
    NEXT_MONTH_PAGE("next_month_page", "%s"),
    LETS_GO("month_events_page", "Let's go! 🚀");

    private final String callbackData;
    private final String buttonText;

}
