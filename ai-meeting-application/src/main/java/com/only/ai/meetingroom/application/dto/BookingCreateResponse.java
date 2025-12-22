package com.only.ai.meetingroom.application.dto;

import java.time.LocalDateTime;

/**
 * 预约创建响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class BookingCreateResponse {
    
    private String bookingId;
    private String subject;
    private String roomName;
    private String roomLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String message;

    public BookingCreateResponse() {
    }

    public BookingCreateResponse(String bookingId, String subject, String roomName, 
                               String roomLocation, LocalDateTime startTime, 
                               LocalDateTime endTime, String status, String message) {
        this.bookingId = bookingId;
        this.subject = subject;
        this.roomName = roomName;
        this.roomLocation = roomLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomLocation() {
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation) {
        this.roomLocation = roomLocation;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BookingCreateResponse{" +
                "bookingId='" + bookingId + '\'' +
                ", subject='" + subject + '\'' +
                ", roomName='" + roomName + '\'' +
                ", roomLocation='" + roomLocation + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}