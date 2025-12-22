package com.only.ai.meetingroom.api.booking.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 创建预约请求
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class CreateBookingRequest {
    /** 会议室ID */
    private Long roomId;
    /** 日期 */
    private LocalDate date;
    /** 开始时间 */
    private LocalTime startTime;
    /** 结束时间 */
    private LocalTime endTime;
    /** 会议主题 */
    private String subject;
    /** 参会人数（可选） */
    private Integer attendeeCount;
    /** 备注（可选） */
    private String remark;
}

