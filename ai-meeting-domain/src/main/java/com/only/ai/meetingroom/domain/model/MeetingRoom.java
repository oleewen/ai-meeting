package com.only.ai.meetingroom.domain.model;

import java.util.Objects;
import java.util.Set;

/**
 * 会议室实体
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class MeetingRoom {
    private MeetingRoomId id;
    private String name;
    private String location;
    private int capacity;
    private Set<Equipment> equipments;
    private boolean active;

    public MeetingRoom(MeetingRoomId id, String name, String location, int capacity, Set<Equipment> equipments) {
        if (id == null) {
            throw new IllegalArgumentException("Meeting room id cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting room name cannot be null or empty");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting room location cannot be null or empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Meeting room capacity must be positive");
        }
        
        this.id = id;
        this.name = name.trim();
        this.location = location.trim();
        this.capacity = capacity;
        this.equipments = equipments;
        this.active = true;
    }

    /**
     * 检查会议室是否有指定设备
     */
    public boolean hasEquipment(Equipment equipment) {
        return equipments != null && equipments.contains(equipment);
    }

    /**
     * 检查会议室容量是否满足要求
     */
    public boolean canAccommodate(int requiredCapacity) {
        return this.capacity >= requiredCapacity;
    }

    /**
     * 激活会议室
     */
    public void activate() {
        this.active = true;
    }

    /**
     * 停用会议室
     */
    public void deactivate() {
        this.active = false;
    }

    // Getters
    public MeetingRoomId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<Equipment> getEquipments() {
        return equipments;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingRoom that = (MeetingRoom) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MeetingRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", equipments=" + equipments +
                ", active=" + active +
                '}';
    }
}