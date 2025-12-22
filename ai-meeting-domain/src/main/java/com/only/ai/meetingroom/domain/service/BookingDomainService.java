package com.only.ai.meetingroom.domain.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约领域服务
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Service
public class BookingDomainService {
    
    @Resource
    private BookingRepository bookingRepository;
    
    // 业务规则常量
    private static final int MAX_ADVANCE_DAYS = 30;
    private static final int MIN_DURATION_MINUTES = 30;
    private static final int CHECK_IN_WINDOW_BEFORE_MINUTES = 15;
    private static final int CHECK_IN_WINDOW_AFTER_MINUTES = 30;

    // 构造函数用于测试
    public BookingDomainService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // 无参构造函数用于Spring依赖注入
    public BookingDomainService() {
    }

    /**
     * 检查时间冲突
     */
    public boolean hasTimeConflict(MeetingRoomId meetingRoomId, TimeSlot timeSlot) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(meetingRoomId, timeSlot);
        return !conflictingBookings.isEmpty();
    }

    /**
     * 获取冲突的预约列表
     */
    public List<Booking> getConflictingBookings(MeetingRoomId meetingRoomId, TimeSlot timeSlot) {
        return bookingRepository.findConflictingBookings(meetingRoomId, timeSlot);
    }

    /**
     * 验证预约业务规则
     */
    public void validateBookingRules(TimeSlot timeSlot) {
        validateAdvanceBookingLimit(timeSlot.getStartTime());
        validateMinimumDuration(timeSlot);
        validateWorkingHours(timeSlot);
    }

    /**
     * 验证提前预约时间限制
     */
    private void validateAdvanceBookingLimit(LocalDateTime startTime) {
        LocalDateTime maxAdvanceTime = LocalDateTime.now().plusDays(MAX_ADVANCE_DAYS);
        if (startTime.isAfter(maxAdvanceTime)) {
            throw new IllegalArgumentException("Cannot book more than " + MAX_ADVANCE_DAYS + " days in advance");
        }
        
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book in the past");
        }
    }

    /**
     * 验证最短时长限制
     */
    private void validateMinimumDuration(TimeSlot timeSlot) {
        Duration duration = timeSlot.getDuration();
        if (duration.toMinutes() < MIN_DURATION_MINUTES) {
            throw new IllegalArgumentException("Booking duration must be at least " + MIN_DURATION_MINUTES + " minutes");
        }
    }

    /**
     * 验证工作时间
     */
    private void validateWorkingHours(TimeSlot timeSlot) {
        LocalDateTime startTime = timeSlot.getStartTime();
        LocalDateTime endTime = timeSlot.getEndTime();
        
        // 工作时间：周一到周五 8:00-18:00
        int startHour = startTime.getHour();
        int endHour = endTime.getHour();
        int dayOfWeek = startTime.getDayOfWeek().getValue();
        
        if (dayOfWeek > 5) { // 周六周日
            throw new IllegalArgumentException("Cannot book on weekends");
        }
        
        if (startHour < 8 || endHour > 18 || (endHour == 18 && endTime.getMinute() > 0)) {
            throw new IllegalArgumentException("Booking must be within working hours (8:00-18:00)");
        }
    }

    /**
     * 检查是否可以在指定时间签到
     */
    public boolean canCheckInAt(Booking booking, LocalDateTime checkInTime) {
        return booking.canCheckInAt(checkInTime, CHECK_IN_WINDOW_BEFORE_MINUTES, CHECK_IN_WINDOW_AFTER_MINUTES);
    }

    /**
     * 检查是否可以取消预约（基于时间限制）
     */
    public boolean canCancelBooking(Booking booking, LocalDateTime currentTime) {
        // 至少提前2小时取消
        LocalDateTime cancelDeadline = booking.getStartTime().minusHours(2);
        return currentTime.isBefore(cancelDeadline) && booking.getStatus().canBeCancelled();
    }

    /**
     * 获取签到时间窗口描述
     */
    public String getCheckInWindowDescription() {
        return "可在会议开始前" + CHECK_IN_WINDOW_BEFORE_MINUTES + "分钟至开始后" + CHECK_IN_WINDOW_AFTER_MINUTES + "分钟内签到";
    }
}