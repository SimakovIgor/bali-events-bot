package com.balievent.telegrambot.service.service;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
import com.balievent.telegrambot.repository.EventSearchCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventSearchCriteriaService {
    private final EventSearchCriteriaRepository eventSearchCriteriaRepository;

    @Transactional
    public void updateSearchCriteria(final Long chatId,
                                     final String searchCriteria) {
        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaRepository.findByChatId(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_999));
        eventSearchCriteria.setDate(searchCriteria);
    }

    @Transactional
    public void updateSearchCriteria(final Long chatId,
                                     final List<String> searchCriteria) {
        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaRepository.findByChatId(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_999));
        eventSearchCriteria.setLocationNameList(searchCriteria);
    }

    public EventSearchCriteria saveOrUpdateEventSearchCriteria(final Long chatId) {
        final Optional<EventSearchCriteria> userDataOptional = eventSearchCriteriaRepository.findByChatId(chatId);
        if (userDataOptional.isPresent()) {
            final EventSearchCriteria userData = userDataOptional.get();
            userData.setLocationNameList(null);
            userData.setDate("");
            return userData;
        }
        return eventSearchCriteriaRepository.save(EventSearchCriteria.builder()
            .chatId(chatId)
            .build());
    }
}
