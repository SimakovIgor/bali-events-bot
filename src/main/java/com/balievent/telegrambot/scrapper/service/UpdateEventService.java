package com.balievent.telegrambot.scrapper.service;

import com.balievent.telegrambot.entity.Event;
import com.balievent.telegrambot.entity.Location;
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
            .orElse(locationRepository.save(eventMapper.toLocation(eventDto)));

        final Event event = eventMapper.toEvent(eventDto, location);

        eventRepository.findByEventUrl(eventDto.getEventUrl())
            .ifPresentOrElse(entity -> {
                    entity.setEventName(event.getEventName());
                    entity.setStartDateTime(event.getStartDateTime());
                    entity.setEventUrl(event.getEventUrl());
                    entity.setImageUrl(event.getImageUrl());

                    if (!Objects.equals(entity.getLocation(), event.getLocation())) {
                        entity.setLocation(event.getLocation());
                    }
                },
                () -> eventRepository.save(event)
            );
    }
}
