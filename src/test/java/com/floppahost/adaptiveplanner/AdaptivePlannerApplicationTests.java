package com.floppahost.adaptiveplanner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;

@SpringBootTest
@ActiveProfiles("test")
class AdaptivePlannerApplicationTests {

    @MockitoBean
    private TelegramBotInitializer telegramBotInitializer;

    @Test
    void contextLoads() {
    }

}
