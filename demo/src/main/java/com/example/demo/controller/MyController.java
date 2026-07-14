package com.example.demo.controller;

import com.example.demo.advisor.FullRequestLoggerAdvisor;
import com.example.demo.advisor.ReReadingAdvisor;
import com.example.demo.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
class MyController {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory;

    private final ChatMemoryRepository chatMemoryRepository;

    private final VectorStore vectorStore;

    public MyController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, VectorStore vectorStore, JdbcChatMemoryRepository chatMemoryRepository) {
        this.chatClient = chatClientBuilder.defaultAdvisors(FullRequestLoggerAdvisor.create()).build();
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
        this.chatMemoryRepository = chatMemoryRepository;
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

    @GetMapping("/ai/rereading")
    ChatResponse reReading(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .advisors(new ReReadingAdvisor())
                .call()
                .chatResponse();
    }

    @GetMapping("/ai/memory")
    ChatResponse memory(String userInput, String conversationId) {
        return this.chatClient.prompt()
                .user(userInput)
                .advisors(a -> a
                        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();
    }

    @PostMapping("/ai/vector/add")
    Map<String, Object> addVectorDocuments() {
        // 用虚构/私有事实做 demo：模型训练数据里通常没有，才能对比有无 RAG 的差异
        List<Document> documents = List.of(
                new Document("""
                        星河科技（XingHe Tech）内部休假政策：
                        正式员工每年享有 18 天带薪年假；入职满 3 年额外增加 5 天。
                        年假申请须提前至少 3 个工作日在 OA 系统提交，直属经理审批后方可生效。
                        """.stripIndent().trim(),
                        Map.of("topic", "leave-policy", "company", "XingHe")),
                new Document("""
                        星河科技 2026 年内部项目代号：
                        「青柠」是客户成功部的续费预警系统，负责人是林晓雯。
                        「北极星」是研发中台的权限统一网关，上线日期为 2026-03-18。
                        """.stripIndent().trim(),
                        Map.of("topic", "projects", "company", "XingHe")),
                new Document("""
                        星河科技食堂补贴规则：
                        工作日午餐补贴 28 元/人，晚餐补贴 22 元/人；周末与法定节假日无补贴。
                        补贴通过员工卡自动到账，不可折现，当月未使用部分不结转。
                        """.stripIndent().trim(),
                        Map.of("topic", "canteen", "company", "XingHe"))
        );
        this.vectorStore.add(documents);
        return Map.of("added", documents.size());
    }

    @GetMapping("/ai/vector/search")
    List<Map<String, Object>> searchVectorDocuments(@RequestParam String query,
            @RequestParam(defaultValue = "2") int topK) {
        return this.vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build()
        ).stream()
                .map(document -> Map.<String, Object>of(
                        "text", document.getText(),
                        "score", document.getScore() == null ? 0.0 : document.getScore(),
                        "metadata", document.getMetadata()))
                .toList();
    }

    @GetMapping("/ai/rag")
    ChatResponse rag(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .advisors(QuestionAnswerAdvisor.builder(this.vectorStore).build())
                .call()
                .chatResponse();
    }

    @GetMapping("/ai/completion")
    String completion(String userInput, String conversationId) {
        return this.chatClient.prompt()
                .user(userInput)
                .advisors(a -> a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .param(ChatMemory.CONVERSATION_ID, conversationId).advisors(QuestionAnswerAdvisor.builder(this.vectorStore).build()))
                .tools(new DateTimeTools())          
                .call()
                .content();
    }
}
