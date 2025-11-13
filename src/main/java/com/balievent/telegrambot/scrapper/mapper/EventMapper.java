package com.balievent.telegrambot.scrapper.mapper;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.scrapper.dto.EventDto;
import org.apache.commons.lang3.StringUtils;
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

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d'T'HH:mm");

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "externalId",
             source = "eventDto.externalId")
    @Mapping(target = "eventName",
             source = "eventDto.eventName")
    @Mapping(target = "startDate",
             source = "eventDto.startDate")
    @Mapping(target = "eventUrl",
             source = "eventDto.eventUrl")
    @Mapping(target = "imageUrl",
             source = "eventDto.imageUrl")
    @Mapping(target = "serviceName",
             source = "eventDto.serviceName")
    @Mapping(target = "coordinates",
             source = "eventDto.coordinates")
    @Mapping(target = "location",
             source = "location")
    Event toEvent(EventDto eventDto,
                  Location location);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id",
             source = "locationName")
    @Mapping(target = "address",
             source = "locationAddress")
    Location toLocation(EventDto eventDto);

    /**
     * Приведение строки формат: "2024-1-1T22:00+08:00" к стандартному LocalDateTime формат: "yyyy-M-d'T'HH:mm".
     *
     * @param fromDateTime - строка формат: "2024-1-1T22:00+08:00"
     * @return - LocalDateTime
     */
    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(final String fromDateTime) {
        if (StringUtils.isEmpty(fromDateTime)) {
            return null;
        }

        final String offsetString = fromDateTime.substring(fromDateTime.indexOf(':') + 1);
        final int hours = Integer.parseInt(offsetString.split(":")[0]);
        final int minutes = Integer.parseInt(offsetString.split(":")[1]);
        final ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(hours, minutes);

        final OffsetDateTime offsetDateTime = OffsetDateTime.of(LocalDateTime.now(), zoneOffset);

        return offsetDateTime.toLocalDateTime();
    }
}
