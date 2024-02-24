package com.bali.events.balievents.configuration;

import com.bali.events.balievents.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfiguration { // отсюда попадает отладка после запуска программы из-> public static void main(final String[] args) { SpringApplication.run(Application .class, args);
}
