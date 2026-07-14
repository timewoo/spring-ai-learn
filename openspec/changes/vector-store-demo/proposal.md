## Why

学习项目已具备对话、Tool Calling、Memory，下一步需要最小可用的向量检索 / RAG demo，理解 Embedding → VectorStore → 问答增强的完整链路。

## What Changes

- 按 OpenAI 兼容格式补充 embedding 配置
- 增加内存版 `SimpleVectorStore` Bean
- 新增文档入库、相似度检索、RAG 问答接口
- 修复未完成的 `/ai/rag` 写法

## Capabilities

### New Capabilities
- `vector-rag-demo`: 内存向量入库、相似度检索、基于 VectorStore 的 RAG 问答

### Modified Capabilities

## Impact

- `demo/build.gradle`：补充 `spring-ai-vector-store`
- `application.yaml`：OpenAI 风格 embedding 配置
- `MyController`：新增 `/ai/vector/*` 与完善 `/ai/rag`
- 依赖可用的 OpenAI 兼容 embedding 接口（与 chat 可共用 base-url/api-key）
