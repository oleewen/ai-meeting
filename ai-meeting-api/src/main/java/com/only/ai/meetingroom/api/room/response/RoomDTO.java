package com.only.ai.meetingroom.api.room.response;

import lombok.Data;

import java.util.List;

/**
 * 会议室DTO
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class RoomDTO {
    /** 会议室ID */
    private Long id;
    /** 会议室名称 */
    private String name;
    /** 地点/楼层 */
    private String location;
    /** 可容纳人数 */
    private Integer capacity;
    /** 支持设备列表 */
    private List<String> equipment;
    /** 状态 */
    private String status;
}

