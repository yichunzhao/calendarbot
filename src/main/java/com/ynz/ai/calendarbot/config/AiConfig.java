package com.ynz.ai.calendarbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("You are a dental appointment assistant.")
                // Keep last 5 messages (user + bot)
                //.defaultMemory(new SlidingWindowMemoryStore(5))
                .build();
    }
}
