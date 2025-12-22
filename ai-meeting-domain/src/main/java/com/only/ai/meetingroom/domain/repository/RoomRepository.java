package com.only.ai.meetingroom.domain.repository;

import com.only.ai.meetingroom.domain.model.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 会议室仓储接口
 *
 * @author only
 * @since 2024-01-01
 */
public interface RoomRepository {
    /**
     * 根据ID查找会议室
     */
    Optional<Room> findById(Room.RoomId id);

    /**
     * 查找所有可用会议室
     */
    List<Room> findAllAvailable();

    /**
     * 根据条件查询可用会议室
     *
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param location 地点筛选（可选）
     * @param minCapacity 最小容量（可选）
     * @param equipment 设备筛选（可选）
     * @return 符合条件的可用会议室列表
     */
    List<Room> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime,
                                   String location, Integer minCapacity, List<String> equipment);

    /**
     * 保存会议室
     */
    void save(Room room);
}

