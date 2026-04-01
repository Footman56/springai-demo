package com.huochai.aimemory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration.class,

})
@MapperScan("com.huochai.aimemory.mapper")
public class AiMemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMemoryApplication.class, args);
    }

}
