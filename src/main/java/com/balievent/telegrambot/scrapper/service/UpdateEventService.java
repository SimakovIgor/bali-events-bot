package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.repository.EventRepository;
import com.balievent.telegrambot.repository.LocationRepository;
import com.balievent.telegrambot.scrapper.mapper.EventMapper;
import com.balievent.telegrambot.scrapper.model.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UpdateEventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;

    @Transactional
    public void saveOrUpdate(final EventDto eventDto) {
        if (!StringUtils.hasText(eventDto.getLocationName())) {
            return;
        }
        final Location location = locationRepository.findById(eventDto.getLocationName())
            .orElse(locationRepository.save(eventMapper.toLocationEntity(eventDto)));

        final Event eventEntity = eventMapper.toEventEntity(eventDto);
        eventEntity.setLocation(location);

        eventRepository.findByExternalId(eventDto.getExternalId())
            .ifPresentOrElse(entity -> {
                    entity.setEventName(eventEntity.getEventName());
                    entity.setStartDate(eventEntity.getStartDate());
                    entity.setEndDate(eventEntity.getEndDate());
                    entity.setEventUrl(eventEntity.getEventUrl());
                    entity.setImageUrl(eventEntity.getImageUrl());
                    entity.setServiceName(eventEntity.getServiceName());
                    entity.setCoordinates(eventEntity.getCoordinates());

                    if (!Objects.equals(entity.getLocation(), eventEntity.getLocation())) {
                        entity.setLocation(eventEntity.getLocation());
                    }
                },
                () -> eventRepository.save(eventEntity)
            );
    }
}
