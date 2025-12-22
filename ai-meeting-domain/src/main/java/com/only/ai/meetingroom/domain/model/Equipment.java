package com.only.ai.meetingroom.domain.model;

/**
 * 会议室设备枚举
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public enum Equipment {
    /**
     * 投影仪
     */
    PROJECTOR("投影仪"),
    
    /**
     * 视频会议设备
     */
    VIDEO_CONFERENCE("视频会议设备"),
    
    /**
     * 白板
     */
    WHITEBOARD("白板"),
    
    /**
     * 大屏幕
     */
    LARGE_SCREEN("大屏幕");

    private final String description;

    Equipment(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}