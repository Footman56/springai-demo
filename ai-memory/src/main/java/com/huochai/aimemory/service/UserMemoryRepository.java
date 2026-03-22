package com.huochai.aimemory.service;

import com.huochai.aimemory.entity.AiUserMemory;
import com.huochai.aimemory.mapper.AiUserMemoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 用户记忆仓库
 * 使用 tk-mybatis 操作 MySQL
 *
 * @author peilizhi
 * @date 2026/3/22
 */
@Repository
public class UserMemoryRepository {

    @Autowired
    private AiUserMemoryMapper aiUserMemoryMapper;

    /**
     * 保存记忆
     */
    public void save(Long userId, String content, String tags, double score, byte[] embeddingBytes) {
        AiUserMemory memory = new AiUserMemory();
        memory.setUserId(userId);
        memory.setContent(content);
        memory.setTags(tags);
        memory.setImportance(score);
        memory.setEmbedding(embeddingBytes);
        aiUserMemoryMapper.insert(memory);
    }

    /**
     * 查询用户重要记忆
     */
    public List<String> findTopMemory(Long userId) {
        List<AiUserMemory> memories = aiUserMemoryMapper.selectByUserIdOrderByImportance(userId);
        if (memories == null || memories.isEmpty()) {
            return List.of();
        }
        return memories.stream()
                .map(AiUserMemory::getContent)
                .limit(5)
                .toList();
    }

    /**
     * 查询用户的记忆列表
     */
    public List<AiUserMemory> findByUserId(Long userId) {
        return aiUserMemoryMapper.selectByUserIdOrderByImportance(userId);
    }

    /**
     * 查找相似记忆
     */
    public AiUserMemory findSimilarMemory(Long userId, float[] embedding) {
        List<AiUserMemory> list = aiUserMemoryMapper.selectByUserIdOrderByImportance(userId);

        for (AiUserMemory mem : list) {
            float[] dbVec = fromBytes(mem.getEmbedding());
            if (dbVec == null) continue;

            double sim = cosineSimilarity(embedding, dbVec);

            if (sim > 0.9) {
                return mem;
            }
        }

        return null;
    }

    /**
     * 更新重要性分数
     */
    public void updateImportance(Long id, Double importance) {
        aiUserMemoryMapper.updateImportance(id, importance);
    }

    /**
     * byte[] 转 float[]
     */
    public static float[] fromBytes(byte[] bytes) {
        if (bytes == null) return null;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        float[] result = new float[bytes.length / 4];
        for (int i = 0; i < result.length; i++) {
            result[i] = buffer.getFloat(i * 4);
        }
        return result;
    }

    /**
     * 余弦相似度计算
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) {
            return 0.0;
        }

        double dot = 0, normA = 0, normB = 0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
