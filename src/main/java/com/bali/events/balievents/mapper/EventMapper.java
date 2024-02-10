package com.bali.events.balievents.mapper;

import com.bali.events.balievents.model.EventDto;
import com.bali.events.balievents.model.entity.Event;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

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
    Event toEventEntity(EventDto eventDto);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(String fromDateTime) {
        return LocalDateTime.now();
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(fromDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//        return zonedDateTime.toLocalDateTime();
    }

}
