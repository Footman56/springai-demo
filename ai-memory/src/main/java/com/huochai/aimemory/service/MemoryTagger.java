package com.huochai.aimemory.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 *@author peilizhi 
 *@date 2026/3/22 11:42
 **/
@Component
public class MemoryTagger {
    @Autowired
    private ChatClient chatClient;

    public String tag(String content) {

        String prompt = """
                给这条用户信息打标签（多个用逗号）：
                标签范围：
                - preference（偏好）
                - profile（用户背景）
                - habit（习惯）
                
                内容：%s
                """.formatted(content);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
