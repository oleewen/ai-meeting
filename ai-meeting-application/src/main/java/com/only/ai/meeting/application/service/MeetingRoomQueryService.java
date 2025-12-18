package com.only.ai.meeting.application.service;

import com.only.ai.meeting.application.query.RoomQuery;
import com.only.ai.meeting.application.result.RoomQueryResult;
import com.only.ai.meeting.domain.model.MeetingRoom;
import com.only.ai.meeting.domain.repository.MeetingRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议室查询应用服务
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Service
public class MeetingRoomQueryService {

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    /**
     * 查询可用会议室
     */
    public RoomQueryResult queryAvailableRooms(RoomQuery query) {
        // 查询指定时间段内可用的会议室
        List<MeetingRoom> rooms = meetingRoomRepository.findAvailableRooms(
                query.getDate(),
                query.getStartTime(),
                query.getEndTime()
        );

        // 应用筛选条件
        if (query.getLocation() != null && !query.getLocation().trim().isEmpty()) {
            rooms = rooms.stream()
                    .filter(room -> room.getLocation().equals(query.getLocation()))
                    .collect(Collectors.toList());
        }

        if (query.getMinCapacity() != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getCapacity() >= query.getMinCapacity())
                    .collect(Collectors.toList());
        }

        if (query.getEquipment() != null && !query.getEquipment().isEmpty()) {
            rooms = rooms.stream()
                    .filter(room -> {
                        if (room.getEquipment() == null) {
                            return false;
                        }
                        return room.getEquipment().containsAll(query.getEquipment());
                    })
                    .collect(Collectors.toList());
        }

        return new RoomQueryResult(rooms);
    }

    /**
     * 根据ID查找会议室
     */
    public MeetingRoom findById(Long id) {
        return meetingRoomRepository.findById(id);
    }
}
