package com.huochai.aimemory.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *@author peilizhi
 *@date 2026/3/22 11:49
 **/
@Service
public class AdvancedMemoryOrchestrator {
    @Autowired
    private RedisChatMemory sessionMemory;

    @Autowired
    private MemoryVectorService vectorService;

    @Autowired
    private UserMemoryRepository userRepo;

    public List<Message> build(Long userId, String sessionId, String prompt) {

        List<Message> messages = new ArrayList<>();

        // 检查是否是首次访问
        boolean isFirstVisit = isFirstTimeUser(userId, sessionId);

        if (isFirstVisit) {
            // 首次访问用户，添加欢迎提示
            messages.add(new SystemMessage("这是您与AI助手的首次对话，请友好地自我介绍并询问用户有什么可以帮助的。"));
        }

        // 1️⃣ 用户长期记忆（DB）
        List<String> longTermMemories = userRepo.findTopMemory(userId);
        longTermMemories.forEach(m -> messages.add(new SystemMessage("用户信息：" + m)));

        // 2️⃣ 向量记忆（语义搜索）
        try {
            List<String> vectorMemories = vectorService.search(userId, prompt);
            vectorMemories.forEach(m -> messages.add(new SystemMessage("相关记忆：" + m)));
        } catch (Exception e) {
            // 向量搜索失败时忽略，不影响主流程
        }

        // 3️⃣ Session 短期记忆
        sessionMemory.get(sessionId)
                .forEach(m -> messages.add(new UserMessage(m)));

        return messages;
    }

    /**
     * 检查是否是首次访问用户
     */
    private boolean isFirstTimeUser(Long userId, String sessionId) {
        // 检查是否有长期记忆
        List<String> longTermMemories = userRepo.findTopMemory(userId);
        if (longTermMemories != null && !longTermMemories.isEmpty()) {
            return false;
        }

        // 检查是否有会话历史
        return !sessionMemory.hasHistory(sessionId);
    }
}
