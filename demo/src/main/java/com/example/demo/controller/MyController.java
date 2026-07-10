package com.example.demo.controller;

import com.example.demo.advisor.FullRequestLoggerAdvisor;
import com.example.demo.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
class MyController {

    private final ChatClient chatClient;

    public MyController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultAdvisors(FullRequestLoggerAdvisor.create()).build();
    }

    @GetMapping("/ai")
    String generation(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }

    @GetMapping("/ai/stream/chat")
    Flux<ChatResponse> streamChat(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .stream()
                .chatResponse();
    }

    @GetMapping("/ai/chatresponse")
    ChatResponse chatResponse(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .call()
                .chatResponse();
    }

    @GetMapping("/ai/chatclientresponse")
    ChatClientResponse chatClientResponse(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .call()
                .chatClientResponse();
    }

    @GetMapping("/ai/addextrabody")
    ChatResponse addExtraBody(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .options(OpenAiChatOptions.builder()
                        .extraBody(Map.of("thinking", Map.of("type", "disabled"))))
                .call()
                .chatResponse();
    }

    @GetMapping("/ai/addsystemprompt")
    ChatResponse addSystemPrompt(String userInput, String voice) {
        
        return this.chatClient.prompt()
                .system(s -> s.param("voice", voice))
                .user(userInput)
                .call()
                .chatResponse();
    }

    @GetMapping("/ai/toolcall")
    ChatResponse toolCall(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .tools(new DateTimeTools())
                .call()
                .chatResponse();
    }
}