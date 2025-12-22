# Spring AI Smart

基于 Spring AI 和阿里云 DashScope 的智能 AI 框架模块。

## 项目结构

```
spring-ai-smart/
├── settings.gradle          # Gradle 项目设置
├── build.gradle            # 根项目构建配置
├── smart-framework-ai/     # AI 框架模块
│   ├── build.gradle        # 子模块构建配置
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/smart/framework/ai/
│       │   │       ├── config/          # 配置类
│       │   │       ├── controller/      # 控制器
│       │   │       ├── model/           # 模型类
│       │   │       └── SpringAiApplication.java  # 主启动类
│       │   └── resources/
│       │       └── application.yml      # 应用配置
│       └── test/
│           └── java/
└── README.md
```

## 功能特性

- ✅ 支持多个 AI 模型（DeepSeek、Qwen）
- ✅ ChatClient 和 ChatModel 两种调用方式
- ✅ 流式和非流式响应
- ✅ Redis 向量存储支持
- ✅ Redis 内存对话支持
- ✅ 图片生成功能
- ✅ 文本向量化功能
- ✅ PromptTemplate 模板支持

## 技术栈

- Spring Boot 3.3.0
- Spring AI Alibaba Agent Framework
- Spring AI DashScope Starter
- Spring AI Vector Store Redis
- Spring AI Memory Redis
- Java 17

## 配置说明

### 1. 环境变量配置

确保在环境变量中配置 DashScope API Key：

```bash
export DASHSCOPE_API_KEY=your-api-key-here
```

### 2. application.yml 配置

配置文件位于 `smart-framework-ai/src/main/resources/application.yml`：

```yaml
spring:
  ai:
    dashscope:
      base-url: https://dashscope.aliyuncs.com
      api-key: ${DASHSCOPE_API_KEY}  # 从环境变量读取
      chat:
        client:
          connect-timeout: 60s
          read-timeout: 30s
        options:
          model: qwen-plus
          temperature: 0.7
      embedding:
        options:
          model: text-embedding-v3
    vectorstore:
      redis:
        initialize-schema: true
        index-name: custom-index
        prefix: custom-prefix

  data:
    redis:
      host: your-redis-host
      port: 6379
      password: your-redis-password
      database: 0
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

## API 接口

### ChatHelloController

- `GET /api/ai/chat/hello?msg=你好` - 普通聊天
- `GET /api/ai/chat/helloStream?msg=你好` - 流式聊天

### ChatClientController

- `GET /api/ai/chatClient/doChat?msg=你好` - 使用 ChatClient 聊天
- `GET /api/ai/chatClient/doChat2?msg=你好` - 使用 DeepSeek 模型流式聊天
- `GET /api/ai/chatClient/doChat3?msg=你好` - 使用 Qwen 模型流式聊天
- `GET /api/ai/chatClient/doChat4?msg=你好` - 使用 Qwen ChatClient 流式聊天
- `GET /api/ai/chatClient/doChat5?msg=你好` - 使用 DeepSeek ChatClient 流式聊天
- `GET /api/ai/chatClient/doChat6?msg=你好` - 带系统提示的聊天
- `GET /api/ai/chatClient/doChat7?msg=你好` - 使用 ChatModel 实现聊天
- `GET /api/ai/chatClient/doChat8?msg=你好` - 获取完整 ChatResponse
- `GET /api/ai/chatClient/doChat9?msg=嘉兴` - 工具调用示例
- `GET /api/ai/chatClient/doChat10?msg=故事&output=json&limit=100` - PromptTemplate 示例
- `GET /api/ai/chatClient/doChat11?msg=故事&output=json&limit=100` - 从文件读取 PromptTemplate
- `GET /api/ai/chatClient/doChat12?systemTopic=Java&userTopic=什么是Spring` - 角色限定示例
- `GET /api/ai/chatClient/doChat13?name=张三&age=20` - 结构化输出示例
- `GET /api/ai/chatClient/doChat14?message=你好&userId=123` - Redis 内存对话
- `GET /api/ai/chatClient/doChat15?message=一只可爱的小猫` - 图片生成
- `GET /api/ai/chatClient/doChat16?message=嘉兴小霸王` - 文本向量化

## 构建和运行

### 使用 Gradle

```bash
# 构建项目
./gradlew build

# 运行应用
./gradlew :smart-framework-ai:bootRun

# 或者直接运行主类
./gradlew :smart-framework-ai:run
```

### 使用 IDE

直接运行 `SpringAiApplication` 主类即可。

## 依赖说明

项目使用 Gradle 作为构建工具，主要依赖包括：

- `spring-boot-starter-web` - Web 支持
- `spring-boot-starter-data-redis` - Redis 支持
- `spring-ai-alibaba-starter-dashscope` - DashScope AI 支持
- `spring-ai-starter-vector-store-redis` - Redis 向量存储
- `spring-ai-alibaba-starter-memory-redis` - Redis 内存对话

## 注意事项

1. **API Key 安全**：建议将 API Key 配置在环境变量中，不要直接写在配置文件中
2. **Redis 配置**：确保 Redis 服务正常运行
3. **网络连接**：需要能够访问 DashScope API 服务
4. **内存对话**：`doChat14` 接口需要配置 `deepSeekMemoryChatClient` Bean，当前代码中已注释，需要根据实际需求启用

## 开发规范

- 代码遵循 Java 编码规范
- 使用 Lombok 简化代码
- 统一使用 SLF4J 日志框架
- 接口使用 RESTful 风格

## 许可证

MIT License

