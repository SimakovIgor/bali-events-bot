package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.repository.EventRepository;
import com.balievent.telegrambot.repository.LocationRepository;
import com.balievent.telegrambot.scrapper.dto.EventDto;
import com.balievent.telegrambot.scrapper.mapper.EventMapper;
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
            .orElse(locationRepository.save(eventMapper.toLocation(eventDto)));

        final Event event = eventMapper.toEvent(eventDto, location);

        eventRepository.findByExternalId(eventDto.getExternalId())
            .ifPresentOrElse(entity -> {
                    entity.setEventName(event.getEventName());
                    entity.setStartDate(event.getStartDate());
                    entity.setEndDate(event.getEndDate());
                    entity.setEventUrl(event.getEventUrl());
                    entity.setImageUrl(event.getImageUrl());
                    entity.setServiceName(event.getServiceName());
                    entity.setCoordinates(event.getCoordinates());

                    if (!Objects.equals(entity.getLocation(), event.getLocation())) {
                        entity.setLocation(event.getLocation());
                    }
                },
                () -> eventRepository.save(event)
            );
    }
}
