package com.cryptoneedle.garden.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/openai")
public class OpenAIController {
    
    @Autowired
    private OpenAiChatModel chatModel;
    
    @Autowired
    private ChatClient chatClient;
    
    @GetMapping("/chat-model")
    public String chatModel(String message) {
        return chatModel.call(new Prompt(message))
                        .getResult()
                        .getOutput()
                        .getText();
    }
    
    @GetMapping("/chat-client")
    public String chatClient(String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
    
    @GetMapping(path = "/chat-client/stream", produces = "text/html;charset=utf-8")
    public Flux<String> streamChatClient(String message) {
        return chatClient.prompt()
                         .user(message)
                         .stream()
                         .content();
    }
}