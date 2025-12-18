package com.only.ai.meeting.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议室领域模型
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class MeetingRoom {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private List<String> equipment;
    private RoomStatus status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public MeetingRoom() {
    }

    public MeetingRoom(Long id, String name, String location, Integer capacity, 
                      List<String> equipment, RoomStatus status, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.equipment = equipment;
        this.status = status;
        this.description = description;
    }

    /**
     * 验证会议室信息
     */
    public void validate() {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("会议室名称不能为空且长度不能超过100");
        }
        if (location == null || location.trim().isEmpty() || location.length() > 100) {
            throw new IllegalArgumentException("地点不能为空且长度不能超过100");
        }
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("容量必须大于0");
        }
        if (status == null) {
            this.status = RoomStatus.AVAILABLE;
        }
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
