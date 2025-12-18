package com.only.ai.meeting.infrastructure.dao;

import com.only.ai.meeting.domain.model.MeetingRoom;
import com.only.ai.meeting.domain.repository.MeetingRoomRepository;
import com.only.ai.meeting.infrastructure.entity.MeetingRoomEntity;
import com.only.ai.meeting.infrastructure.factory.EntityFactory;
import com.only.ai.meeting.infrastructure.mapper.MeetingRoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室资源库实现类
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Repository
public class MeetingRoomRepositoryImpl implements MeetingRoomRepository {

    @Autowired
    private MeetingRoomMapper meetingRoomMapper;

    @Override
    public MeetingRoom findById(Long id) {
        MeetingRoomEntity entity = meetingRoomMapper.findById(id);
        return EntityFactory.toDomainModel(entity);
    }

    @Override
    public List<MeetingRoom> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<MeetingRoomEntity> entities = meetingRoomMapper.findAvailableRooms(date, startTime, endTime);
        return EntityFactory.toDomainModelList(entities);
    }

    @Override
    public List<MeetingRoom> findByLocation(String location) {
        List<MeetingRoomEntity> entities = meetingRoomMapper.findByLocation(location);
        return EntityFactory.toDomainModelList(entities);
    }

    @Override
    public void save(MeetingRoom room) {
        MeetingRoomEntity entity = EntityFactory.toEntity(room);
        if (entity.getId() == null) {
            meetingRoomMapper.insert(entity);
            room.setId(entity.getId());
        } else {
            meetingRoomMapper.update(entity);
        }
    }
}
