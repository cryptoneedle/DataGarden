package com.cryptoneedle.garden;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    //@Autowired
    //private ChatClient chatClient;
    
    @Autowired
    private DeepSeekChatModel deepSeekChatModel;
    
    @GetMapping("/chat")
    public Map<String, String> chat(@RequestParam(value = "message", defaultValue = "Hello DeepSeek") String message) {
        //String response = chatClient.prompt()
        //                            .user(message)
        //                            .call()
        //                            .content();
        String whatIsYourModel = deepSeekChatModel.call(new Prompt("用一句话介绍你的版本"))
                                                  .getResult()
                                                  .getOutput()
                                                  .getText();
        return Map.of("response", whatIsYourModel);
    }
}