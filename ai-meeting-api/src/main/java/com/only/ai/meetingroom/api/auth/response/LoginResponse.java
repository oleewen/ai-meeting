package com.only.ai.meetingroom.api.auth.response;

import lombok.Data;

/**
 * 登录响应
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class LoginResponse {
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 姓名 */
    private String name;
}

