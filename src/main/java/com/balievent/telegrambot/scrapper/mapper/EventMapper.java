package com.balievent.telegrambot.scrapper.mapper;

import com.balievent.telegrambot.entity.Event;
import com.balievent.telegrambot.entity.Location;
import com.balievent.telegrambot.scrapper.model.EventDto;
import com.balievent.telegrambot.scrapper.model.RawEventHtml;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.annotation.Nullable;

@Mapper(config = MapperConfiguration.class)
public interface EventMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    @Mapping(target = "userProfileEventList",
             ignore = true)
    @Mapping(target = "updateDateTime",
             ignore = true)
    @Mapping(target = "createDateTime",
             ignore = true)
    @Mapping(target = "id",
             ignore = true)
    @Mapping(target = "location",
             source = "location")
    @Mapping(target = "startDateTime",
             expression = "java(mapStartDateTime(eventDto))")
    Event toEvent(EventDto eventDto,
                  Location location);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id",
             source = "locationName")
    @Mapping(target = "address",
             source = "locationAddress")
    Location toLocation(EventDto eventDto);

    @Mapping(target = "serviceName",
             ignore = true)
    @Mapping(target = "locationAddress",
             ignore = true)
    @Mapping(target = "endDate",
             ignore = true)
    @Mapping(target = "coordinates",
             ignore = true)
    @Mapping(target = "eventName",
             source = "raw.eventName")
    @Mapping(target = "time",
             source = "raw.time")
    @Mapping(target = "locationName",
             source = "raw.locationName")
    @Mapping(target = "eventUrl",
             source = "raw.eventUrl")
    @Mapping(target = "imageUrl",
             source = "raw.imageUrl")
    @Mapping(target = "date",
             source = "date")
    EventDto rawToDto(RawEventHtml raw,
                      LocalDate date);

    @Nullable
    @Named("mapStartDateTime")
    default LocalDateTime mapStartDateTime(EventDto eventDto) {
        if (StringUtils.isEmpty(eventDto.getTime())) {
            return null;
        }

        final var time = LocalTime.parse(eventDto.getTime(), FORMATTER);
        return LocalDateTime.of(eventDto.getDate(), time);
    }
}
