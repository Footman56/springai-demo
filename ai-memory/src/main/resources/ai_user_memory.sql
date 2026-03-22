CREATE TABLE ai_user_memory (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT,
                                content TEXT,
                                tags VARCHAR(255),
                                importance DOUBLE,
                                embedding BLOB,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);