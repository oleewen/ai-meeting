package com.only.ai.meetingroom.domain.model;

/**
 * 用户角色枚举
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public enum UserRole {
    /**
     * 普通用户
     */
    USER("普通用户"),
    
    /**
     * 管理员
     */
    ADMIN("管理员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}