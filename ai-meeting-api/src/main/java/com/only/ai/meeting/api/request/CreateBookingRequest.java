package com.only.ai.meeting.api.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 创建预约请求
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateBookingRequest {
    @NotNull(message = "会议室ID不能为空")
    private Long roomId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate date;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    @NotBlank(message = "会议主题不能为空")
    @Size(max = 200, message = "会议主题长度不能超过200")
    private String subject;

    @NotNull(message = "参会人数不能为空")
    @Min(value = 1, message = "参会人数必须大于0")
    private Integer attendeeCount;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
}
