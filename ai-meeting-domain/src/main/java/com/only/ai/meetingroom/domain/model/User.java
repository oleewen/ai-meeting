package com.only.ai.meetingroom.domain.model;

import lombok.Getter;

/**
 * 用户领域模型
 *
 * @author only
 * @since 2024-01-01
 */
@Getter
public class User {
    /** 用户ID */
    private final UserId id;
    /** 用户名 */
    private final String username;
    /** 密码（加密后） */
    private final String password;
    /** 姓名 */
    private final String name;

    public User(UserId id, String username, String password, String name) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String inputPassword) {
        // 简单实现，实际应该使用加密算法（如BCrypt）进行验证
        return this.password.equals(inputPassword);
    }

    /**
     * 用户ID值对象
     */
    public static class UserId extends com.only.ai.common.domain.Id {
        public UserId(Long id) {
            super(id);
        }
    }
}

