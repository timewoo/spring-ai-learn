## Context

现有 demo 使用 `spring-ai-starter-model-openai` 对接兼容 OpenAI 协议的服务。向量能力需要额外的 EmbeddingModel + VectorStore。

## Goals / Non-Goals

- Goals: 最小可运行向量 demo（入库、检索、RAG）
- Non-Goals: 生产级向量库、文档切分流水线、持久化、权限

## Decisions

1. **VectorStore**：使用 `SimpleVectorStore`（进程内，适合学习）
2. **配置**：沿用 OpenAI 属性风格
   - `spring.ai.openai.api-key` / `base-url`
   - `spring.ai.openai.embedding.options.model`
3. **API**：
   - `POST /ai/vector/add`：写入示例文档
   - `GET /ai/vector/search`：纯相似度检索
   - `GET /ai/rag`：`QuestionAnswerAdvisor` 问答
4. **Advisor 范围**：仅 `/ai/rag` 挂 `QuestionAnswerAdvisor`，不设为全局 default

## Risks

- 若 `base-url` 仅支持 chat、不支持 embeddings，入库会失败；需换成提供 embedding 的 OpenAI 兼容地址或模型名。
