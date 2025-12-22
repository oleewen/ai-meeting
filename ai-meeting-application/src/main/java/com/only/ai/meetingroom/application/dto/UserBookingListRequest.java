package com.only.ai.meetingroom.application.dto;

import java.time.LocalDate;

/**
 * 用户预约列表查询请求DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class UserBookingListRequest {
    
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public UserBookingListRequest() {
    }

    public UserBookingListRequest(String userId, LocalDate startDate, LocalDate endDate, String status) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserBookingListRequest{" +
                "userId='" + userId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}