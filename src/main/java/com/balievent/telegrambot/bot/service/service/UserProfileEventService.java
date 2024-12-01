package com.balievent.telegrambot.bot.service.service;

import com.balievent.telegrambot.bot.constant.Settings;
import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserProfile;
import com.balievent.telegrambot.model.entity.UserProfileEvent;
import com.balievent.telegrambot.model.entity.UserProfileEventKey;
import com.balievent.telegrambot.repository.UserProfileEventRepository;
import com.balievent.telegrambot.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileEventService {
    private final UserProfileEventRepository userProfileEventRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void saveUserEvents(final List<Event> eventList, final Long chatId) {
        final UserProfile userProfile = userProfileRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_999));

        final List<UserProfileEvent> userProfileEventList = eventList.stream()
            .map(event -> UserProfileEvent.builder()
                .userProfileEventKey(UserProfileEventKey.builder()
                    .eventId(event.getId())
                    .userProfileId(chatId)
                    .build())
                .event(event)
                .userProfile(userProfile)
                .isViewed(false)
                .build())
            .toList();

        userProfileEventRepository.saveAll(userProfileEventList);
    }

    @Transactional
    public List<Event> findNextUnseenEvents(final Long chatId) {
        final Pageable pageable = Pageable.ofSize(Settings.SHOW_EVENTS_COUNT);
        final List<UserProfileEvent> userProfileEventList =
            userProfileEventRepository.findByIsViewedAndUserProfileEventKey_UserProfileId(false, chatId, pageable)
                .stream()
                .toList();

        for (final UserProfileEvent userProfileEvent : userProfileEventList) {
            userProfileEvent.setIsViewed(true);
        }

        return userProfileEventList.stream()
            .map(UserProfileEvent::getEvent)
            .toList();

    }

    public int findUnseenCount(final Long chatId) {
        return userProfileEventRepository.countByIsViewedAndUserProfileEventKey_UserProfileId(
            false, chatId);
    }

    @Transactional
    public void deleteUserEvents(final Long chatId) {
        userProfileEventRepository.deleteByUserProfileEventKey_UserProfileId(chatId);
    }
}
