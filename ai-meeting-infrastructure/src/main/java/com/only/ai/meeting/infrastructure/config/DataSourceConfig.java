package com.only.ai.meeting.infrastructure.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * 数据源配置（H2内存数据库）
 * 使用Druid连接池
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Configuration
@Profile({"dev", "test"})
public class DataSourceConfig {

    @Value("${spring.datasource.url:jdbc:h2:mem:meetingdb;DB_CLOSE_DELAY=-1;MODE=MySQL}")
    private String url;

    @Value("${spring.datasource.username:sa}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.h2.Driver}")
    private String driverClassName;

    @Value("${spring.datasource.druid.initial-size:5}")
    private int initialSize;

    @Value("${spring.datasource.druid.max-active:20}")
    private int maxActive;

    @Value("${spring.datasource.druid.min-idle:5}")
    private int minIdle;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        
        // 连接池配置
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        
        // 连接验证
        dataSource.setTimeBetweenEvictionRunsMillis(30000);
        dataSource.setMinEvictableIdleTimeMillis(15000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        
        return dataSource;
    }
}
