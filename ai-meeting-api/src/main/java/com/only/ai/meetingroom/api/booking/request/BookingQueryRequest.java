package com.only.ai.meetingroom.api.booking.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * 预约查询请求
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class BookingQueryRequest {
    /** 开始日期（可选） */
    private LocalDate startDate;
    /** 结束日期（可选） */
    private LocalDate endDate;
    /** 状态筛选（可选） */
    private String status;
}

