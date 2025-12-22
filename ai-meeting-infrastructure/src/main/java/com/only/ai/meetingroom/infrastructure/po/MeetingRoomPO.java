package com.only.ai.meetingroom.infrastructure.po;

import com.only.ai.meetingroom.domain.model.Equipment;
import com.only.ai.meetingroom.domain.model.MeetingRoom;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议室持久化对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class MeetingRoomPO {
    private String id;
    private String name;
    private String location;
    private Integer capacity;
    private String equipments; // JSON字符串存储
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MeetingRoomPO() {}

    public static MeetingRoomPO fromDomain(MeetingRoom meetingRoom) {
        MeetingRoomPO po = new MeetingRoomPO();
        po.id = meetingRoom.getId().value();
        po.name = meetingRoom.getName();
        po.location = meetingRoom.getLocation();
        po.capacity = meetingRoom.getCapacity();
        po.active = meetingRoom.isActive();
        
        // 将设备集合转换为JSON字符串
        if (meetingRoom.getEquipments() != null && !meetingRoom.getEquipments().isEmpty()) {
            String equipmentNames = meetingRoom.getEquipments().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
            po.equipments = "[\"" + equipmentNames.replace(",", "\",\"") + "\"]";
        } else {
            po.equipments = "[]";
        }
        
        return po;
    }

    public MeetingRoom toDomain() {
        Set<Equipment> equipmentSet = parseEquipments(this.equipments);
        return new MeetingRoom(
                MeetingRoomId.of(this.id),
                this.name,
                this.location,
                this.capacity,
                equipmentSet
        );
    }

    private Set<Equipment> parseEquipments(String equipmentsJson) {
        if (equipmentsJson == null || equipmentsJson.trim().isEmpty() || "[]".equals(equipmentsJson)) {
            return new HashSet<>();
        }
        
        try {
            // 简单解析JSON数组格式的设备列表
            String cleaned = equipmentsJson.replace("[", "").replace("]", "").replace("\"", "");
            if (cleaned.trim().isEmpty()) {
                return new HashSet<>();
            }
            
            return Arrays.stream(cleaned.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Equipment::valueOf)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getEquipments() {
        return equipments;
    }

    public void setEquipments(String equipments) {
        this.equipments = equipments;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}