package com.only.ai.meeting.application.query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室查询参数
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class RoomQuery {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Integer minCapacity;
    private List<String> equipment;

    public RoomQuery() {
    }

    public RoomQuery(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
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

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }
}
