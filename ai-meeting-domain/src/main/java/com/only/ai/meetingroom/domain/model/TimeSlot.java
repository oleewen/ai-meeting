package com.only.ai.meetingroom.domain.model;

import com.only.ai.common.domain.ValueObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 时间段值对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class TimeSlot implements ValueObject<TimeSlot> {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeSlot of(LocalDateTime startTime, LocalDateTime endTime) {
        return new TimeSlot(startTime, endTime);
    }

    /**
     * 检查是否与另一个时间段重叠
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
    }

    /**
     * 获取时间段持续时长
     */
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    /**
     * 检查指定时间是否在时间段内
     */
    public boolean contains(LocalDateTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TimeSlot value() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(startTime, timeSlot.startTime) &&
                Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}