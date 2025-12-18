package com.only.ai.meeting.infrastructure.mapper;

import com.only.ai.meeting.infrastructure.entity.MeetingRoomEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室Mapper接口
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Mapper
public interface MeetingRoomMapper {
    /**
     * 根据ID查找会议室
     */
    MeetingRoomEntity findById(@Param("id") Long id);

    /**
     * 查找指定时间段内可用的会议室
     */
    List<MeetingRoomEntity> findAvailableRooms(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    /**
     * 根据地点查找会议室
     */
    List<MeetingRoomEntity> findByLocation(@Param("location") String location);

    /**
     * 保存会议室
     */
    void insert(MeetingRoomEntity entity);

    /**
     * 更新会议室
     */
    void update(MeetingRoomEntity entity);
}
