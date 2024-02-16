package com.example.telegrambot.repository;

import com.example.telegrambot.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findEventsByStartDateBetween(LocalDateTime start, LocalDateTime end);
}
