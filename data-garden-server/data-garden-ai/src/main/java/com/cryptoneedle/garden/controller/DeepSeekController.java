package com.cryptoneedle.garden.controller;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/deepseek")
public class DeepSeekController {
    
    @Autowired
    private DeepSeekChatModel deepSeekChatModel;
    
    @GetMapping("/chat")
    public String deepSeek(String message) {
        String result = deepSeekChatModel.call(new Prompt("用一句话介绍你的版本和你的数据最新日期"))
                                         .getResult()
                                         .getOutput()
                                         .getText();
        return result;
    }
}