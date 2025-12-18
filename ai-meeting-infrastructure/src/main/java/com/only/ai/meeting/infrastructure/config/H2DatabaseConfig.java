package com.only.ai.meeting.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * H2数据库配置
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Configuration
@Profile({"dev", "test"})
public class H2DatabaseConfig {
    // H2数据库配置通过application.properties中的spring.datasource配置
    // 本类用于标识H2数据库配置，可在需要时添加H2特定的配置
}
