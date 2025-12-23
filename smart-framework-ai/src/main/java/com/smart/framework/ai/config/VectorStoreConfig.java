package com.smart.framework.ai.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * VectorStore 和 EmbeddingModel 配置类
 * 确保 VectorStore 和 EmbeddingModel 被正确配置
 * 
 * 注意：VectorStore 由 spring-ai-starter-vector-store-redis 自动配置，
 * 但需要 EmbeddingModel 存在才能正常工作
 *
 * @author smart-framework
 */
@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    /**
     * 配置 EmbeddingModel
     * 如果自动配置没有创建，则手动创建
     */
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModel embeddingModel() {
        return DashScopeEmbeddingModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeEmbeddingOptions.builder()
                        .withModel("text-embedding-v3")
                        .build())
                .build();
    }

    /**
     * 配置 ImageModel
     * 如果自动配置没有创建，则手动创建
     */
    @Bean
    @ConditionalOnMissingBean
    public ImageModel imageModel() {
        return DashScopeImageModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .build();
    }
}

