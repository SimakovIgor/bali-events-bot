package com.balievent.telegrambot.repository;

import com.balievent.telegrambot.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {
}
