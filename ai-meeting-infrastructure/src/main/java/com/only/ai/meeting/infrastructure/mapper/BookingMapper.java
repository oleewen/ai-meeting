package com.only.ai.meeting.infrastructure.mapper;

import com.only.ai.meeting.infrastructure.entity.BookingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约Mapper接口
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Mapper
public interface BookingMapper {
    /**
     * 根据ID查找预约
     */
    BookingEntity findById(@Param("id") Long id);

    /**
     * 根据用户ID查找预约
     */
    List<BookingEntity> findByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和日期范围查找预约
     */
    List<BookingEntity> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 根据会议室ID和日期查找预约
     */
    List<BookingEntity> findByRoomIdAndDate(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date
    );

    /**
     * 检查是否存在时间冲突
     */
    int countConflicts(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeBookingId") Long excludeBookingId
    );

    /**
     * 保存预约
     */
    void insert(BookingEntity entity);

    /**
     * 更新预约状态
     */
    void updateStatus(
            @Param("id") Long id,
            @Param("status") String status
    );

    /**
     * 查找已结束的预约（用于定时任务）
     */
    List<BookingEntity> findCompletedBookings(@Param("beforeTime") LocalDateTime beforeTime);
}
