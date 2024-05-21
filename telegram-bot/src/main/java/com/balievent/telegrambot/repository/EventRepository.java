package com.balievent.telegrambot.repository;

import com.balievent.telegrambot.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findEventsByStartDateBetween(LocalDateTime start, LocalDateTime end);

}
