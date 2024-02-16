package com.example.telegrambot.service;

import com.example.telegrambot.contant.Constants;
import com.example.telegrambot.model.entity.Event;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.util.DateUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Setter
@Getter
@RequiredArgsConstructor
public class CalendarProcessService {

    private final EventRepository eventRepository;

    private Integer day = LocalDate.now().getDayOfMonth();            // текущее число (день месяца)
    private Integer month = LocalDate.now().getMonthValue();          // текущий месяца
    private Integer year = LocalDate.now().getYear();                 // текущий год

    public SendMessage process(Update update,
                               AtomicInteger getDay,
                               AtomicInteger getMonth,
                               AtomicInteger getYear) {
        String text = update.getMessage().getText();

        SendMessage sendMessage = new SendMessage();                        // создаем класс для отправки сообщения
        sendMessage.setChatId(update.getMessage().getChatId());             // присваиваем идентификатор получателя сообщения

        if (DateUtil.getMonthNumber(text) > 0) {                              // Наличие в строке месяца
            if (getDay.get() > 0) {
                this.day = getDay.get();                              // найденный ДЕНЬ присваиваем глобально
            }
            getMonthAndSendList(text, sendMessage);                         // Получаем месяц из строки

        } else if (getDay.get() > 0) {                                      // наличие в строке числа
            this.day = getDay.get();                                  // найденный ДЕНЬ присваиваем глобально
            if (getMonth.get() > 0) {
                this.month = getMonth.get();                          // найденный МЕСЯЦ присваиваем глобально
            }
            if (getYear.get() > 0) {
                this.year = getYear.get();                            // найденный ГОД присваиваем глобально
            }
            sendMessageToDay(sendMessage);
        }
        return sendMessage;
    }

    private void getMonthAndSendList(String text, SendMessage sendMessage) {
        int monthNumber = DateUtil.getMonthNumber(text);
        if (0 < monthNumber && monthNumber < 13) {
            if (this.month == 12 && monthNumber == 1) {       // переход на следующий год
                this.year++;
            }
            if (this.month == 1 && monthNumber == 12) {       // переход на предыдущий год
                this.year--;
            }
            this.month = monthNumber;

            sendMessageToDay(sendMessage);
        } else {
            sendMessage.setText(Constants.WE_DO_NOT_HAVE_SUCH_DATA + text);
        }
    }

    private void sendMessageToDay(SendMessage sendMessage) {
        if (isValidDate(this.day, this.month, this.year)) {
            String eventListToday = findEventListToday();

            sendMessage.setText(String.format("%s %02d.%02d.%d \n\n\n %s",
                Constants.LIST_OF_EVENTS_ON, this.day, this.month, this.year, eventListToday));

            sendMessage.setReplyMarkup(KeyboardUtil.getKeyboard(this.month, this.year));
        } else {
            // Если дата недействительна, выполните необходимые действия
            // Например, отправьте пользователю сообщение об ошибке

            sendMessage.setText(Constants.INCORRECT_DATE);
        }
    }

    private boolean isValidDate(int day, int month, int year) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (java.time.DateTimeException e) {
            day--;
            if (day > 0) {
                this.day = day;
                return isValidDate(day, month, year);
            } else {
                return false;
            }
        }
    }

    private String findEventListToday() {
        LocalDateTime from = LocalDateTime.of(this.year, this.month, this.day, 0, 0);
        LocalDateTime end = LocalDateTime.of(this.year, this.month, this.day, 23, 59);

        List<Event> eventList = eventRepository.findEventsByStartDateBetween(from, end);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(event.getEventName())
                .append("\n\n")
                .append("Location:")
                .append("\n")
                .append(event.getLocationAddress())
                .append("\n")
                .append("Coordinates:")
                .append("\n")
                .append(event.getCoordinates())
                .append("\n")
                .append("----------------")
                .append("\n\n\n");
        }

        return stringBuilder.toString();
    }
}
