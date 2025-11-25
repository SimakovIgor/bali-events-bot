package com.balievent.telegrambot.repository;

import com.balievent.telegrambot.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findEventsByStartDateTimeBetween(OffsetDateTime start,
                                                 OffsetDateTime end);

    Optional<Event> findByExternalId(String externalId);

    Optional<Event> findByEventUrl(String eventUrl);
}
