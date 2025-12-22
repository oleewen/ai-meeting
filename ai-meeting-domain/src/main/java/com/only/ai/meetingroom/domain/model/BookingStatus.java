package com.only.ai.meetingroom.domain.model;

/**
 * 预约状态枚举
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public enum BookingStatus {
    /**
     * 活跃状态（已预约）
     */
    ACTIVE("活跃"),
    
    /**
     * 已取消
     */
    CANCELLED("已取消"),
    
    /**
     * 已签到
     */
    CHECKED_IN("已签到");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否可以取消
     */
    public boolean canBeCancelled() {
        return this == ACTIVE;
    }

    /**
     * 检查是否可以签到
     */
    public boolean canCheckIn() {
        return this == ACTIVE;
    }
}