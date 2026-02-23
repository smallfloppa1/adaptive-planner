package com.floppahost.adaptiveplanner.planner.presentation.telegrambot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record TelegramBotProperties(
        String apiKey,
        String username
) {
}
