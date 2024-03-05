package com.balievent.telegrambot.service.storage;

import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.util.DateUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
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
            .pageCount(0) // количество страниц установим позже
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
        final String text = update.getMessage().getText(); // получаем текст сообщения из чата
        final UserData userData = calendarStore.getOrDefault(chatId, getDefaultUserData());
        final LocalDate localDate = isMonthChanged
                                    ? DateUtil.convertToDateTimeCalendarMonthChanged(text, userData.getCalendarDate())
                                    : DateUtil.parseSelectedDate(text, userData.getCalendarDate());

        if (calendarStore.get(chatId) != null) {
            calendarStore.get(chatId).setCalendarDate(localDate);
        } else {
            // если кликнули по "/17_02_2024" после старта программы, то попадем сюда
            calendarStore.put(chatId, getDefaultUserData());
            calendarStore.get(chatId).setCalendarDate(localDate);
        }

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

        // проверка на максимальную страницу
        if (userData.getPage().equals(userData.getPageCount())) {
            // устанавливаем первую страницу
            userData.setPage(0);
        } else {
            // увеличение страницы
            userData.setPage(userData.getPage() + 1);
        }
        return userData;
    }

    public UserData decrementPageAndGetUserData(final Long chatId) {
        final UserData userData = calendarStore.get(chatId);

        if (userData.getPage().equals(0)) {                 // проверка на минимально возможную страницу == 0
            userData.setPage(userData.getPageCount() + 1);    // устанавливаем на одну больше максимальной
        }
        userData.setPage(userData.getPage() - 1);
        return userData;
    }

    public UserData setPageAndGetUserData(final Long chatId, final int page) {
        final UserData userData = calendarStore.get(chatId);
        userData.setPage(page);
        return userData;
    }

    public void setPageCount(final Long chatId, final int pageMax) {
        UserData userData = calendarStore.get(chatId);
        if (userData == null) {
            userData = getDefaultUserData();
        }
        userData.setPageCount(pageMax);
    }

    public UserData saveMediaIdList(final List<Message> mediaIds, final Long chatId) {
        final List<Integer> idList = mediaIds.stream()
            .map(Message::getMessageId)
            .toList();

        final UserData userData = calendarStore.get(chatId);
        userData.setMediaIdList(idList);
        return userData;

    }

    public UserData saveLastDateSelectedMessageId(final Integer messageId, final Long chatId) {
        final UserData userData = calendarStore.get(chatId);
        userData.setLastDateSelectedMessageId(messageId);
        return userData;
    }

    /**
     * Возвращает список всех идентификаторов сообщений для удаления
     *
     * @param chatId - идентификатор чата
     * @return - список идентификаторов сообщений
     */
    public List<Integer> getAllMessageIdsForDelete(final Long chatId) {
        final UserData userData = getUserData(chatId);
        if (userData == null) {
            // Обработка ситуации, когда нет данных для данного chatId
            return List.of(); // или возвращаем пустой список или бросаем исключение
        }
        final Integer messageId = userData.getLastDateSelectedMessageId();

        final List<Integer> mediaIdList = userData.getMediaIdList();
        final List<Integer> messageIds = new ArrayList<>();
        if (mediaIdList != null) {
            messageIds.addAll(mediaIdList);
        }
        if (messageId != null) {
            messageIds.add(messageId);
        }
        return messageIds;
    }

}
