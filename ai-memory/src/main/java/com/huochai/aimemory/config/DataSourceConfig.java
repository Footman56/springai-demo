package com.huochai.aimemory.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据源配置
 * MySQL: 主数据库
 * PostgreSQL: 向量数据库
 *
 * @author peilizhi
 * @date 2026/3/22
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String mysqlDriverClassName;

    @Value("${spring.pgvector.datasource.url}")
    private String pgUrl;

    @Value("${spring.pgvector.datasource.username}")
    private String pgUsername;

    @Value("${spring.pgvector.datasource.password}")
    private String pgPassword;

    @Value("${spring.pgvector.datasource.driver-class-name}")
    private String pgDriverClassName;

    /**
     * MySQL 数据源（主数据库 - 默认）
     */
    @Bean
    @Primary
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
                .type(BasicDataSource.class)
                .url(mysqlUrl)
                .username(mysqlUsername)
                .password(mysqlPassword)
                .driverClassName(mysqlDriverClassName)
                .build();
    }

    /**
     * MySQL JdbcTemplate
     */
    @Bean
    @Primary
    public JdbcTemplate mysqlJdbcTemplate(DataSource mysqlDataSource) {
        return new JdbcTemplate(mysqlDataSource);
    }

    /**
     * PostgreSQL 数据源（向量数据库）
     */
    @Bean(name = "pgVectorDataSource")
    /**
     * PostgreSQL 数据源（向量数据库）
     */
    public DataSource pgDataSource() {
        return DataSourceBuilder.create()
                .type(BasicDataSource.class)
                .url(pgUrl)
                .username(pgUsername)
                .password(pgPassword)
                .driverClassName(pgDriverClassName)
                .build();
    }


    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate pgVectorJdbcTemplate(
            @Qualifier("pgVectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public VectorStore vectorStore(
            @Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
            EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1024)                    // text-embedding-v3 维度
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(true)              // 自动建表（需确保 vector 扩展已安装）
                .vectorTableName("ai_store")
                .build();
    }
}
