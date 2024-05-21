package com.balievent.telegrambot.service.service;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserDataService {
    private final UserDataRepository userDataRepository;

    public UserData getUserData(final Long chatId) {
        return userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
    }

    @Transactional
    public UserData saveOrUpdateUserData(final Long chatId) {
        return userDataRepository.findById(chatId)
            .orElseGet(() -> userDataRepository.save(
                UserData.builder()
                    .id(chatId)
                    .build()
            ));
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
                Optional.ofNullable(userData.getLastBotMessageId()).stream()
            )
            .flatMap(i -> i)
            .filter(Objects::nonNull)
            .toList();
    }

}
