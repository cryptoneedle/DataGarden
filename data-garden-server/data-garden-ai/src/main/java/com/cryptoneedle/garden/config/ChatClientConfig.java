package com.cryptoneedle.garden.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-15
 */
@Configuration
public class ChatClientConfig {
    
    @Bean
    @Primary
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                         .defaultAdvisors(new SimpleLoggerAdvisor())
                         .build();
    }
}