package com.only.ai.meetingroom.application.dto;

import com.only.ai.meetingroom.domain.model.Equipment;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 会议室搜索请求DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class MeetingRoomSearchRequest {
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer minCapacity;
    private Set<Equipment> requiredEquipments;
    private String sortBy;

    public MeetingRoomSearchRequest() {
    }

    public MeetingRoomSearchRequest(LocalDateTime startTime, LocalDateTime endTime, 
                                  String location, Integer minCapacity, 
                                  Set<Equipment> requiredEquipments, String sortBy) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.minCapacity = minCapacity;
        this.requiredEquipments = requiredEquipments;
        this.sortBy = sortBy;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }

    public Set<Equipment> getRequiredEquipments() {
        return requiredEquipments;
    }

    public void setRequiredEquipments(Set<Equipment> requiredEquipments) {
        this.requiredEquipments = requiredEquipments;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public String toString() {
        return "MeetingRoomSearchRequest{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", minCapacity=" + minCapacity +
                ", requiredEquipments=" + requiredEquipments +
                ", sortBy='" + sortBy + '\'' +
                '}';
    }
}