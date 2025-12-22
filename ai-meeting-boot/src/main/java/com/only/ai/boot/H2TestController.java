package com.only.ai.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H2数据库测试控制器
 * 用于验证H2数据库连接和基本操作
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@RestController
public class H2TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    /**
     * 测试数据库连接
     */
    @GetMapping("/test/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 测试数据库连接
            String dbUrl = dataSource.getConnection().getMetaData().getURL();
            result.put("status", "SUCCESS");
            result.put("database_url", dbUrl);
            result.put("message", "H2数据库连接成功");
            
            // 测试简单查询
            String version = jdbcTemplate.queryForObject("SELECT H2VERSION()", String.class);
            result.put("h2_version", version);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试创建表和插入数据
     */
    @GetMapping("/test/table")
    public Map<String, Object> testTable() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 创建测试表
            jdbcTemplate.execute("DROP TABLE IF EXISTS test_table");
            jdbcTemplate.execute("CREATE TABLE test_table (id INT PRIMARY KEY, name VARCHAR(50))");
            
            // 插入测试数据
            jdbcTemplate.update("INSERT INTO test_table (id, name) VALUES (?, ?)", 1, "测试数据1");
            jdbcTemplate.update("INSERT INTO test_table (id, name) VALUES (?, ?)", 2, "测试数据2");
            
            // 查询数据
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM test_table");
            
            result.put("status", "SUCCESS");
            result.put("message", "表创建和数据操作成功");
            result.put("data", rows);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 查看所有表
     */
    @GetMapping("/test/tables")
    public Map<String, Object> showTables() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'"
            );
            
            result.put("status", "SUCCESS");
            result.put("tables", tables);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        return result;
    }
}