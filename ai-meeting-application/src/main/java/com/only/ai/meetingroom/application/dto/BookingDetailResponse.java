package com.only.ai.meetingroom.application.dto;

import java.time.LocalDateTime;

/**
 * 预约详情响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class BookingDetailResponse {
    
    private String bookingId;
    private String subject;
    private String roomName;
    private String roomLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeeCount;
    private String notes;
    private String status;
    private LocalDateTime checkedInAt;
    private boolean canCancel;
    private boolean canCheckIn;

    public BookingDetailResponse() {
    }

    public BookingDetailResponse(String bookingId, String subject, String roomName, 
                               String roomLocation, LocalDateTime startTime, LocalDateTime endTime, 
                               int attendeeCount, String notes, String status, 
                               LocalDateTime checkedInAt, boolean canCancel, boolean canCheckIn) {
        this.bookingId = bookingId;
        this.subject = subject;
        this.roomName = roomName;
        this.roomLocation = roomLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeeCount = attendeeCount;
        this.notes = notes;
        this.status = status;
        this.checkedInAt = checkedInAt;
        this.canCancel = canCancel;
        this.canCheckIn = canCheckIn;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean isCanCheckIn() {
        return canCheckIn;
    }

    public void setCanCheckIn(boolean canCheckIn) {
        this.canCheckIn = canCheckIn;
    }

    @Override
    public String toString() {
        return "BookingDetailResponse{" +
                "bookingId='" + bookingId + '\'' +
                ", subject='" + subject + '\'' +
                ", roomName='" + roomName + '\'' +
                ", roomLocation='" + roomLocation + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", attendeeCount=" + attendeeCount +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                ", checkedInAt=" + checkedInAt +
                ", canCancel=" + canCancel +
                ", canCheckIn=" + canCheckIn +
                '}';
    }
}