package com.only.ai.meetingroom.infrastructure.mapper;

import com.only.ai.meetingroom.infrastructure.po.BookingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约记录Mapper
 *
 * @author only
 * @since 2024-01-01
 */
@Mapper
public interface BookingMapper {
    /**
     * 根据ID查询
     */
    BookingPO selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询
     */
    List<BookingPO> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和日期范围查询
     */
    List<BookingPO> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 根据会议室ID和日期查询
     */
    List<BookingPO> selectByRoomIdAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date);

    /**
     * 检查时间段是否有冲突
     */
    int countConflicts(@Param("roomId") Long roomId,
                       @Param("date") LocalDate date,
                       @Param("startTime") LocalTime startTime,
                       @Param("endTime") LocalTime endTime,
                       @Param("excludeBookingId") Long excludeBookingId);

    /**
     * 插入
     */
    int insert(BookingPO booking);

    /**
     * 更新
     */
    int update(BookingPO booking);

    /**
     * 删除
     */
    int delete(@Param("id") Long id);
}

