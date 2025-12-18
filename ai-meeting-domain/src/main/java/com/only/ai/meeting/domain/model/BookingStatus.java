package com.only.ai.meeting.domain.model;

/**
 * 预约状态枚举
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public enum BookingStatus {
    /**
     * 待开始
     */
    PENDING("待开始"),
    
    /**
     * 已签到
     */
    SIGNED_IN("已签到"),
    
    /**
     * 已结束
     */
    COMPLETED("已结束"),
    
    /**
     * 已取消
     */
    CANCELLED("已取消"),
    
    /**
     * 未签到（会议结束后未签到）
     */
    NOT_SIGNED_IN("未签到");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
