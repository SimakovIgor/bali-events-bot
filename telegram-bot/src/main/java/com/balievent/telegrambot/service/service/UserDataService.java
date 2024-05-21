package com.balievent.telegrambot.service.service;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserDataService {
    private final UserDataRepository userDataRepository;

    private static UserData getDefaultUserData(final Long chatId) {
        return UserData.builder()
            .id(chatId)
            .searchEventDate(LocalDate.now())
            .currentEventPage(1)
            .totalEventPages(1)
            .build();
    }

    public UserData getUserData(final Long chatId) {
        return userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
    }

    @Transactional
    public UserData saveOrUpdateUserData(final Long chatId) {
        final Optional<UserData> userDataOptional = userDataRepository.findById(chatId);
        if (userDataOptional.isPresent()) {
            final UserData userData = userDataOptional.get();
            userData.setSearchEventDate(LocalDate.now());
            userData.setCurrentEventPage(1);
            userData.setTotalEventPages(1);
            return userData;
        }
        return userDataRepository.save(getDefaultUserData(chatId));
    }

    @Transactional
    public UserData addMonthAndGetUserData(final Long chatId) {
        final UserData userData = userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
        userData.setSearchEventDate(userData.getSearchEventDate().plusMonths(1));
        return userData;
    }

    @Transactional
    public UserData substractMonthAndGetUserData(final Long chatId) {
        final UserData userData = userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
        userData.setSearchEventDate(userData.getSearchEventDate().minusMonths(1));
        return userData;
    }

    @Transactional
    public void updateLastBotMessageId(final Integer messageId, final Long chatId) {
        final UserData userData = getUserData(chatId);
        userData.setLastBotMessageId(messageId);
    }

    @Transactional
    public void saveUserMessageId(final Integer messageId, final Long chatId) {
        final UserData userData = getUserData(chatId);
        userData.setLastUserMessageId(messageId);
    }

    public List<Integer> getAllMessageIdsForDelete(final UserData userData) {
        return Stream.of(
                Optional.ofNullable(userData.getLastUserMessageId()).stream(),
                Optional.ofNullable(userData.getLastBotMessageId()).stream(),
                userData.getMediaMessageIdList().stream()
            )
            .flatMap(i -> i)
            .filter(Objects::nonNull)
            .toList();
    }

}
