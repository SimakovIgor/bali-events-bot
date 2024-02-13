package com.bali.events.balievents.service;

import com.bali.events.balievents.mapper.EventMapper;
import com.bali.events.balievents.model.EventDto;
import com.bali.events.balievents.model.entity.Event;
import com.bali.events.balievents.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public void saveOrUpdate(final EventDto eventDto) {
        final Event eventEntity = eventMapper.toEventEntity(eventDto);
        eventRepository.findByExternalId(eventDto.getExternalId())
            .ifPresentOrElse(entity -> {
                    entity.setEventName(eventEntity.getEventName());
                    entity.setLocationName(eventEntity.getLocationName());
                    entity.setLocationAddress(eventEntity.getLocationAddress());
                    entity.setStartDate(eventEntity.getStartDate());
                    entity.setEndDate(eventEntity.getEndDate());
                    entity.setEventUrl(eventEntity.getEventUrl());
                    entity.setImageUrl(eventEntity.getImageUrl());
                    entity.setServiceName(eventEntity.getServiceName());
                    entity.setCoordinates(eventEntity.getCoordinates());
                },
                () -> eventRepository.save(eventEntity)
            );
    }
}
