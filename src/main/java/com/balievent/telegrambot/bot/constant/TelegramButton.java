package com.balievent.telegrambot.bot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum TelegramButton {
    LETS_GO("lets_go", "Let's go! 🚀", CallbackHandlerType.EVENT_START_FILTER),

    MONTH_EVENTS_PAGE("month_events_page", "Back to month 📅", CallbackHandlerType.SEND_EVENT_LIST_SERVICE),

    // filter choose event date buttons:
    SEARCH_TODAY_EVENTS("search_today_events", "Today", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_TOMORROW_EVENTS("search_tomorrow_events", "Tomorrow", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_THIS_WEEK_EVENTS("search_this_week_events", "This week", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_NEXT_WEEK_EVENTS("search_next_week_events", "Next week", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_ON_THIS_WEEKEND_EVENTS("search_on_this_weekend_events", "On this weekend", CallbackHandlerType.EVENT_DATE_SELECTION),
    SEARCH_SHOW_ALL_EVENTS("search_show_all_events", "Show all this month", CallbackHandlerType.EVENT_DATE_SELECTION),

    // Detailed event view buttons:
    DETAILED_EVENT_VIEW_ON_MAP("-", "On map 🔎", null),
    DETAILED_EVENT_VIEW_BUY_TICKET("-", "Buy Tickets Now! 💸", null),

    //===
    SHOW_MORE_EVENT_LIST("show_more_event_list", "Show %s more 😼", CallbackHandlerType.SHOW_MORE_EVENT_LIST_SERVICE),
    FILTER_EVENT_LOCATIONS_COMPLETE("month_events_page", "Complete ✅️", CallbackHandlerType.SEND_EVENT_LIST_SERVICE),

    SELECT_ALL_LOCATIONS("select_all_locations", "Select all", CallbackHandlerType.EVENT_LOCATIONS_SELECTION),
    DESELECT_ALL_LOCATIONS("deselect_all_locations", "Deselect all 🔄", CallbackHandlerType.EVENT_LOCATIONS_SELECTION);

    private final String callbackData;
    private final String buttonText;
    private final CallbackHandlerType callbackHandlerType;

    public static TelegramButton findByCallbackData(final String callbackData) {
        return Arrays.stream(values())
            .filter(telegramButton -> callbackData.equals(telegramButton.getCallbackData()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unexpected value: " + callbackData));
    }
}
