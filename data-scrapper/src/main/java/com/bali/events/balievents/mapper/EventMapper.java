package com.bali.events.balievents.mapper;

import com.bali.events.balievents.model.EventDto;
import com.bali.events.balievents.model.entity.Event;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Mapper(config = MapperConfiguration.class)
public interface EventMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "externalId", source = "externalId")
    @Mapping(target = "eventName", source = "eventName")
    @Mapping(target = "locationName", source = "locationName")
    @Mapping(target = "locationAddress", source = "locationAddress")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "eventUrl", source = "eventUrl")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "serviceName", source = "serviceName")
    @Mapping(target = "coordinates", source = "coordinates")
    Event toEventEntity(EventDto eventDto);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(String fromDateTime) {

        // Разбираем строку в объект OffsetDateTime; String fromDateTime = "2024-1-1T22:00+08:00";
        // Разбираем дату и время с явным указанием формата
        LocalDateTime localDateTime = LocalDateTime.parse(fromDateTime.substring(0, fromDateTime.indexOf("+")), DateTimeFormatter.ofPattern("yyyy-M-d'T'HH:mm"));

        // Разбираем смещение временной зоны
        String offsetString = fromDateTime.substring(fromDateTime.indexOf("+") + 1); // Извлекаем только строку смещения
        int hours = Integer.parseInt(offsetString.split(":")[0]); // Получаем часы из строки
        int minutes = Integer.parseInt(offsetString.split(":")[1]); // Получаем минуты из строки
        ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(hours, minutes); // Создаем ZoneOffset

        // Создаем OffsetDateTime с помощью LocalDateTime и ZoneOffset
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);

        // Получаем LocalDateTime из OffsetDateTime
        return offsetDateTime.toLocalDateTime();
    }
}
