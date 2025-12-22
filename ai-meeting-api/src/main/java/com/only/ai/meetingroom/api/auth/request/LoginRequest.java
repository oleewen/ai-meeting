package com.only.ai.meetingroom.api.auth.request;

import lombok.Data;

/**
 * 登录请求
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class LoginRequest {
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
}

