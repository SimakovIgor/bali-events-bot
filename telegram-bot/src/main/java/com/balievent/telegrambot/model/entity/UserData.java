package com.balievent.telegrambot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserData {
    private LocalDate calendarDate;
    private Integer page;
    private List<Integer> mediaIdList;
    private Integer lastDateSelectedMessageId;

}
