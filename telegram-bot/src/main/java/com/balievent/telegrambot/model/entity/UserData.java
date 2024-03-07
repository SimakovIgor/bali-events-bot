package com.balievent.telegrambot.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDate;
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
@Table(name = "user_data")
public class UserData {
    @Id
    private Long id;
    @Column(name = "calendar_date")
    private LocalDate calendarDate;
    @Column(name = "current_page")
    private Integer currentPage;
    @Column(name = "page_count")
    private Integer pageCount;
    @Column(name = "sent_message_id_list")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<Integer> sentMessageIdList = new ArrayList<>();
    @Column(name = "last_date_selected_message_id")
    private Integer lastDateSelectedMessageId;

}
