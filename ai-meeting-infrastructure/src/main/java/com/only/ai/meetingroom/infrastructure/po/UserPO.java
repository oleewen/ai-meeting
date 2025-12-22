package com.only.ai.meetingroom.infrastructure.po;

import lombok.Data;

/**
 * 用户持久化对象
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class UserPO {
    /** 用户ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 密码（加密后） */
    private String password;
    /** 姓名 */
    private String name;
}

