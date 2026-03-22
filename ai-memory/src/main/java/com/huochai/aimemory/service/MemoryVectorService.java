package com.huochai.aimemory.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *@author peilizhi
 *@date 2026/3/22 11:47
 **/
@Service
public class MemoryVectorService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    public void store(Long userId, String content) {

        Document doc = new Document(content);
        doc.getMetadata().put("userId", userId);

        vectorStore.add(List.of(doc));
    }

    public List<String> search(Long userId, String query) {
        try {
            return vectorStore.similaritySearch(query).stream()
                    .filter(d -> userId.equals(d.getMetadata().get("userId")))
                    .map(Document::getText)
                    .toList();
        } catch (Exception e) {
            // 向量库为空或查询失败时返回空列表
            return List.of();
        }
    }

    /**
     * 生成文本向量
     */
    public float[] embed(String content) {
        return embeddingModel.embed(content);
    }
}
