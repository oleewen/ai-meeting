package com.only.ai.meetingroom.api.booking.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约DTO
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class BookingDTO {
    /** 预约ID */
    private Long id;
    /** 会议室ID */
    private Long roomId;
    /** 会议室名称 */
    private String roomName;
    /** 用户ID */
    private Long userId;
    /** 日期 */
    private LocalDate date;
    /** 开始时间 */
    private LocalTime startTime;
    /** 结束时间 */
    private LocalTime endTime;
    /** 会议主题 */
    private String subject;
    /** 参会人数 */
    private Integer attendeeCount;
    /** 备注 */
    private String remark;
    /** 状态 */
    private String status;
    /** 创建时间 */
    private LocalDateTime createTime;
}

