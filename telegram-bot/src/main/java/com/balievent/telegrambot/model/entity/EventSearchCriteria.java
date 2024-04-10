package com.balievent.telegrambot.model.entity;

import com.balievent.telegrambot.constant.TgBotConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Getter
@Setter
@ToString

@Entity
@Table(name = "event_search_criteria")
public class EventSearchCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    @Column(name = "location_name_list")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> locationNameList = new ArrayList<>();

    @Column(name = "date")
    private String date;

    public void toggleLocationName(final String locationName,
                                   final List<String> locationIds) {
        // если нужно удалить все локации
        if (locationName.equals(TgBotConstants.DESELECT_ALL)) {
            // удалить все локации кроме последней
            if (!locationNameList.isEmpty()) {
                // Получаем последний элемент, где лежит выбранный пользователем фильтр
                final String lastElement = locationNameList.get(locationNameList.size() - 1);
                // удаляем все элементы списка
                locationNameList.clear();
                // Добавляем кнопку Select All
                locationNameList.add(TgBotConstants.SELECT_ALL);
                // Добавляем в список переменную lastElement которая содержит пользователем фильтр
                locationNameList.add(lastElement);
                return;
            }
        }
        // если нужно добавить все локации
        if (locationName.equals(TgBotConstants.SELECT_ALL)) {
            // удалить все локации кроме последней
            if (!locationNameList.isEmpty()) {
                // Получаем последний элемент, где лежит выбранный пользователем фильтр
                final String lastElement = locationNameList.get(locationNameList.size() - 1);
                // удаляем все элементы списка
                locationNameList.clear();
                // добавить все существующие в базе
                locationNameList.addAll(locationIds);
                // Добавляем кнопку Select All
                locationNameList.add(TgBotConstants.DESELECT_ALL);
                // Добавляем в список переменную lastElement которая содержит пользователем фильтр
                locationNameList.add(lastElement);
                return;
            }
        }

        if (locationNameList.contains(locationName)) {
            locationNameList.remove(locationName);
        } else {
            locationNameList.add(locationName);
        }
    }
}
