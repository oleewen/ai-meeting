package com.only.ai.meetingroom.infrastructure.repository;

import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.repository.RoomRepository;
import com.only.ai.meetingroom.infrastructure.factory.RoomFactory;
import com.only.ai.meetingroom.infrastructure.mapper.RoomMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会议室仓储实现
 *
 * @author only
 * @since 2024-01-01
 */
@Repository
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomMapper roomMapper;

    public RoomRepositoryImpl(RoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    @Override
    public Optional<Room> findById(Room.RoomId id) {
        return Optional.ofNullable(RoomFactory.toDomain(roomMapper.selectById(id.value())));
    }

    @Override
    public List<Room> findAllAvailable() {
        return roomMapper.selectAllAvailable().stream()
                .map(RoomFactory::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime,
                                          String location, Integer minCapacity, List<String> equipment) {
        return roomMapper.selectAvailableRooms(date, startTime, endTime, location, minCapacity, equipment)
                .stream()
                .map(RoomFactory::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Room room) {
        if (room.getId() == null || roomMapper.selectById(room.getId().value()) == null) {
            roomMapper.insert(RoomFactory.toPO(room));
        } else {
            roomMapper.update(RoomFactory.toPO(room));
        }
    }
}

