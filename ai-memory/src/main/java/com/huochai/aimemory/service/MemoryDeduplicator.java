package com.huochai.aimemory.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *@author peilizhi 
 *@date 2026/3/22 11:40
 **/

@Component
public class MemoryDeduplicator {

    @Autowired
    private VectorStore vectorStore;

    public boolean isDuplicate(String content) {

        List<Document> docs = vectorStore.similaritySearch(content);

        return docs.stream().anyMatch(d ->
                similarity(d.getText(), content) > 0.9
        );
    }

    private double similarity(String a, String b) {
        return a.equals(b) ? 1.0 : 0.0; // 可替换为 embedding 相似度
    }
}
