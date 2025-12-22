package com.only.ai.meetingroom.infrastructure.repository;

import com.only.ai.meetingroom.domain.model.Equipment;
import com.only.ai.meetingroom.domain.model.MeetingRoom;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;
import com.only.ai.meetingroom.domain.repository.MeetingRoomRepository;
import com.only.ai.meetingroom.infrastructure.mapper.MeetingRoomMapper;
import com.only.ai.meetingroom.infrastructure.po.MeetingRoomPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议室仓储实现
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Repository
public class MeetingRoomRepositoryImpl implements MeetingRoomRepository {

    @Resource
    private MeetingRoomMapper meetingRoomMapper;

    @Override
    public Optional<MeetingRoom> findById(MeetingRoomId id) {
        MeetingRoomPO po = meetingRoomMapper.selectById(id.value());
        return po != null ? Optional.of(po.toDomain()) : Optional.empty();
    }

    @Override
    public List<MeetingRoom> findAllActive() {
        List<MeetingRoomPO> pos = meetingRoomMapper.selectAllActive();
        return pos.stream().map(MeetingRoomPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MeetingRoom> findAvailableInTimeSlot(TimeSlot timeSlot) {
        List<MeetingRoomPO> pos = meetingRoomMapper.selectAvailableInTimeSlot(
                timeSlot.getStartTime(), timeSlot.getEndTime());
        return pos.stream().map(MeetingRoomPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MeetingRoom> findByFilters(String location, Integer minCapacity, Set<Equipment> requiredEquipments) {
        List<String> equipmentNames = requiredEquipments != null ? 
                requiredEquipments.stream().map(Enum::name).collect(Collectors.toList()) : null;
        
        List<MeetingRoomPO> pos = meetingRoomMapper.selectByFilters(location, minCapacity, equipmentNames);
        return pos.stream().map(MeetingRoomPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MeetingRoom> findAvailableByFilters(TimeSlot timeSlot, String location, Integer minCapacity, Set<Equipment> requiredEquipments) {
        List<String> equipmentNames = requiredEquipments != null ? 
                requiredEquipments.stream().map(Enum::name).collect(Collectors.toList()) : null;
        
        List<MeetingRoomPO> pos = meetingRoomMapper.selectAvailableByFilters(
                timeSlot.getStartTime(), timeSlot.getEndTime(), location, minCapacity, equipmentNames);
        return pos.stream().map(MeetingRoomPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public void save(MeetingRoom meetingRoom) {
        MeetingRoomPO po = MeetingRoomPO.fromDomain(meetingRoom);
        if (meetingRoomMapper.selectById(po.getId()) != null) {
            meetingRoomMapper.update(po);
        } else {
            meetingRoomMapper.insert(po);
        }
    }

    @Override
    public void delete(MeetingRoomId id) {
        meetingRoomMapper.deleteById(id.value());
    }
}