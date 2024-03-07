package com.balievent.telegrambot.service.storage;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.repository.UserDataRepository;
import com.balievent.telegrambot.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDataService {
    private final UserDataRepository userDataRepository;

    /**
     * Updates the calendar store with the selected date or the changed calendar month.
     *
     * @param update         - the update event from Telegram
     * @param isMonthChanged - a flag indicating whether the calendar month has changed
     * @return the updated local date
     */
    @Transactional
    public UserData updateDataStore(final Update update, final boolean isMonthChanged) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();

        final UserData userData = getUserData(chatId);

        final LocalDate localDate = isMonthChanged
                                    ? DateUtil.convertToDateTimeCalendarMonthChanged(text, userData.getCalendarDate())
                                    : DateUtil.parseSelectedDate(text, userData.getCalendarDate());

        userData.setCalendarDate(localDate);

        return userData;
    }

    public UserData getUserData(final Long chatId) {
        return userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
    }

    public UserData incrementPageAndGetUserData(final Long chatId) {
        final UserData userData = getUserData(chatId);
        userData.setCurrentPage(userData.getCurrentPage() + 1);
        return userData;
    }

    public UserData decrementPageAndGetUserData(final Long chatId) {
        final UserData userData = userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
        userData.setCurrentPage(userData.getCurrentPage() - 1);
        return userData;
    }

    @Transactional
    public UserData setCurrentPage(final Long chatId, final int page) {
        final UserData userData = getUserData(chatId);
        userData.setCurrentPage(page);
        return userData;
    }

    @Transactional
    public void setPageCount(final Long chatId, final int pageMax) {
        final UserData userData = getUserData(chatId);
        userData.setPageCount(pageMax);
    }

    @Transactional
    public void saveMediaIdList(final List<Message> mediaIds, final Long chatId) {
        final List<Integer> idList = mediaIds.stream()
            .map(Message::getMessageId)
            .toList();

        final UserData userData = getUserData(chatId);
        userData.setSentMessageIdList(idList);

    }

    @Transactional
    public void saveLastDateSelectedMessageId(final Integer messageId, final Long chatId) {
        final UserData userData = getUserData(chatId);
        userData.setLastDateSelectedMessageId(messageId);
    }

    /**
     * Возвращает список всех идентификаторов сообщений для удаления
     *
     * @param chatId - идентификатор чата
     * @return - список идентификаторов сообщений
     */
    public List<Integer> getAllMessageIdsForDelete(final Long chatId) {
        final UserData userData = getUserData(chatId);
        final Integer messageId = userData.getLastDateSelectedMessageId();
        final List<Integer> mediaIdList = userData.getSentMessageIdList();

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
