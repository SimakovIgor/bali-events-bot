package com.balievent.telegrambot.repository;

import com.balievent.telegrambot.entity.EventSearchCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventSearchCriteriaRepository extends JpaRepository<EventSearchCriteria, Long> {

    Optional<EventSearchCriteria> findByChatId(Long chatId);
}
