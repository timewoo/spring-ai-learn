package com.example.demo.advisor;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;

public final class FullRequestLoggerAdvisor {

    private FullRequestLoggerAdvisor() {
    }

    public static SimpleLoggerAdvisor create() {
        return SimpleLoggerAdvisor.builder()
                .requestToString(req -> {
                    if (req == null) {
                        return "null";
                    }
                    var prompt = req.prompt();
                    var sb = new StringBuilder();
                    sb.append("\nmessages:\n");
                    prompt.getInstructions().forEach(message -> sb.append("  - ").append(message).append('\n'));

                    sb.append("options:\n  ").append(prompt.getOptions()).append('\n');

                    if (prompt.getOptions() instanceof ToolCallingChatOptions options
                            && options.getToolCallbacks() != null
                            && !options.getToolCallbacks().isEmpty()) {
                        sb.append("tools:\n");
                        for (ToolCallback callback : options.getToolCallbacks()) {
                            var definition = callback.getToolDefinition();
                            sb.append("  - name: ").append(definition.name()).append('\n');
                            sb.append("    description: ").append(definition.description()).append('\n');
                            sb.append("    inputSchema: ").append(definition.inputSchema()).append('\n');
                        }
                    }
                    else {
                        sb.append("tools: []\n");
                    }
                    return sb.toString();
                })
                .build();
    }
}
