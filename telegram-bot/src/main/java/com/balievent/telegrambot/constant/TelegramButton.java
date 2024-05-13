package com.balievent.telegrambot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum TelegramButton {
    LETS_GO("month_events_page", "Let's go! 🚀", CallbackHandlerType.MONTH_EVENTS_PAGE),

    //Events page buttons
    FIRST_EVENTS_PAGE("first_events_page", "<< [1/%s]", CallbackHandlerType.EVENTS_PAGINATION),
    PREVIOUS_EVENTS_PAGE("previous_events_page", "< [%s/%s]", CallbackHandlerType.EVENTS_PAGINATION),
    NEXT_EVENTS_PAGE("next_events_page", "> [%s/%s]", CallbackHandlerType.EVENTS_PAGINATION),
    LAST_EVENTS_PAGE("last_events_page", ">> [%s/%s]", CallbackHandlerType.EVENTS_PAGINATION),

    //Month page buttons
    DAY_EVENT_PAGE("day_event_page", "Back to the list for the day 📅", CallbackHandlerType.DAY_EVENT_PAGE),
    MONTH_EVENTS_PAGE("month_events_page", "Back to month 📅", CallbackHandlerType.MONTH_EVENTS_PAGE),

    PREVIOUS_MONTH_PAGE("previous_month_page", "%s", CallbackHandlerType.MONTH_PAGINATION),
    NEXT_MONTH_PAGE("next_month_page", "%s", CallbackHandlerType.MONTH_PAGINATION),

    //Main menu buttons
    //    CHANGE_SEARCH_PARAMETERS("change_search_parameters", "Change search parameters 🔍"),
    //    SETTINGS_AND_HELP("settings_and_help", "Settings and help ⚙️"),
    //    CONTACT_US("contact_us", "Contact us 📧"),
    //    FAVORITE_EVENTS("favorite_events", "Favorites ❤️"),

    //change search parameters buttons
    // EVENT_DATE_SELECTION("events_date_selection", "Select dates 📅"),
    //    SELECT_EVENT_LOCATIONS("event_locations_selection", "Select locations 🌍"),
    //    BACK_TO_MAIN_MENU("back_to_main_menu", "Back to main menu ⬅️"),
    //    SAVE_SEARCH_PARAMETERS("save_search_parameters", "Save search parameters ✅"),

    //choose event date buttons
    SEARCH_TODAY_EVENTS("search_today_events", "Today", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_TOMORROW_EVENTS("search_tomorrow_events", "Tomorrow", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_THIS_WEEK_EVENTS("search_this_week_events", "This week", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_NEXT_WEEK_EVENTS("search_next_week_events", "Next week", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_ON_THIS_WEEKEND_EVENTS("search_on_this_weekend_events", "On this weekend", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_SHOW_ALL_EVENTS("search_show_all_events", "Show all this month", CallbackHandlerType.EVENT_DATE_SELECTION),
    //    SEARCH_PICK_DATE_EVENTS("search_pick_date_events", "Pick date", CallbackHandlerType.EVENT_DATE_SELECTION);

    EVENT_LOCATIONS_NEXT("month_events_page", "Next ➡️", CallbackHandlerType.MONTH_EVENTS_PAGE),
    EVENT_START_FILTER("event_start_filter", "Filter", CallbackHandlerType.EVENT_START_FILTER),

    SELECT_ALL_LOCATIONS("select_all_locations", "Select all", CallbackHandlerType.EVENT_LOCATIONS_SELECTION),
    DESELECT_ALL_LOCATIONS("deselect_all_locations", "Deselect all", CallbackHandlerType.EVENT_LOCATIONS_SELECTION);

    private final String callbackData;
    private final String buttonText;
    private final CallbackHandlerType callbackHandlerType;

    public static TelegramButton findByCallbackData(final String callbackData) {
        return Arrays.stream(TelegramButton.values())
            .filter(telegramButton -> callbackData.equals(telegramButton.getCallbackData()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unexpected value: " + callbackData));
    }
}
