package com.only.ai.meetingroom.infrastructure.mapper;

import com.only.ai.meetingroom.infrastructure.po.MeetingRoomPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议室Mapper接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Mapper
public interface MeetingRoomMapper {
    
    /**
     * 根据ID查询会议室
     */
    MeetingRoomPO selectById(@Param("id") String id);
    
    /**
     * 查询所有活跃的会议室
     */
    List<MeetingRoomPO> selectAllActive();
    
    /**
     * 查询在指定时间段内可用的会议室
     */
    List<MeetingRoomPO> selectAvailableInTimeSlot(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据筛选条件查询会议室
     */
    List<MeetingRoomPO> selectByFilters(@Param("location") String location,
                                        @Param("minCapacity") Integer minCapacity,
                                        @Param("requiredEquipments") List<String> requiredEquipments);
    
    /**
     * 查询在指定时间段内可用且符合筛选条件的会议室
     */
    List<MeetingRoomPO> selectAvailableByFilters(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("location") String location,
                                                 @Param("minCapacity") Integer minCapacity,
                                                 @Param("requiredEquipments") List<String> requiredEquipments);
    
    /**
     * 插入会议室
     */
    int insert(MeetingRoomPO meetingRoom);
    
    /**
     * 更新会议室
     */
    int update(MeetingRoomPO meetingRoom);
    
    /**
     * 删除会议室
     */
    int deleteById(@Param("id") String id);
}