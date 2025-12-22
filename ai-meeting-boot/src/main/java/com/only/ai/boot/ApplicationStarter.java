package com.only.ai.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * H2数据库测试启动器
 * 专注于验证H2数据库基本功能
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@SpringBootApplication(scanBasePackages = "com.only.ai")
public class ApplicationStarter {

    public static void main(String[] args) {
        System.out.println("正在启动H2数据库测试系统...");
        SpringApplication.run(ApplicationStarter.class, args);
        System.out.println("H2数据库测试系统启动完成！");
    }
}
