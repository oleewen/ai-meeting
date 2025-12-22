package com.only.ai.meetingroom.infrastructure.factory;

import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.infrastructure.po.RoomPO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

/**
 * 会议室工厂（PO和领域模型转换）
 *
 * @author only
 * @since 2024-01-01
 */
public class RoomFactory {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * PO转领域模型
     */
    public static Room toDomain(RoomPO po) {
        if (po == null) {
            return null;
        }
        List<String> equipment = Collections.emptyList();
        if (po.getEquipment() != null && !po.getEquipment().isEmpty()) {
            try {
                equipment = objectMapper.readValue(po.getEquipment(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                equipment = Collections.emptyList();
            }
        }
        Room.RoomStatus status = po.getStatus() == 0 ? Room.RoomStatus.AVAILABLE : Room.RoomStatus.UNAVAILABLE;
        return new Room(
                new Room.RoomId(po.getId()),
                po.getName(),
                po.getLocation(),
                po.getCapacity(),
                equipment,
                status
        );
    }

    /**
     * 领域模型转PO
     */
    public static RoomPO toPO(Room room) {
        if (room == null) {
            return null;
        }
        RoomPO po = new RoomPO();
        po.setId(room.getId().value());
        po.setName(room.getName());
        po.setLocation(room.getLocation());
        po.setCapacity(room.getCapacity());
        try {
            po.setEquipment(objectMapper.writeValueAsString(room.getEquipment()));
        } catch (Exception e) {
            po.setEquipment("[]");
        }
        po.setStatus(room.getStatus() == Room.RoomStatus.AVAILABLE ? 0 : 1);
        return po;
    }
}

