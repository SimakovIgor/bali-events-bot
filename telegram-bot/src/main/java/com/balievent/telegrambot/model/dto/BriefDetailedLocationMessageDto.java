package com.balievent.telegrambot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BriefDetailedLocationMessageDto {
    private String message;
    private Map<String, Long> locationMap;
}
