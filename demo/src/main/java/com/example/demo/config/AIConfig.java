package com.example.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *@author peilizhi 
 *@date 2026/4/1 16:08
 **/
@Configuration
public class AIConfig {


    @Bean
    public ChatClient deepseekChatClient(@Autowired DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }


    @Bean
    public ChatClient planningChatClient(@Autowired DeepSeekChatModel chatModel,
                                         ChatMemory chatMemory) {

        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        # 票务助手任务拆分规则
                        ## 1.要求
                        ### 1.1 根据用户内容识别任务
                        
                        ## 2. 任务
                        ### 2.1 JobType:退票(CANCEL) 要求用户提供姓名和预定号，也需要你能够从之前或者当前的对话中识别姓名和预订号；
                        ### 2.2 JobType:查票(QUERY) 要求用户提供预定号， 也需要你能够从之前或者当前的对话中识别姓名和预订号；
                        ### 2.3 JobType:其他(OTHER)
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }


}
