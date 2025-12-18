package com.only.ai.meeting.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置 (使用SpringDoc OpenAPI)
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("会议室预约系统 API")
                        .description("会议室预约系统的RESTful API接口文档")
                        .version("1.0.0"));
    }
}
