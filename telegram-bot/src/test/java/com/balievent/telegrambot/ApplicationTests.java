package com.balievent.telegrambot;

import com.balievent.telegrambot.initializer.PostgreSqlInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgreSqlInitializer.class)
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
