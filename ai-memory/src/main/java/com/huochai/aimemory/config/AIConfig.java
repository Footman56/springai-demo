package com.huochai.aimemory.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 * MySQL: 主数据库
 * PostgreSQL: 向量数据库
 * Redis: Session 记忆
 *
 * Chat 模型: DeepSeek (deepseek-chat)
 * Embedding 模型: 阿里云通义千问 text-embedding-v3 (DashScope SDK)
 * - 维度: 1536
 * - 价格: 0.0007元/千tokens
 * - 效果: 中文CMTEB评测73.23分
 *
 * @author peilizhi
 * @date 2026/3/22
 */
@Configuration
public class AIConfig {

    /**
     * 配置 ChatClient
     * ChatModel 由 Spring AI 自动配置创建（DeepSeek）
     */
    @Bean
    public ChatClient chatClient(@Autowired DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
