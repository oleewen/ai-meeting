package com.only.ai.meeting.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约领域模型
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class Booking {
    private Long id;
    private String userId;
    private String userName;
    private Long roomId;
    private String roomName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subject;
    private Integer attendeeCount;
    private String remark;
    private BookingStatus status;
    private LocalDateTime signInTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Booking() {
        this.status = BookingStatus.PENDING;
    }

    /**
     * 验证预约信息
     */
    public void validate() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        if (date == null) {
            throw new IllegalArgumentException("预约日期不能为空");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("开始时间必须早于结束时间");
        }
        if (subject == null || subject.trim().isEmpty() || subject.length() > 200) {
            throw new IllegalArgumentException("会议主题不能为空且长度不能超过200");
        }
        if (attendeeCount == null || attendeeCount <= 0) {
            throw new IllegalArgumentException("参会人数必须大于0");
        }
    }

    /**
     * 检查会议时长是否在允许范围内（30分钟到4小时）
     */
    public boolean isDurationValid() {
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return minutes >= 30 && minutes <= 240; // 30分钟到4小时
    }

    /**
     * 检查是否可以签到（会议开始前10分钟至开始后15分钟）
     */
    public boolean canSignIn(LocalDateTime now) {
        if (status != BookingStatus.PENDING) {
            return false;
        }
        LocalDateTime meetingStart = LocalDateTime.of(date, startTime);
        LocalDateTime signInStart = meetingStart.minusMinutes(10);
        LocalDateTime signInEnd = meetingStart.plusMinutes(15);
        return !now.isBefore(signInStart) && !now.isAfter(signInEnd);
    }

    /**
     * 签到
     */
    public void signIn(LocalDateTime signInTime) {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("只有待开始的预约才能签到");
        }
        this.status = BookingStatus.SIGNED_IN;
        this.signInTime = signInTime;
    }

    /**
     * 取消预约
     */
    public void cancel() {
        if (status == BookingStatus.CANCELLED || status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("已取消或已结束的预约不能再次取消");
        }
        this.status = BookingStatus.CANCELLED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(Integer attendeeCount) {
        this.attendeeCount = attendeeCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(LocalDateTime signInTime) {
        this.signInTime = signInTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
