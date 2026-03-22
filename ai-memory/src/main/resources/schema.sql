-- MySQL 主数据库初始化脚本
-- 用户记忆表

CREATE TABLE IF NOT EXISTS ai_user_memory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT,
    tags VARCHAR(255),
    importance DOUBLE DEFAULT 0.0,
    embedding BLOB,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_importance (importance DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
