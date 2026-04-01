package com.huochai.aimemory.service;

import com.huochai.aimemory.entity.AiUserMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 *@author peilizhi 
 *@date 2026/3/22 11:49
 **/
@Service
public class AdvancedChatService {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private AdvancedMemoryOrchestrator orchestrator;

    @Autowired
    private MemoryExtractor extractor;

    @Autowired
    private MemoryDeduplicator deduplicator;

    @Autowired
    private MemoryTagger tagger;

    @Autowired
    private MemoryScorer scorer;

    @Autowired
    private MemoryVectorService vectorService;

    @Autowired
    private UserMemoryRepository repo;

    @Autowired
    private RedisChatMemory sessionMemory;

    public String chat(Long userId, String sessionId, String prompt) {

        // 1. 构建上下文（Redis + MySQL）
        List<Message> context = orchestrator.build(userId, sessionId, prompt);

        // 2. 调用大模型
        String response = chatClient.prompt()
                .messages(context)
                .user(prompt)
                .call()
                .content();

        // 3. 写入短期记忆（Redis）
        sessionMemory.add(sessionId, prompt);
        sessionMemory.add(sessionId, response);

        // 4. 提取长期记忆候选
        List<String> mems = extractor.extractUserMemory(prompt + response);

        for (String mem : mems) {

            // ===== 4.1 基础过滤 =====
            if (mem == null || mem.length() < 5) continue;

            // ===== 4.2 规则去重 =====
            if (deduplicator.isDuplicate(mem)) continue;

            // ===== 4.3 打分 =====
            double score = scorer.score(mem);
            if (score < 0.6) continue;

            // ===== 4.4 标签 =====
            String tags = tagger.tag(mem);

            // ===== 4.5 向量生成 =====
            float[] embeddingArray = vectorService.embed(mem);

            // 转 byte[]（存 MySQL）
            byte[] embeddingBytes = toBytes(embeddingArray);

            // ===== 4.6 语义去重（关键升级）=====
            AiUserMemory similar = repo.findSimilarMemory(userId, embeddingArray);

            if (similar != null) {
                // 👉 已存在类似记忆 → 更新（不是插入）
                double newScore = Math.max(similar.getImportance(), score);

                repo.updateImportance(similar.getId(), newScore);

                continue;
            }

            // ===== 4.7 插入新记忆 =====
            repo.save(userId, mem, tags, score, embeddingBytes);

            // ===== 4.8 写入向量库（可选 pgvector / milvus）=====
            vectorService.store(userId, mem);
        }

        return response;
    }

    /**
     * 获取会话历史
     */
    public List<String> getSessionHistory(String sessionId) {
        return sessionMemory.get(sessionId);
    }

    /**
     * 清除会话
     */
    public void clearSession(String sessionId) {
        sessionMemory.clear(sessionId);
    }

    private byte[] toBytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * vector.length);
        for (float v : vector) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }
}
