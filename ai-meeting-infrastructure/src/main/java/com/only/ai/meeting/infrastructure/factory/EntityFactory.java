package com.only.ai.meeting.infrastructure.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.BookingStatus;
import com.only.ai.meeting.domain.model.MeetingRoom;
import com.only.ai.meeting.domain.model.RoomStatus;
import com.only.ai.meeting.domain.model.User;
import com.only.ai.meeting.infrastructure.entity.BookingEntity;
import com.only.ai.meeting.infrastructure.entity.MeetingRoomEntity;
import com.only.ai.meeting.infrastructure.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体转换工厂类
 * 负责领域模型和数据实体之间的转换
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class EntityFactory {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * MeetingRoomEntity -> MeetingRoom
     */
    public static MeetingRoom toDomainModel(MeetingRoomEntity entity) {
        if (entity == null) {
            return null;
        }
        MeetingRoom room = new MeetingRoom();
        room.setId(entity.getId());
        room.setName(entity.getName());
        room.setLocation(entity.getLocation());
        room.setCapacity(entity.getCapacity());
        room.setStatus(RoomStatus.valueOf(entity.getStatus()));
        room.setDescription(entity.getDescription());
        room.setCreateTime(entity.getCreateTime());
        room.setUpdateTime(entity.getUpdateTime());
        
        // 解析equipment JSON
        if (entity.getEquipment() != null && !entity.getEquipment().isEmpty()) {
            try {
                List<String> equipment = objectMapper.readValue(
                    entity.getEquipment(), 
                    new TypeReference<List<String>>() {}
                );
                room.setEquipment(equipment);
            } catch (Exception e) {
                room.setEquipment(new ArrayList<>());
            }
        }
        
        return room;
    }

    /**
     * MeetingRoom -> MeetingRoomEntity
     */
    public static MeetingRoomEntity toEntity(MeetingRoom domain) {
        if (domain == null) {
            return null;
        }
        MeetingRoomEntity entity = new MeetingRoomEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setLocation(domain.getLocation());
        entity.setCapacity(domain.getCapacity());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().name() : RoomStatus.AVAILABLE.name());
        entity.setDescription(domain.getDescription());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        
        // 序列化equipment为JSON
        if (domain.getEquipment() != null && !domain.getEquipment().isEmpty()) {
            try {
                entity.setEquipment(objectMapper.writeValueAsString(domain.getEquipment()));
            } catch (Exception e) {
                entity.setEquipment("[]");
            }
        }
        
        return entity;
    }

    /**
     * BookingEntity -> Booking
     */
    public static Booking toDomainModel(BookingEntity entity) {
        if (entity == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setId(entity.getId());
        booking.setUserId(entity.getUserId());
        booking.setUserName(entity.getUserName());
        booking.setRoomId(entity.getRoomId());
        booking.setRoomName(entity.getRoomName());
        booking.setDate(entity.getDate());
        booking.setStartTime(entity.getStartTime());
        booking.setEndTime(entity.getEndTime());
        booking.setSubject(entity.getSubject());
        booking.setAttendeeCount(entity.getAttendeeCount());
        booking.setRemark(entity.getRemark());
        booking.setStatus(BookingStatus.valueOf(entity.getStatus()));
        booking.setSignInTime(entity.getSignInTime());
        booking.setCreateTime(entity.getCreateTime());
        booking.setUpdateTime(entity.getUpdateTime());
        return booking;
    }

    /**
     * Booking -> BookingEntity
     */
    public static BookingEntity toEntity(Booking domain) {
        if (domain == null) {
            return null;
        }
        BookingEntity entity = new BookingEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setUserName(domain.getUserName());
        entity.setRoomId(domain.getRoomId());
        entity.setRoomName(domain.getRoomName());
        entity.setDate(domain.getDate());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setSubject(domain.getSubject());
        entity.setAttendeeCount(domain.getAttendeeCount());
        entity.setRemark(domain.getRemark());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().name() : BookingStatus.PENDING.name());
        entity.setSignInTime(domain.getSignInTime());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    /**
     * UserEntity -> User
     */
    public static User toDomainModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setDepartment(entity.getDepartment());
        return user;
    }

    /**
     * User -> UserEntity
     */
    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setDepartment(domain.getDepartment());
        return entity;
    }

    /**
     * List转换
     */
    public static List<MeetingRoom> toDomainModelList(List<MeetingRoomEntity> entities) {
        List<MeetingRoom> result = new ArrayList<>();
        if (entities != null) {
            for (MeetingRoomEntity entity : entities) {
                result.add(toDomainModel(entity));
            }
        }
        return result;
    }

    public static List<Booking> toBookingDomainModelList(List<BookingEntity> entities) {
        List<Booking> result = new ArrayList<>();
        if (entities != null) {
            for (BookingEntity entity : entities) {
                result.add(toDomainModel(entity));
            }
        }
        return result;
    }
}
