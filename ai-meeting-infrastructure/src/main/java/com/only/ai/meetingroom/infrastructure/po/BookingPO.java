package com.only.ai.meetingroom.infrastructure.po;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.BookingId;
import com.only.ai.meetingroom.domain.model.BookingStatus;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.UserId;

import java.time.LocalDateTime;

/**
 * 预约持久化对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class BookingPO {
    private String id;
    private String meetingRoomId;
    private String userId;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer attendeeCount;
    private String notes;
    private String status;
    private LocalDateTime checkedInAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingPO() {}

    public static BookingPO fromDomain(Booking booking) {
        BookingPO po = new BookingPO();
        po.id = booking.getId().value();
        po.meetingRoomId = booking.getMeetingRoomId().value();
        po.userId = booking.getUserId().value();
        po.subject = booking.getSubject();
        po.startTime = booking.getStartTime();
        po.endTime = booking.getEndTime();
        po.attendeeCount = booking.getAttendeeCount();
        po.notes = booking.getNotes();
        po.status = booking.getStatus().name();
        po.checkedInAt = booking.getCheckedInAt();
        return po;
    }

    public Booking toDomain() {
        Booking booking = new Booking(
                BookingId.of(this.id),
                MeetingRoomId.of(this.meetingRoomId),
                UserId.of(this.userId),
                this.subject,
                this.startTime,
                this.endTime,
                this.attendeeCount,
                this.notes
        );
        
        // 设置状态和签到时间（通过反射或其他方式，这里简化处理）
        if (BookingStatus.CANCELLED.name().equals(this.status)) {
            booking.cancel();
        } else if (BookingStatus.CHECKED_IN.name().equals(this.status)) {
            booking.checkIn();
        }
        
        return booking;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(Integer attendeeCount) {
        this.attendeeCount = attendeeCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}