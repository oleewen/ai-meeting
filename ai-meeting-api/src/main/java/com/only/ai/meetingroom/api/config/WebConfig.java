package com.only.ai.meetingroom.api.config;

import com.only.ai.meetingroom.api.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Web配置类
 * 配置拦截器、CORS等
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/meeting-rooms/**",  // 会议室查询接口公开
                    "/actuator/**",           // 健康检查
                    "/swagger-ui/**",         // API文档
                    "/v3/api-docs/**"         // API文档
                );
    }
}