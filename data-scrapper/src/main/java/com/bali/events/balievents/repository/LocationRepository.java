package com.bali.events.balievents.repository;

import com.bali.events.balievents.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {
}
