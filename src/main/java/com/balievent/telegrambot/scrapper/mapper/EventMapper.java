package com.balievent.telegrambot.scrapper.mapper;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.scrapper.dto.EventDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.balievent.telegrambot.scrapper.support.SeleniumUtils.getAttributeByClass;
import static com.balievent.telegrambot.scrapper.support.SeleniumUtils.getAttributeByXpath;

@Mapper(config = MapperConfiguration.class)
public interface EventMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d'T'HH:mm");

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "externalId", source = "eventDto.externalId")
    @Mapping(target = "eventName", source = "eventDto.eventName")
    @Mapping(target = "startDate", source = "eventDto.startDate", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "endDate", source = "eventDto.endDate", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "eventUrl", source = "eventDto.eventUrl")
    @Mapping(target = "imageUrl", source = "eventDto.imageUrl")
    @Mapping(target = "serviceName", source = "eventDto.serviceName")
    @Mapping(target = "coordinates", source = "eventDto.coordinates")
    @Mapping(target = "location", source = "location")
    Event toEvent(EventDto eventDto, Location location);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "locationName")
    @Mapping(target = "address", source = "locationAddress")
    Location toLocation(EventDto eventDto);

    /**
     * Приведение строки формат: "2024-1-1T22:00+08:00" к стандартному LocalDateTime формат: "yyyy-M-d'T'HH:mm".
     *
     * @param fromDateTime - строка формат: "2024-1-1T22:00+08:00"
     * @return - LocalDateTime
     */
    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(final String fromDateTime) {
        final LocalDateTime localDateTime = LocalDateTime.parse(fromDateTime.substring(0, fromDateTime.indexOf('+')), FORMATTER);

        final String offsetString = fromDateTime.substring(fromDateTime.indexOf('+') + 1);
        final int hours = Integer.parseInt(offsetString.split(":")[0]);
        final int minutes = Integer.parseInt(offsetString.split(":")[1]);
        final ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(hours, minutes);

        final OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);

        return offsetDateTime.toLocalDateTime();
    }

    default EventDto createEventDto(final WebElement child, final String rootName) {
        final String externalId = child.getAttribute("id");
        final String eventName = getAttributeByClass(child, "evcal_event_title", "innerHTML");
        final String locationName = getAttributeByClass(child, "event_location_attrs", "data-location_name");
        final String locationAddress = getAttributeByClass(child, "event_location_attrs", "data-location_address");
        final String startDate = getAttributeByXpath(child, "div/meta[2]", "content");
        final String endDate = getAttributeByXpath(child, "div/meta[3]", "content");
        final String eventUrl = getAttributeByXpath(child, "div/a", "href");
        final String imageUrl = getAttributeByClass(child, "ev_ftImg", "data-img");
        final String coordinates = getAttributeByClass(child, "evcal_location", "data-latlng");

        return EventDto.builder()
            .externalId(externalId)
            .eventName(eventName)
            .locationName(locationName)
            .locationAddress(locationAddress)
            .startDate(startDate)
            .endDate(endDate)
            .eventUrl(eventUrl)
            .imageUrl(imageUrl)
            .serviceName(rootName)
            .coordinates(coordinates)
            .build();
    }
}
