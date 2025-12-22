package com.only.ai.meetingroom.domain.repository;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 预约记录仓储接口
 *
 * @author only
 * @since 2024-01-01
 */
public interface BookingRepository {
    /**
     * 根据ID查找预约
     */
    Optional<Booking> findById(Booking.BookingId id);

    /**
     * 根据用户ID查找预约列表
     */
    List<Booking> findByUserId(com.only.ai.meetingroom.domain.model.User.UserId userId);

    /**
     * 根据用户ID和日期范围查找预约列表
     */
    List<Booking> findByUserIdAndDateRange(com.only.ai.meetingroom.domain.model.User.UserId userId,
                                            LocalDate startDate, LocalDate endDate);

    /**
     * 根据会议室ID和日期查找预约列表
     */
    List<Booking> findByRoomIdAndDate(Room.RoomId roomId, LocalDate date);

    /**
     * 检查时间段是否有冲突的预约
     *
     * @param roomId 会议室ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeBookingId 排除的预约ID（用于更新时排除自身）
     * @return 是否存在冲突
     */
    boolean hasConflict(Room.RoomId roomId, LocalDate date, LocalTime startTime, LocalTime endTime,
                        Booking.BookingId excludeBookingId);

    /**
     * 保存预约
     * @return 保存后的预约（包含生成的ID）
     */
    Booking save(Booking booking);

    /**
     * 删除预约
     */
    void delete(Booking booking);
}

