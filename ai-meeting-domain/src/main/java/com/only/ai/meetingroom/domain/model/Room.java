package com.only.ai.meetingroom.domain.model;

import lombok.Getter;

import java.util.List;

/**
 * 会议室领域模型
 *
 * @author only
 * @since 2024-01-01
 */
@Getter
public class Room {
    /** 会议室ID */
    private final RoomId id;
    /** 会议室名称 */
    private final String name;
    /** 地点/楼层 */
    private final String location;
    /** 可容纳人数 */
    private final Integer capacity;
    /** 支持设备列表 */
    private final List<String> equipment;
    /** 状态（可用/不可用） */
    private final RoomStatus status;

    public Room(RoomId id, String name, String location, Integer capacity, List<String> equipment, RoomStatus status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("会议室名称不能为空");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("地点不能为空");
        }
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("容量必须大于0");
        }
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.equipment = equipment != null ? List.copyOf(equipment) : List.of();
        this.status = status != null ? status : RoomStatus.AVAILABLE;
    }

    /**
     * 会议室ID值对象
     */
    public static class RoomId extends com.only.ai.common.domain.Id {
        public RoomId(Long id) {
            super(id);
        }
    }

    /**
     * 会议室状态枚举
     */
    public enum RoomStatus {
        /** 可用 */
        AVAILABLE,
        /** 不可用 */
        UNAVAILABLE
    }
}

