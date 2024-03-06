package com.balievent.telegrambot.service.support;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.util.CommonUtil;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MessageBuilder {

    public static String buildBriefEventsMessage(final int currentPage, final List<Event> eventList) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(1 + i + Settings.PAGE_SIZE * (currentPage - 1)).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }

        return stringBuilder.toString();
    }
}
