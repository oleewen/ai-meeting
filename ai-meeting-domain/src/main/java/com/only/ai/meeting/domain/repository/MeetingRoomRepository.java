package com.only.ai.meeting.domain.repository;

import com.only.ai.meeting.domain.model.MeetingRoom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室资源库接口
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public interface MeetingRoomRepository {
    /**
     * 根据ID查找会议室
     */
    MeetingRoom findById(Long id);

    /**
     * 查找指定时间段内可用的会议室
     */
    List<MeetingRoom> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * 根据地点查找会议室
     */
    List<MeetingRoom> findByLocation(String location);

    /**
     * 保存会议室
     */
    void save(MeetingRoom room);
}
