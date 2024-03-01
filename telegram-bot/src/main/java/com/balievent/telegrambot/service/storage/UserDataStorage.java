package com.balievent.telegrambot.service.storage;

import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.util.DateUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserDataStorage {
    private final Map<Long, UserData> calendarStore = new ConcurrentHashMap<>(100);

    private static UserData getDefaultUserData() {
        return UserData.builder()
            .calendarDate(LocalDate.now())
            .page(0)
            .build();
    }

    /**
     * Updates the calendar store with the selected date or the changed calendar month.
     *
     * @param update         - the update event from Telegram
     * @param isMonthChanged - a flag indicating whether the calendar month has changed
     * @return the updated local date
     */
    private LocalDate updateDataStore(final Update update, final boolean isMonthChanged) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();
        final UserData userData = calendarStore.getOrDefault(chatId, getDefaultUserData());
        final LocalDate localDate = isMonthChanged
                                    ? DateUtil.convertToDateTimeCalendarMonthChanged(text, userData.getCalendarDate())
                                    : DateUtil.parseSelectedDate(text, userData.getCalendarDate());

        calendarStore.get(chatId).setCalendarDate(localDate);

        return localDate;
    }

    public LocalDate updateWithSelectedDate(final Update update) {
        return updateDataStore(update, false);
    }

    public LocalDate updateWithCalendarMonthChanged(final Update update) {
        return updateDataStore(update, true);
    }

    public LocalDate reset(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, getDefaultUserData());
        return getUserData(chatId).getCalendarDate();
    }

    public LocalDate getCalendarDate(final Long chatId) {
        return calendarStore.get(chatId).getCalendarDate();
    }

    public UserData getUserData(final Long chatId) {
        return calendarStore.get(chatId);
    }

    public UserData incrementPageAndGetUserData(final Long chatId) {
        final UserData userData = calendarStore.get(chatId);
        userData.setPage(userData.getPage() + 1);
        return userData;
    }

    public UserData decrementPageAndGetUserData(final Long chatId) {
        final UserData userData = calendarStore.get(chatId);
        userData.setPage(userData.getPage() - 1);
        return userData;
    }

    public UserData saveMediaIdList(final List<Message> mediaIds, final Long chatId) {
        final List<Integer> idList = mediaIds.stream()
            .map(Message::getMessageId)
            .toList();

        final UserData userData = calendarStore.get(chatId);
        userData.setMediaIdList(idList);
        return userData;

    }

}
