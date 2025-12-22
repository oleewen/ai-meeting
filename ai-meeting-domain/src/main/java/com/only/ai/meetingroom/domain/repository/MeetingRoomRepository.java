package com.only.ai.meetingroom.domain.repository;

import com.only.ai.meetingroom.domain.model.Equipment;
import com.only.ai.meetingroom.domain.model.MeetingRoom;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 会议室仓储接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public interface MeetingRoomRepository {
    
    /**
     * 根据ID查找会议室
     */
    Optional<MeetingRoom> findById(MeetingRoomId id);
    
    /**
     * 查找所有活跃的会议室
     */
    List<MeetingRoom> findAllActive();
    
    /**
     * 查找在指定时间段内可用的会议室
     */
    List<MeetingRoom> findAvailableInTimeSlot(TimeSlot timeSlot);
    
    /**
     * 根据筛选条件查找会议室
     */
    List<MeetingRoom> findByFilters(String location, Integer minCapacity, Set<Equipment> requiredEquipments);
    
    /**
     * 查找在指定时间段内可用且符合筛选条件的会议室
     */
    List<MeetingRoom> findAvailableByFilters(TimeSlot timeSlot, String location, Integer minCapacity, Set<Equipment> requiredEquipments);
    
    /**
     * 保存会议室
     */
    void save(MeetingRoom meetingRoom);
    
    /**
     * 删除会议室
     */
    void delete(MeetingRoomId id);
}