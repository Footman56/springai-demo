package com.huochai.aimemory.controller;

import com.huochai.aimemory.service.AdvancedChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 对话 Controller
 * 支持多轮对话
 *
 * @author peilizhi
 * @date 2026/3/22
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private AdvancedChatService chatService;

    /**
     * 对话请求
     */
    @PostMapping
    public Map<String, Object> chat(@RequestBody ChatRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 如果没有 sessionId，生成一个新的
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            // 获取用户ID（默认1）
            Long userId = request.getUserId() != null ? request.getUserId() : 1L;

            // 调用对话服务
            String response = chatService.chat(userId, sessionId, request.getMessage());

            result.put("success", true);
            result.put("sessionId", sessionId);
            result.put("userId", userId);
            result.put("message", request.getMessage());
            result.put("response", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history/{sessionId}")
    public Map<String, Object> getHistory(@PathVariable String sessionId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> history = chatService.getSessionHistory(sessionId);
            result.put("success", true);
            result.put("history", history);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 清除会话
     */
    @DeleteMapping("/session/{sessionId}")
    public Map<String, Object> clearSession(@PathVariable String sessionId) {
        Map<String, Object> result = new HashMap<>();
        try {
            chatService.clearSession(sessionId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 对话请求 DTO
     */
    public static class ChatRequest {
        private Long userId;
        private String sessionId;
        private String message;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
