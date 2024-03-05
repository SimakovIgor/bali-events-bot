package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.contant.Settings;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.CallbackHandler;
import com.balievent.telegrambot.service.storage.UserDataStorage;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public abstract class AbstractPaginationHandler implements CallbackHandler {
    @Autowired
    protected EventService eventService;
    @Autowired
    protected UserDataStorage userDataStorage;

    protected String getBriefEventsForToday(final UserData userData) {
        final LocalDate calendarDate = userData.getCalendarDate();
        final int page = userData.getPage();
        final int pageMax = userData.getPageMax();
        final List<Event> eventList = eventService.findEvents(calendarDate, page, Settings.PAGE_SIZE);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            // При нумерации ивентов учитываем номер страницы
            stringBuilder.append(i + 1 + Settings.PAGE_SIZE * page).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }

        if (pageMax > 0) {
            stringBuilder.append("\nAll ")
                .append(page + 1)
                .append("\\")
                .append(pageMax + 1)
                .append(" ")
                .append( MyConstants.PAGES + "\n");
        }

        return stringBuilder.toString();
    }
}
