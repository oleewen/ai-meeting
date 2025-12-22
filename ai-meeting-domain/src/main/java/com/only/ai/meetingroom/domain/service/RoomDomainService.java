package com.only.ai.meetingroom.domain.service;

import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 会议室领域服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class RoomDomainService {
    private final RoomRepository roomRepository;

    public RoomDomainService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * 获取会议室详情
     */
    public Optional<Room> getRoom(Room.RoomId roomId) {
        return roomRepository.findById(roomId);
    }

    /**
     * 获取所有可用会议室
     */
    public List<Room> getAllAvailableRooms() {
        return roomRepository.findAllAvailable();
    }
}

