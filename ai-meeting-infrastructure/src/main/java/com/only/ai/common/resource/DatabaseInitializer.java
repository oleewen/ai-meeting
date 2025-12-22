package com.only.ai.common.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * 数据库初始化器
 * 如果Spring Boot的自动初始化不工作，使用此方式执行SQL脚本
 * 仅在spring.sql.init.mode不为always时启用（作为备用方案）
 *
 * @author only
 * @since 2024-01-01
 */
@Component
@ConditionalOnProperty(name = "spring.sql.init.mode", havingValue = "never", matchIfMissing = false)
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // 检查表是否已存在
            String checkTableSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ROOM'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            
            if (tableCount == null || tableCount == 0) {
                // 执行DDL脚本
                ClassPathResource schemaResource = new ClassPathResource("db/schema-h2.sql");
                String schemaSql = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8);
                executeScript(schemaSql);
                
                // 执行DML脚本
                ClassPathResource dataResource = new ClassPathResource("db/data-h2.sql");
                String dataSql = StreamUtils.copyToString(dataResource.getInputStream(), StandardCharsets.UTF_8);
                executeScript(dataSql);
                
                System.out.println("数据库初始化完成");
            }
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
            // 不抛出异常，允许应用继续启动（可能表已存在）
        }
    }

    private void executeScript(String sql) {
        // 分割SQL语句（以分号和换行符分割）
        String[] statements = sql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    jdbcTemplate.execute(trimmed);
                } catch (Exception e) {
                    // 忽略已存在的表等错误
                    String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                    if (!errorMsg.contains("already exists") && 
                        !errorMsg.contains("duplicate") &&
                        !errorMsg.contains("table") && 
                        !errorMsg.contains("index")) {
                        System.err.println("执行SQL失败: " + trimmed.substring(0, Math.min(50, trimmed.length())) + "...");
                        System.err.println("错误: " + e.getMessage());
                    }
                }
            }
        }
    }
}

