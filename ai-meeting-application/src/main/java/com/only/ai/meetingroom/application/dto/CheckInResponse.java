package com.only.ai.meetingroom.application.dto;

import java.time.LocalDateTime;

/**
 * 签到响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class CheckInResponse {
    
    private String bookingId;
    private String subject;
    private LocalDateTime checkedInAt;
    private String message;

    public CheckInResponse() {
    }

    public CheckInResponse(String bookingId, String subject, LocalDateTime checkedInAt, String message) {
        this.bookingId = bookingId;
        this.subject = subject;
        this.checkedInAt = checkedInAt;
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

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CheckInResponse{" +
                "bookingId='" + bookingId + '\'' +
                ", subject='" + subject + '\'' +
                ", checkedInAt=" + checkedInAt +
                ", message='" + message + '\'' +
                '}';
    }
}