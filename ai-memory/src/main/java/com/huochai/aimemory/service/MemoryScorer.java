package com.huochai.aimemory.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *@author peilizhi
 *@date 2026/3/22 11:46
 **/
@Component
public class MemoryScorer {

    @Autowired
    private ChatClient chatClient;

    /**
     * 对信息进行重要性评分
     * @param content 要评分的内容
     * @return 包含分数的文本描述
     */
    public Double score(String content) {

        String prompt = """
                请给这条信息打重要性评分（0~1）：
                规则：
                - 用户长期偏好 = 高
                - 临时问题 = 低
                
                内容：%s
                
                请直接返回评分结果，格式如：评分: 0.8
                """.formatted(content);

        String contented = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return extractScore(contented);
    }

    /**
     * 从评分文本中提取分数数值
     * @param scoreText 包含分数的文本
     * @return 分数值，如果提取失败返回 -1
     */
    public double extractScore(String scoreText) {
        // 匹配 0.x 或 0.xx 或 0.xxx 格式的数字
        Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(scoreText);

        if (matcher.find()) {
            String numberStr = matcher.group(1);
            // 确保数字在 0-1 范围内
            double score = Double.parseDouble(numberStr);
            if (score >= 0 && score <= 1) {
                return score;
            }
        }
        return -1;
    }
}
