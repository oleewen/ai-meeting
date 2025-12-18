package com.only.ai.meeting.web.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户认证过滤器（简化版，用于演示）
 * MVP阶段：从Header中获取用户ID
 * 后续迭代：集成完整认证系统（JWT、Session等）
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 简化实现：检查是否有用户ID Header
        // 后续可以验证Token、Session等
        String userId = httpRequest.getHeader("X-User-Id");
        
        if (userId == null || userId.trim().isEmpty()) {
            // MVP阶段：如果没有Header，使用默认用户ID
            // 生产环境应该返回401未授权
            userId = "user001";
        }

        // 将用户ID存储到Request属性中，供Controller使用
        httpRequest.setAttribute("currentUserId", userId);

        chain.doFilter(request, response);
    }
}
