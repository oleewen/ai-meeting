package com.only.ai.meetingroom.infrastructure.mapper;

import com.only.ai.meetingroom.infrastructure.po.RoomPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室Mapper
 *
 * @author only
 * @since 2024-01-01
 */
@Mapper
public interface RoomMapper {
    /**
     * 根据ID查询
     */
    RoomPO selectById(@Param("id") Long id);

    /**
     * 查询所有可用会议室
     */
    List<RoomPO> selectAllAvailable();

    /**
     * 根据条件查询可用会议室
     */
    List<RoomPO> selectAvailableRooms(@Param("date") LocalDate date,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime,
                                      @Param("location") String location,
                                      @Param("minCapacity") Integer minCapacity,
                                      @Param("equipment") List<String> equipment);

    /**
     * 插入
     */
    int insert(RoomPO room);

    /**
     * 更新
     */
    int update(RoomPO room);
}

