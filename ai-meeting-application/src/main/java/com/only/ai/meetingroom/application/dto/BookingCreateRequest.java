package com.only.ai.meetingroom.application.dto;

import java.time.LocalDateTime;

/**
 * 预约创建请求DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class BookingCreateRequest {
    
    private String meetingRoomId;
    private String userId;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeeCount;
    private String notes;

    public BookingCreateRequest() {
    }

    public BookingCreateRequest(String meetingRoomId, String userId, String subject, 
                              LocalDateTime startTime, LocalDateTime endTime, 
                              int attendeeCount, String notes) {
        this.meetingRoomId = meetingRoomId;
        this.userId = userId;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeeCount = attendeeCount;
        this.notes = notes;
    }

    // Getters and Setters
    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public int getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(int attendeeCount) {
        this.attendeeCount = attendeeCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "BookingCreateRequest{" +
                "meetingRoomId='" + meetingRoomId + '\'' +
                ", userId='" + userId + '\'' +
                ", subject='" + subject + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", attendeeCount=" + attendeeCount +
                ", notes='" + notes + '\'' +
                '}';
    }
}