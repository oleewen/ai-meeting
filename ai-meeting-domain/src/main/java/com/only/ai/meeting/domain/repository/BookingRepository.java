package com.only.ai.meeting.domain.repository;

import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约资源库接口
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public interface BookingRepository {
    /**
     * 根据ID查找预约
     */
    Booking findById(Long id);

    /**
     * 根据用户ID查找预约
     */
    List<Booking> findByUserId(String userId);

    /**
     * 根据用户ID和日期范围查找预约
     */
    List<Booking> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据会议室ID和日期查找预约
     */
    List<Booking> findByRoomIdAndDate(Long roomId, LocalDate date);

    /**
     * 检查是否存在时间冲突
     */
    boolean existsConflict(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeBookingId);

    /**
     * 保存预约
     */
    void save(Booking booking);

    /**
     * 更新预约状态
     */
    void updateStatus(Long id, BookingStatus status);

    /**
     * 查找已结束的预约（用于定时任务）
     */
    List<Booking> findCompletedBookings(LocalDateTime beforeTime);
}
