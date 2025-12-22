package com.only.ai.meetingroom.domain.repository;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.BookingId;
import com.only.ai.meetingroom.domain.model.BookingStatus;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;
import com.only.ai.meetingroom.domain.model.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 预约仓储接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public interface BookingRepository {
    
    /**
     * 根据ID查找预约
     */
    Optional<Booking> findById(BookingId id);
    
    /**
     * 查找指定会议室在指定时间段内的冲突预约
     */
    List<Booking> findConflictingBookings(MeetingRoomId meetingRoomId, TimeSlot timeSlot);
    
    /**
     * 查找指定会议室在指定日期的所有有效预约
     */
    List<Booking> findActiveBookingsByRoomAndDate(MeetingRoomId meetingRoomId, LocalDate date);
    
    /**
     * 查找指定用户的所有预约
     */
    List<Booking> findByUserId(UserId userId);
    
    /**
     * 查找指定用户在指定日期范围内的预约
     */
    List<Booking> findByUserIdAndDateRange(UserId userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 查找指定用户指定状态的预约
     */
    List<Booking> findByUserIdAndStatus(UserId userId, BookingStatus status);
    
    /**
     * 查找指定用户在指定日期范围内指定状态的预约
     */
    List<Booking> findByUserIdAndDateRangeAndStatus(UserId userId, LocalDate startDate, LocalDate endDate, BookingStatus status);
    
    /**
     * 保存预约
     */
    void save(Booking booking);
    
    /**
     * 删除预约
     */
    void delete(BookingId id);
}