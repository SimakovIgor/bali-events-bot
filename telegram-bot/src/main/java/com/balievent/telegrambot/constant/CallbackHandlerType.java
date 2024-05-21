package com.balievent.telegrambot.constant;

public enum CallbackHandlerType {
    EVENT_DATE_SELECTION,       // -> public class EventDateQuestionHandler()
    EVENT_LOCATIONS_SELECTION,  // -> public class EventLocationsQuestionHandler()
    MONTH_EVENTS_PAGE,          // -> public class MonthEventsHandler()
    EVENT_START_FILTER          // -> public class StartFilterHandler()

}
