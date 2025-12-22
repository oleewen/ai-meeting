package com.only.ai.meetingroom.infrastructure.po;

import lombok.Data;

/**
 * 会议室持久化对象
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class RoomPO {
    /** 会议室ID */
    private Long id;
    /** 会议室名称 */
    private String name;
    /** 地点/楼层 */
    private String location;
    /** 可容纳人数 */
    private Integer capacity;
    /** 支持设备（JSON格式存储） */
    private String equipment;
    /** 状态（0-可用，1-不可用） */
    private Integer status;
}

