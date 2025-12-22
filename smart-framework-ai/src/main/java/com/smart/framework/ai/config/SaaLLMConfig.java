package com.smart.framework.ai.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI DashScope 配置类
 * 配置多个 ChatModel 和 ChatClient 实例
 *
 * @author smart-framework
 */
@Configuration
public class SaaLLMConfig {

    /**
     * 模型常量名称定义 保证一个系统可以使用多个模型
     */
    private static final String DEEPSEEK_MODEL = "deepseek-v3";
    private static final String QWEN_MODEL = "qwen-max";

    /**
     * 注入 DashScopeApi 实例
     */
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public ChatClient chatClient(@Qualifier("qwenChatModel") ChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return builder.build();
    }

    /**
     * 构建deepseek模型
     */
    @Bean(name = "deepSeekChatModel")
    public ChatModel deepSeekChatModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(DEEPSEEK_MODEL).build())
                .build();
    }

    /**
     * 构建qwen模型
     */
    @Bean(name = "qwenChatModel")
    public ChatModel qwenChatModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN_MODEL).build())
                .build();
    }

    /**
     * 构建deepseek模型的ChatClient
     *
     * @param chatModel 指定chatModel
     */
    @Bean(name = "deepSeekChatClient")
    public ChatClient deepSeekChatClient(@Qualifier("deepSeekChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder().withModel(DEEPSEEK_MODEL).build())
                .build();
    }

    /**
     * 构建qwen模型的ChatClient
     *
     * @param chatModel 指定chatModel
     */
    @Bean(name = "qwenChatClient")
    public ChatClient qwenChatClient(@Qualifier("qwenChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN_MODEL).build())
                .build();
    }

    /**
     * 构建deepseek模型的ChatClient(带记忆功能)
     *
     * @param chatModel                       指定chatModel
     * @param redissonRedisChatMemoryRepository 内存仓库
     */
    @Bean(name = "deepSeekMemoryChatClient")
    public ChatClient deepSeekMemoryChatClient(@Qualifier("deepSeekChatModel") ChatModel chatModel,
                                               RedissonRedisChatMemoryRepository redissonRedisChatMemoryRepository) {
        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(redissonRedisChatMemoryRepository)
                .maxMessages(10)
                .build();

        return ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder().withModel(DEEPSEEK_MODEL).build())
                // 将消息内存Advisor添加到ChatClient中
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(messageWindowChatMemory).build())
                .build();
    }
}

