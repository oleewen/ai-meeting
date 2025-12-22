package com.only.ai.meetingroom.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户预约列表响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class UserBookingListResponse {
    
    private List<BookingInfo> bookings;
    private int totalCount;

    public UserBookingListResponse() {
    }

    public UserBookingListResponse(List<BookingInfo> bookings, int totalCount) {
        this.bookings = bookings;
        this.totalCount = totalCount;
    }

    // Getters and Setters
    public List<BookingInfo> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingInfo> bookings) {
        this.bookings = bookings;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 预约信息内部类
     */
    public static class BookingInfo {
        private String bookingId;
        private String subject;
        private String roomName;
        private String roomLocation;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private LocalDateTime checkedInAt;

        public BookingInfo() {
        }

        public BookingInfo(String bookingId, String subject, String roomName, String roomLocation,
                         LocalDateTime startTime, LocalDateTime endTime, String status, LocalDateTime checkedInAt) {
            this.bookingId = bookingId;
            this.subject = subject;
            this.roomName = roomName;
            this.roomLocation = roomLocation;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.checkedInAt = checkedInAt;
        }

        // Getters and Setters
        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomLocation() {
            return roomLocation;
        }

        public void setRoomLocation(String roomLocation) {
            this.roomLocation = roomLocation;
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

        @Override
        public String toString() {
            return "BookingInfo{" +
                    "bookingId='" + bookingId + '\'' +
                    ", subject='" + subject + '\'' +
                    ", roomName='" + roomName + '\'' +
                    ", roomLocation='" + roomLocation + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", status='" + status + '\'' +
                    ", checkedInAt=" + checkedInAt +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserBookingListResponse{" +
                "bookings=" + bookings +
                ", totalCount=" + totalCount +
                '}';
    }
}