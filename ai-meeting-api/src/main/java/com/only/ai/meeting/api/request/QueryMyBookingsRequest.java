package com.only.ai.meeting.api.request;

import java.time.LocalDate;

/**
 * 查询我的预约请求
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class QueryMyBookingsRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    // Getters and Setters
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
}
