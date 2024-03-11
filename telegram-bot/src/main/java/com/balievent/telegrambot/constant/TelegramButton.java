package com.balievent.telegrambot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TelegramButton {
    LETS_GO("month_events_page", "Let's go! 🚀"),

    SHOW_MONTH_FULL("show_month_full", "Show more ➕"),
    SHOW_MONTH_LESS("show_month_less", "Show less ➖"),

    //Events page buttons
    FIRST_EVENTS_PAGE("first_events_page", "<< [1/%s]"),
    PREVIOUS_EVENTS_PAGE("previous_events_page", "< [%s/%s]"),
    NEXT_EVENTS_PAGE("next_events_page", "> [%s/%s]"),
    LAST_EVENTS_PAGE("last_events_page", ">> [%s/%s]"),

    //Month page buttons
    MONTH_EVENTS_PAGE("month_events_page", "Back to month 📅"),
    PREVIOUS_MONTH_PAGE("previous_month_page", "%s"),
    NEXT_MONTH_PAGE("next_month_page", "%s"),

    //Main menu buttons
    CHANGE_SEARCH_PARAMETERS("change_search_parameters", "Change search parameters 🔍"),
    SETTINGS_AND_HELP("settings_and_help", "Settings and help ⚙️"),
    CONTACT_US("contact_us", "Contact us 📧"),
    FAVORITE_EVENTS("favorite_events", "Favorites ❤️"),

    //change search parameters buttons
    EVENT_DATE_SELECTION("events_date_selection", "Select dates 📅"),
    SELECT_EVENT_LOCATIONS("event_locations_selection", "Select locations 🌍"),
    BACK_TO_MAIN_MENU("back_to_main_menu", "Back to main menu ⬅️"),
    SAVE_SEARCH_PARAMETERS("save_search_parameters", "Save search parameters ✅"),

    //choose event date buttons
    TODAY_EVENTS("today_events", "Today"),
    TOMORROW_EVENTS("tomorrow_events", "Tomorrow"),
    THIS_WEEK_EVENTS("this_week_events", "This week"),
    NEXT_WEEK_EVENTS("next_week_events", "Next week"),
    ON_THIS_WEEKEND_EVENTS("on_this_weekend_events", "On this weekend"),
    SHOW_ALL_EVENTS("show_all_events", "Show all event"),
    PICK_DATE_EVENTS("pick_date_events", "Pick date");

    private final String callbackData;
    private final String buttonText;

}
