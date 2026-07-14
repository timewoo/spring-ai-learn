## ADDED Requirements

### Requirement: Document ingestion into vector store
系统 SHALL 提供接口将示例文档写入 VectorStore，并在写入时通过 EmbeddingModel 生成向量。

#### Scenario: Add sample documents
- **WHEN** 客户端调用文档入库接口
- **THEN** 系统将预置示例文档写入内存 VectorStore 并返回成功信息

### Requirement: Similarity search
系统 SHALL 提供接口按查询文本做相似度检索，返回匹配文档文本列表。

#### Scenario: Search related documents
- **WHEN** 客户端传入查询文本
- **THEN** 系统返回 topK 条相似文档文本

### Requirement: RAG question answering
系统 SHALL 提供 RAG 接口：先从 VectorStore 检索相关文档，再基于检索上下文调用聊天模型回答。

#### Scenario: Answer with retrieved context
- **WHEN** 客户端调用 RAG 接口并传入问题
- **THEN** 系统使用 QuestionAnswerAdvisor 增强 prompt 并返回 ChatResponse
