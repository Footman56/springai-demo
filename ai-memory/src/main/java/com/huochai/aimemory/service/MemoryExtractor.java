package com.huochai.aimemory.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 *
 *@author peilizhi 
 *@date 2026/3/22 11:50
 **/
@Service
public class MemoryExtractor {
    @Autowired
    private ChatClient chatClient;

    public List<String> extractUserMemory(String conversation) {

        String prompt = """
                请从以下对话中提取“用户长期偏好或重要信息”，
                要求：
                1. 只提取有价值信息
                2. 每条一行
                3. 不要废话
                
                对话：
                %s
                """.formatted(conversation);

        String result = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return Arrays.stream(result.split("\n"))
                .filter(s -> !s.isBlank())
                .toList();
    }
}
