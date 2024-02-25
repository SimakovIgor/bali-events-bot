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
    private final EventRepository eventRepository;                                  // создается для получения данных из таблицы Базы Данных для дальнейшего сравнения с
    private final EventMapper eventMapper;                                          // создается для получения структуры аналогичной базе данных

    @Transactional
    public void saveOrUpdate(final EventDto eventDto) {
        final Event eventEntity = eventMapper.toEventEntity(eventDto);              //   преобразует EventDto в Event, чтобы можно было сохранить или обновить его в базе данных.

        eventRepository.findByExternalId(eventDto.getExternalId())                  //  eventRepository.findByExternalId(eventDto.getExternalId()) - ищет событие в базе данных по внешнему идентификатору (externalId) из EventDto.
            .ifPresentOrElse(entity -> {                                            // .ifPresentOrElse(entity -> { ... }, () -> { ... }) - это метод из Java Optional, если результат поиска не пустой (существует событие с таким externalId), иначе (сохраняет новое событие).
                    entity.setEventName(eventEntity.getEventName());                // далее...
                    entity.setLocationName(eventEntity.getLocationName());          // Внутри ifPresentOrElse выполняется логика обновления полей события (entity) данными
                    entity.setLocationAddress(eventEntity.getLocationAddress());
                    entity.setStartDate(eventEntity.getStartDate());
                    entity.setEndDate(eventEntity.getEndDate());
                    entity.setEventUrl(eventEntity.getEventUrl());
                    entity.setImageUrl(eventEntity.getImageUrl());
                    entity.setServiceName(eventEntity.getServiceName());
                    entity.setCoordinates(eventEntity.getCoordinates());            // если произошли изменения в полях то произойдет обновление записи в Базе данных после завершения транзакции (завершение этого метода)
                },
                () -> eventRepository.save(eventEntity)                             // если запись не найдена по поиску в findByExternalId(...) то в базе данных будет создана новая запись
            );
    }
}
