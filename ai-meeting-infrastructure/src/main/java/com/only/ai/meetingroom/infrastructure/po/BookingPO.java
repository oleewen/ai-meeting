package com.only.ai.meetingroom.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约记录持久化对象
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class BookingPO {
    /** 预约ID */
    private Long id;
    /** 会议室ID */
    private Long roomId;
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
    /** 状态（0-待开始，1-已签到，2-已结束，3-已取消） */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
}

