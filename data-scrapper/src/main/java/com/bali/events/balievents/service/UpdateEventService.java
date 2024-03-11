package com.bali.events.balievents.service;

import com.bali.events.balievents.mapper.EventMapper;
import com.bali.events.balievents.model.EventDto;
import com.bali.events.balievents.model.entity.Event;
import com.bali.events.balievents.model.entity.Location;
import com.bali.events.balievents.repository.EventRepository;
import com.bali.events.balievents.repository.LocationRepository;
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
        //   преобразует EventDto в Event, чтобы можно было сохранить или обновить его в базе данных.
        final Location location = locationRepository.findById(eventDto.getLocationName())
            .orElse(locationRepository.save(eventMapper.toLocationEntity(eventDto)));

        final Event eventEntity = eventMapper.toEventEntity(eventDto);
        eventEntity.setLocation(location);

        eventRepository.findByExternalId(eventDto.getExternalId())
            // .ifPresentOrElse(entity -> { ... }, () -> { ... }) - это метод из Java Optional,
            // если результат поиска не пустой (существует событие с таким externalId), иначе (сохраняет новое событие).
            .ifPresentOrElse(entity -> {
                    // Внутри ifPresentOrElse выполняется логика обновления полей события (entity) данными
                    // если произошли изменения в полях то произойдет обновление записи в Базе данных
                    // после завершения транзакции (завершение этого метода)
                    entity.setEventName(eventEntity.getEventName());
                    entity.setStartDate(eventEntity.getStartDate());
                    entity.setEndDate(eventEntity.getEndDate());
                    entity.setEventUrl(eventEntity.getEventUrl());
                    entity.setImageUrl(eventEntity.getImageUrl());
                    entity.setServiceName(eventEntity.getServiceName());
                    entity.setCoordinates(eventEntity.getCoordinates());

                    // Если произошло изменение локации, устанавливаем новую локацию
                    if (!Objects.equals(entity.getLocation(), eventEntity.getLocation())) {
                        entity.setLocation(eventEntity.getLocation());
                    }
                },
                // если запись не найдена по поиску в findByExternalId(...) то в базе данных будет создана новая запись
                () -> eventRepository.save(eventEntity)
            );
    }
}
