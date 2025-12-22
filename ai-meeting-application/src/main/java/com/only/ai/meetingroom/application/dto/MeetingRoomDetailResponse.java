package com.only.ai.meetingroom.application.dto;

import com.only.ai.meetingroom.domain.model.Equipment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 会议室详情响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class MeetingRoomDetailResponse {
    
    private String id;
    private String name;
    private String location;
    private int capacity;
    private Set<Equipment> equipments;
    private LocalDate date;
    private List<TimeSlotInfo> timeSlots;

    public MeetingRoomDetailResponse() {
    }

    public MeetingRoomDetailResponse(String id, String name, String location, int capacity, 
                                   Set<Equipment> equipments, LocalDate date, List<TimeSlotInfo> timeSlots) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.equipments = equipments;
        this.date = date;
        this.timeSlots = timeSlots;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(Set<Equipment> equipments) {
        this.equipments = equipments;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<TimeSlotInfo> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotInfo> timeSlots) {
        this.timeSlots = timeSlots;
    }

    /**
     * 时间段信息内部类
     */
    public static class TimeSlotInfo {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String subject;
        private String status;

        public TimeSlotInfo() {
        }

        public TimeSlotInfo(LocalDateTime startTime, LocalDateTime endTime, String subject, String status) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.subject = subject;
            this.status = status;
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

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "TimeSlotInfo{" +
                    "startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", subject='" + subject + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MeetingRoomDetailResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", equipments=" + equipments +
                ", date=" + date +
                ", timeSlots=" + timeSlots +
                '}';
        }
}