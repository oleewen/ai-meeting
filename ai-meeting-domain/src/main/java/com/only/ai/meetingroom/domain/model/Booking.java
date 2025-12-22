package com.only.ai.meetingroom.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约记录领域模型
 *
 * @author only
 * @since 2024-01-01
 */
@Getter
public class Booking {
    /** 预约ID */
    private final BookingId id;
    /** 会议室ID */
    private final Room.RoomId roomId;
    /** 用户ID */
    private final User.UserId userId;
    /** 日期 */
    private final LocalDate date;
    /** 开始时间 */
    private final LocalTime startTime;
    /** 结束时间 */
    private final LocalTime endTime;
    /** 会议主题 */
    private final String subject;
    /** 参会人数 */
    private final Integer attendeeCount;
    /** 备注 */
    private final String remark;
    /** 状态 */
    private final BookingStatus status;
    /** 创建时间 */
    private final LocalDateTime createTime;

    public Booking(BookingId id, Room.RoomId roomId, User.UserId userId, LocalDate date,
                   LocalTime startTime, LocalTime endTime, String subject, Integer attendeeCount,
                   String remark, BookingStatus status, LocalDateTime createTime) {
        if (roomId == null) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (date == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("结束时间不能为空");
        }
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("开始时间必须早于结束时间");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("会议主题不能为空");
        }
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.attendeeCount = attendeeCount;
        this.remark = remark;
        this.status = status != null ? status : BookingStatus.PENDING;
        this.createTime = createTime != null ? createTime : LocalDateTime.now();
    }

    /**
     * 检查时间段是否与另一个预约重叠
     */
    public boolean overlapsWith(Booking other) {
        if (!this.date.equals(other.date)) {
            return false;
        }
        if (!this.roomId.equals(other.roomId)) {
            return false;
        }
        // 检查时间段是否重叠
        return !(this.endTime.isBefore(other.startTime) || this.endTime.equals(other.startTime) ||
                 this.startTime.isAfter(other.endTime) || this.startTime.equals(other.endTime));
    }

    /**
     * 取消预约
     */
    public Booking cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("预约已取消");
        }
        if (this.status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("已完成的预约不能取消");
        }
        return new Booking(this.id, this.roomId, this.userId, this.date, this.startTime,
                this.endTime, this.subject, this.attendeeCount, this.remark,
                BookingStatus.CANCELLED, this.createTime);
    }

    /**
     * 签到
     */
    public Booking checkIn() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("已取消的预约不能签到");
        }
        if (this.status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("已完成的预约不能签到");
        }
        return new Booking(this.id, this.roomId, this.userId, this.date, this.startTime,
                this.endTime, this.subject, this.attendeeCount, this.remark,
                BookingStatus.CHECKED_IN, this.createTime);
    }

    /**
     * 预约ID值对象
     */
    public static class BookingId extends com.only.ai.common.domain.Id {
        public BookingId(Long id) {
            super(id);
        }
    }

    /**
     * 预约状态枚举
     */
    public enum BookingStatus {
        /** 待开始 */
        PENDING,
        /** 已签到 */
        CHECKED_IN,
        /** 已结束 */
        COMPLETED,
        /** 已取消 */
        CANCELLED
    }
}

