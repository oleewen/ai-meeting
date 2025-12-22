package com.only.ai.meetingroom.web.controller;

import com.only.ai.meetingroom.api.auth.request.LoginRequest;
import com.only.ai.meetingroom.api.auth.response.LoginResponse;
import com.only.ai.meetingroom.api.common.response.ApiResponse;
import com.only.ai.meetingroom.application.service.AuthApplicationService;
import com.only.ai.meetingroom.domain.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 认证控制器
 *
 * @author only
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<User> userOpt = authApplicationService.login(request.getUsername(), request.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("userId", user.getId().value());
            session.setAttribute("username", user.getUsername());

            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId().value());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            return ApiResponse.success(response);
        } else {
            return ApiResponse.error("LOGIN_FAILED", "用户名或密码错误");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success(null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ApiResponse<LoginResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("NOT_LOGIN", "未登录");
        }
        Optional<User> userOpt = authApplicationService.getUserById(new User.UserId(userId));
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId().value());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            return ApiResponse.success(response);
        } else {
            return ApiResponse.error("USER_NOT_FOUND", "用户不存在");
        }
    }
}

