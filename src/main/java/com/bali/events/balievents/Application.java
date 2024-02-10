package com.bali.events.balievents;

import com.bali.events.balievents.service.TheBeatBaliScrapperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(TheBeatBaliScrapperService theBeatBaliScrapperService) {
        return args -> theBeatBaliScrapperService.process();
    }

}
