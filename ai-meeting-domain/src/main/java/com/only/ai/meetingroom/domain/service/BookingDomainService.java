package com.only.ai.meetingroom.domain.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

/**
 * 预约领域服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class BookingDomainService {
    private final BookingRepository bookingRepository;

    // 业务规则配置（可配置化）
    private static final int MAX_ADVANCE_DAYS = 30; // 最多提前30天预约
    private static final int MIN_DURATION_MINUTES = 30; // 最短30分钟
    private static final int MAX_DURATION_HOURS = 4; // 最长4小时
    private static final int MIN_ADVANCE_MINUTES = 5; // 至少提前5分钟预约

    public BookingDomainService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * 验证预约时间范围
     */
    public void validateBookingTimeRange(LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingStart = LocalDateTime.of(date, startTime);

        // 检查是否在未来
        if (bookingStart.isBefore(now)) {
            throw new IllegalArgumentException("预约时间不能早于当前时间");
        }

        // 检查是否在允许的时间范围内（最多提前30天）
        long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), date);
        if (daysBetween > MAX_ADVANCE_DAYS) {
            throw new IllegalArgumentException("只能预约未来" + MAX_ADVANCE_DAYS + "天内的会议室");
        }

        // 检查时长
        long durationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        if (durationMinutes < MIN_DURATION_MINUTES) {
            throw new IllegalArgumentException("会议时长不能少于" + MIN_DURATION_MINUTES + "分钟");
        }
        if (durationMinutes > MAX_DURATION_HOURS * 60) {
            throw new IllegalArgumentException("会议时长不能超过" + MAX_DURATION_HOURS + "小时");
        }

        // 检查是否至少提前5分钟
        long minutesUntilStart = ChronoUnit.MINUTES.between(now, bookingStart);
        if (minutesUntilStart < MIN_ADVANCE_MINUTES) {
            throw new IllegalArgumentException("会议开始前" + MIN_ADVANCE_MINUTES + "分钟不可预约");
        }
    }

    /**
     * 检查预约冲突
     */
    public void checkConflict(Room.RoomId roomId, LocalDate date, LocalTime startTime, LocalTime endTime,
                               Booking.BookingId excludeBookingId) {
        if (bookingRepository.hasConflict(roomId, date, startTime, endTime, excludeBookingId)) {
            throw new IllegalStateException("该时间段已被预约，请选择其他时间");
        }
    }

    /**
     * 验证取消时间限制
     */
    public void validateCancelTime(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingStart = LocalDateTime.of(booking.getDate(), booking.getStartTime());
        long minutesUntilStart = ChronoUnit.MINUTES.between(now, bookingStart);

        if (minutesUntilStart < MIN_ADVANCE_MINUTES) {
            throw new IllegalStateException("会议开始前" + MIN_ADVANCE_MINUTES + "分钟不可取消预约");
        }
    }

    /**
     * 验证签到时间窗口
     */
    public void validateCheckInTime(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingStart = LocalDateTime.of(booking.getDate(), booking.getStartTime());
        long minutesFromStart = ChronoUnit.MINUTES.between(bookingStart, now);

        // 允许提前10分钟到延后15分钟签到
        if (minutesFromStart < -10) {
            throw new IllegalStateException("签到时间未到，请在会议开始前10分钟至开始后15分钟内签到");
        }
        if (minutesFromStart > 15) {
            throw new IllegalStateException("签到时间已过");
        }
    }
}

