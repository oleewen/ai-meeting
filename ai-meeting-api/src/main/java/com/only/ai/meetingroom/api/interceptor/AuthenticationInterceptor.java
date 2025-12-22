package com.only.ai.meetingroom.api.interceptor;

import com.only.ai.meetingroom.api.exception.AuthenticationException;
import com.only.ai.meetingroom.application.service.UserApplicationService;
import com.only.ai.meetingroom.domain.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 用户认证拦截器
 * 验证请求头中的用户ID是否有效
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private UserApplicationService userApplicationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跳过OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 跳过不需要认证的路径
        String requestPath = request.getRequestURI();
        if (isPublicPath(requestPath)) {
            return true;
        }
        
        // 获取用户ID
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.trim().isEmpty()) {
            throw new AuthenticationException("缺少用户认证信息");
        }
        
        // 验证用户是否存在且激活
        Optional<User> userOpt = userApplicationService.getUserInfo(userId);
        if (!userOpt.isPresent()) {
            throw new AuthenticationException("用户不存在");
        }
        
        User user = userOpt.get();
        if (!user.isActive()) {
            throw new AuthenticationException("用户已被停用");
        }
        
        // 将用户信息存储到请求属性中，供后续使用
        request.setAttribute("currentUser", user);
        request.setAttribute("currentUserId", userId);
        
        return true;
    }

    /**
     * 判断是否为公开路径（不需要认证）
     */
    private boolean isPublicPath(String path) {
        // 健康检查等公开接口
        if (path.startsWith("/actuator/") || 
            path.equals("/health") || 
            path.equals("/") ||
            path.startsWith("/swagger-") ||
            path.startsWith("/v3/api-docs")) {
            return true;
        }
        
        // 会议室查询接口可以公开访问（只读操作）
        if (path.startsWith("/api/meeting-rooms") && "GET".equals("GET")) {
            return true;
        }
        
        return false;
    }
}