package com.only.ai.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 最简单的启动器
 * 用于测试H2数据库基本功能
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.only.ai"})
@RestController
public class SimpleStarter {

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "H2数据库测试系统运行正常");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 系统信息接口
     */
    @GetMapping("/system-info")
    public Map<String, Object> systemInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("application", "AI会议室预约系统-H2测试版");
        result.put("version", "1.0.0-SNAPSHOT");
        result.put("database", "H2内存数据库");
        result.put("description", "基于Spring Boot的H2数据库测试系统");
        return result;
    }

    public static void main(String[] args) {
        System.out.println("正在启动H2数据库测试系统...");
        SpringApplication.run(SimpleStarter.class, args);
        System.out.println("H2数据库测试系统启动完成！");
    }
}