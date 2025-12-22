package com.only.ai.meetingroom.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 预约实体
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class Booking {
    private BookingId id;
    private MeetingRoomId meetingRoomId;
    private UserId userId;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeeCount;
    private String notes;
    private BookingStatus status;
    private LocalDateTime checkedInAt;

    public Booking(BookingId id, MeetingRoomId meetingRoomId, UserId userId, String subject,
                   LocalDateTime startTime, LocalDateTime endTime, int attendeeCount, String notes) {
        if (id == null) {
            throw new IllegalArgumentException("Booking id cannot be null");
        }
        if (meetingRoomId == null) {
            throw new IllegalArgumentException("Meeting room id cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (attendeeCount <= 0) {
            throw new IllegalArgumentException("Attendee count must be positive");
        }

        this.id = id;
        this.meetingRoomId = meetingRoomId;
        this.userId = userId;
        this.subject = subject.trim();
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeeCount = attendeeCount;
        this.notes = notes != null ? notes.trim() : "";
        this.status = BookingStatus.ACTIVE;
    }

    /**
     * 获取预约时间段
     */
    public TimeSlot getTimeSlot() {
        return TimeSlot.of(startTime, endTime);
    }

    /**
     * 取消预约
     */
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException("Cannot cancel booking with status: " + status);
        }
        this.status = BookingStatus.CANCELLED;
    }

    /**
     * 签到
     */
    public void checkIn() {
        if (!status.canCheckIn()) {
            throw new IllegalStateException("Cannot check in booking with status: " + status);
        }
        this.status = BookingStatus.CHECKED_IN;
        this.checkedInAt = LocalDateTime.now();
    }

    /**
     * 检查是否可以在指定时间签到
     */
    public boolean canCheckInAt(LocalDateTime checkInTime, int allowedMinutesBefore, int allowedMinutesAfter) {
        LocalDateTime earliestCheckIn = startTime.minusMinutes(allowedMinutesBefore);
        LocalDateTime latestCheckIn = startTime.plusMinutes(allowedMinutesAfter);
        return !checkInTime.isBefore(earliestCheckIn) && !checkInTime.isAfter(latestCheckIn);
    }

    /**
     * 检查预约是否属于指定用户
     */
    public boolean belongsTo(UserId userId) {
        return this.userId.equals(userId);
    }

    // Getters
    public BookingId getId() {
        return id;
    }

    public MeetingRoomId getMeetingRoomId() {
        return meetingRoomId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getAttendeeCount() {
        return attendeeCount;
    }

    public String getNotes() {
        return notes;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", meetingRoomId=" + meetingRoomId +
                ", userId=" + userId +
                ", subject='" + subject + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", attendeeCount=" + attendeeCount +
                ", status=" + status +
                '}';
    }
}