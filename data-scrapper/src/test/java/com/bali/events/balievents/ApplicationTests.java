package com.bali.events.balievents;

import com.bali.events.balievents.initializer.PostgreSqlInitializer;
import com.bali.events.balievents.service.SchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {
    PostgreSqlInitializer.class
})
@ActiveProfiles("test")
class ApplicationTests {

    @MockBean
    protected SchedulerService schedulerService;

    @Test
    void contextLoads() {
    }

}
