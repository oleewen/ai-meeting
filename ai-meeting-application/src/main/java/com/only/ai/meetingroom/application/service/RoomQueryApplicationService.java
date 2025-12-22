package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室查询应用服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class RoomQueryApplicationService {
    private final RoomRepository roomRepository;

    public RoomQueryApplicationService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * 查询可用会议室
     */
    public List<Room> queryAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime,
                                          String location, Integer minCapacity, List<String> equipment) {
        return roomRepository.findAvailableRooms(date, startTime, endTime, location, minCapacity, equipment);
    }

    /**
     * 获取会议室详情
     */
    public Room getRoomDetail(Room.RoomId roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("会议室不存在"));
    }
}

