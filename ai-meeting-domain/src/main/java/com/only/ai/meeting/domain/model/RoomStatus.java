package com.only.ai.meeting.domain.model;

/**
 * 会议室状态枚举
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public enum RoomStatus {
    /**
     * 可用
     */
    AVAILABLE("可用"),
    
    /**
     * 不可用
     */
    UNAVAILABLE("不可用");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
