package com.only.ai.meetingroom.application.dto;

/**
 * 签到请求DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class CheckInRequest {
    
    private String bookingId;
    private String userId;

    public CheckInRequest() {
    }

    public CheckInRequest(String bookingId, String userId) {
        this.bookingId = bookingId;
        this.userId = userId;
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CheckInRequest{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}