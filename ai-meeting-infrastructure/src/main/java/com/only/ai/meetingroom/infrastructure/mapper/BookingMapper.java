package com.only.ai.meetingroom.infrastructure.mapper;

import com.only.ai.meetingroom.infrastructure.po.BookingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约Mapper接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Mapper
public interface BookingMapper {
    
    /**
     * 根据ID查询预约
     */
    BookingPO selectById(@Param("id") String id);
    
    /**
     * 查询冲突的预约
     */
    List<BookingPO> selectConflictingBookings(@Param("meetingRoomId") String meetingRoomId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询指定会议室在指定时间范围内的活跃预约
     */
    List<BookingPO> selectActiveBookingsByRoomAndDateRange(@Param("meetingRoomId") String meetingRoomId,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据用户ID查询预约
     */
    List<BookingPO> selectByUserId(@Param("userId") String userId);
    
    /**
     * 根据用户ID和时间范围查询预约
     */
    List<BookingPO> selectByUserIdAndDateRange(@Param("userId") String userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据用户ID和状态查询预约
     */
    List<BookingPO> selectByUserIdAndStatus(@Param("userId") String userId,
                                            @Param("status") String status);
    
    /**
     * 根据用户ID、时间范围和状态查询预约
     */
    List<BookingPO> selectByUserIdAndDateRangeAndStatus(@Param("userId") String userId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime,
                                                        @Param("status") String status);
    
    /**
     * 插入预约
     */
    int insert(BookingPO booking);
    
    /**
     * 更新预约
     */
    int update(BookingPO booking);
    
    /**
     * 删除预约
     */
    int deleteById(@Param("id") String id);
}