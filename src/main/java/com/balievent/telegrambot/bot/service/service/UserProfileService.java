package com.balievent.telegrambot.bot.service.service;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.UserProfile;
import com.balievent.telegrambot.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfile getUserData(final Long chatId) {
        return userProfileRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));
    }

    @Transactional
    public UserProfile saveOrUpdateUserData(final Long chatId) {
        return userProfileRepository.findById(chatId)
            .orElseGet(() -> userProfileRepository.save(
                UserProfile.builder()
                    .id(chatId)
                    .build()
            ));
    }

    @Transactional
    public void updateLastBotMessageId(final Integer messageId, final Long chatId) {
        final UserProfile userProfile = getUserData(chatId);
        userProfile.setLastBotMessageId(messageId);
    }

    @Transactional
    public void saveUserMessageId(final Integer messageId, final Long chatId) {
        final UserProfile userProfile = getUserData(chatId);
        userProfile.setLastUserMessageId(messageId);
    }

    public List<Integer> getAllMessageIdsForDelete(final UserProfile userProfile) {
        return Stream.of(
                Optional.ofNullable(userProfile.getLastUserMessageId()).stream(),
                Optional.ofNullable(userProfile.getLastBotMessageId()).stream()
            )
            .flatMap(i -> i)
            .filter(Objects::nonNull)
            .toList();
    }

}
