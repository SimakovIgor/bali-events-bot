package com.balievent.telegrambot.scrapper.mapper;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.scrapper.dto.EventDto;
import com.balievent.telegrambot.scrapper.utils.ZoneUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.Nullable;

@Mapper(config = MapperConfiguration.class)
public interface EventMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d'T'HH:mm");

    @Mapping(target = "userProfileEventList",
             ignore = true)
    @Mapping(target = "updateDateTime",
             ignore = true)
    @Mapping(target = "startDateTime",
             expression = "java(toOffsetDateTime(eventDto.getStartDate()))")
    @Mapping(target = "createDateTime",
             ignore = true)
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
    @Nullable
    @Named("toOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(final String fromDateTime) {
        if (StringUtils.isEmpty(fromDateTime)) {
            return null;
        }
        LocalDateTime ldt = LocalDateTime.parse(fromDateTime);
        return ZoneUtils.toBaliOffsetDateTime(ldt);
    }
}
