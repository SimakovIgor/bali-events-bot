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
    public EventSearchCriteria toggleLocationName(final Long chatId,
                                                  final String locationName,
                                                  final List<String> locationIds) {
        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaRepository.findByChatId(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_999));
        eventSearchCriteria.toggleLocationName(locationName, locationIds);
        return eventSearchCriteria;
    }

    @Transactional
    public EventSearchCriteria saveOrUpdateEventSearchCriteria(final Long chatId, final List<String> locationNameList) {
        final Optional<EventSearchCriteria> userDataOptional = eventSearchCriteriaRepository.findByChatId(chatId);
        if (userDataOptional.isPresent()) {
            final EventSearchCriteria userData = userDataOptional.get();
            userData.setLocationNameList(locationNameList); // сохраняем все локации и кнопки в event_search_criteria.location_name_list
            userData.setDate("");
            return userData;
        }
        return eventSearchCriteriaRepository.save(EventSearchCriteria.builder()
            .chatId(chatId)
            .build());
    }
}
