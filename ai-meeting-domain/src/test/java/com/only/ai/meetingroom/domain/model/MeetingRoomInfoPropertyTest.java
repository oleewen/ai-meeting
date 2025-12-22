package com.only.ai.meetingroom.domain.model;

import net.jqwik.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会议室信息完整性属性测试
 * **Feature: meeting-room-booking, Property 3: 会议室信息完整性**
 * 
 * @author ai-meeting
 * @since 2024-01-15
 */
class MeetingRoomInfoPropertyTest {

    /**
     * 属性 3: 会议室信息完整性
     * 对于任何会议室显示请求，返回的信息应该包含名称、地点、容量和设备等所有必需字段
     * **验证: 需求 1.3**
     */
    @Property(tries = 100)
    void meetingRoomInfoCompleteness(@ForAll("validMeetingRoom") MeetingRoom meetingRoom) {
        
        // Given: 一个有效的会议室对象
        
        // When: 获取会议室信息（模拟显示请求）
        MeetingRoomInfo info = extractMeetingRoomInfo(meetingRoom);
        
        // Then: 验证信息完整性
        
        // 1. 信息对象不应该为null
        assertNotNull(info, "会议室信息对象不应该为null");
        
        // 2. 验证名称信息完整性
        assertNotNull(info.getName(), "会议室名称不应该为null");
        assertFalse(info.getName().trim().isEmpty(), "会议室名称不应该为空");
        assertEquals(meetingRoom.getName(), info.getName(), 
                "返回的名称应该与原始会议室名称一致");
        
        // 3. 验证地点信息完整性
        assertNotNull(info.getLocation(), "会议室地点不应该为null");
        assertFalse(info.getLocation().trim().isEmpty(), "会议室地点不应该为空");
        assertEquals(meetingRoom.getLocation(), info.getLocation(),
                "返回的地点应该与原始会议室地点一致");
        
        // 4. 验证容量信息完整性
        assertNotNull(info.getCapacity(), "会议室容量不应该为null");
        assertTrue(info.getCapacity() > 0, "会议室容量应该大于0");
        assertEquals(meetingRoom.getCapacity(), info.getCapacity().intValue(),
                "返回的容量应该与原始会议室容量一致");
        
        // 5. 验证设备信息完整性
        assertNotNull(info.getEquipments(), "会议室设备信息不应该为null");
        
        // 验证设备信息的准确性
        Set<Equipment> originalEquipments = meetingRoom.getEquipments();
        Set<Equipment> returnedEquipments = info.getEquipments();
        
        if (originalEquipments == null || originalEquipments.isEmpty()) {
            assertTrue(returnedEquipments.isEmpty(), 
                    "当原始设备为空时，返回的设备信息也应该为空");
        } else {
            assertEquals(originalEquipments.size(), returnedEquipments.size(),
                    "返回的设备数量应该与原始设备数量一致");
            
            for (Equipment equipment : originalEquipments) {
                assertTrue(returnedEquipments.contains(equipment),
                        String.format("返回的设备信息应该包含原始设备: %s", equipment.getDescription()));
            }
        }
        
        // 6. 验证设备描述信息的完整性
        for (Equipment equipment : returnedEquipments) {
            assertNotNull(equipment.getDescription(), 
                    String.format("设备 %s 的描述不应该为null", equipment.name()));
            assertFalse(equipment.getDescription().trim().isEmpty(),
                    String.format("设备 %s 的描述不应该为空", equipment.name()));
        }
        
        // 7. 验证ID信息的完整性（虽然不在需求1.3中明确要求，但对于系统完整性很重要）
        assertNotNull(info.getId(), "会议室ID不应该为null");
        assertEquals(meetingRoom.getId(), info.getId(),
                "返回的ID应该与原始会议室ID一致");
        
        // 8. 验证活跃状态信息的完整性
        assertNotNull(info.getActive(), "会议室活跃状态不应该为null");
        assertEquals(meetingRoom.isActive(), info.getActive().booleanValue(),
                "返回的活跃状态应该与原始会议室状态一致");
        
        // 9. 验证信息的一致性 - 所有字段都应该正确映射
        assertTrue(isInfoConsistent(meetingRoom, info),
                "会议室信息应该与原始数据保持一致");
        
        // 10. 验证信息的可读性 - 所有必需字段都应该有有意义的值
        assertTrue(isInfoReadable(info),
                "会议室信息应该具有可读性，所有必需字段都应该有有意义的值");
    }

    /**
     * 模拟从会议室对象提取显示信息的过程
     */
    private MeetingRoomInfo extractMeetingRoomInfo(MeetingRoom meetingRoom) {
        return new MeetingRoomInfo(
                meetingRoom.getId(),
                meetingRoom.getName(),
                meetingRoom.getLocation(),
                meetingRoom.getCapacity(),
                meetingRoom.getEquipments(),
                meetingRoom.isActive()
        );
    }

    /**
     * 验证信息的一致性
     */
    private boolean isInfoConsistent(MeetingRoom original, MeetingRoomInfo info) {
        return Objects.equals(original.getId(), info.getId()) &&
               Objects.equals(original.getName(), info.getName()) &&
               Objects.equals(original.getLocation(), info.getLocation()) &&
               original.getCapacity() == info.getCapacity() &&
               Objects.equals(original.getEquipments(), info.getEquipments()) &&
               original.isActive() == info.getActive();
    }

    /**
     * 验证信息的可读性
     */
    private boolean isInfoReadable(MeetingRoomInfo info) {
        // 检查名称的可读性
        if (info.getName() == null || info.getName().trim().isEmpty()) {
            return false;
        }
        
        // 检查地点的可读性
        if (info.getLocation() == null || info.getLocation().trim().isEmpty()) {
            return false;
        }
        
        // 检查容量的合理性
        if (info.getCapacity() == null || info.getCapacity() <= 0) {
            return false;
        }
        
        // 检查设备信息的可读性
        if (info.getEquipments() == null) {
            return false;
        }
        
        for (Equipment equipment : info.getEquipments()) {
            if (equipment == null || equipment.getDescription() == null || 
                equipment.getDescription().trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 生成有效的会议室对象
     */
    @Provide
    Arbitrary<MeetingRoom> validMeetingRoom() {
        return Arbitraries.of(
                createMeetingRoom("会议室A", "1楼东侧", 10, Collections.singleton(Equipment.PROJECTOR), true),
                createMeetingRoom("会议室B", "2楼西侧", 20, Collections.singleton(Equipment.VIDEO_CONFERENCE), true),
                createMeetingRoom("大会议室C", "3楼中央", 50, createEquipmentSet(Equipment.PROJECTOR, Equipment.LARGE_SCREEN), true),
                createMeetingRoom("小会议室D", "1楼南侧", 6, Collections.singleton(Equipment.WHITEBOARD), true),
                createMeetingRoom("多功能厅E", "4楼", 100, createEquipmentSet(Equipment.VIDEO_CONFERENCE, Equipment.LARGE_SCREEN, Equipment.PROJECTOR), true),
                createMeetingRoom("培训室F", "2楼北侧", 30, createEquipmentSet(Equipment.WHITEBOARD, Equipment.PROJECTOR), true),
                createMeetingRoom("讨论室G", "3楼东侧", 8, Collections.emptySet(), true), // 无设备的会议室
                createMeetingRoom("VIP会议室H", "5楼", 15, createEquipmentSet(Equipment.VIDEO_CONFERENCE, Equipment.WHITEBOARD, Equipment.LARGE_SCREEN, Equipment.PROJECTOR), true),
                createMeetingRoom("临时会议室I", "地下1楼", 4, Collections.singleton(Equipment.WHITEBOARD), false) // 非活跃状态
        );
    }

    /**
     * 创建设备集合辅助方法
     */
    private Set<Equipment> createEquipmentSet(Equipment... equipments) {
        Set<Equipment> equipmentSet = new HashSet<>();
        Collections.addAll(equipmentSet, equipments);
        return equipmentSet;
    }

    /**
     * 创建会议室辅助方法
     */
    private MeetingRoom createMeetingRoom(String name, String location, int capacity, 
                                        Set<Equipment> equipments, boolean active) {
        MeetingRoom room = new MeetingRoom(
                MeetingRoomId.of(name + "-" + UUID.randomUUID().toString().substring(0, 8)),
                name,
                location,
                capacity,
                equipments
        );
        if (!active) {
            room.deactivate();
        }
        return room;
    }

    /**
     * 会议室信息DTO类 - 模拟显示层使用的数据传输对象
     */
    private static class MeetingRoomInfo {
        private final MeetingRoomId id;
        private final String name;
        private final String location;
        private final Integer capacity;
        private final Set<Equipment> equipments;
        private final Boolean active;

        public MeetingRoomInfo(MeetingRoomId id, String name, String location, 
                             int capacity, Set<Equipment> equipments, boolean active) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.capacity = capacity;
            this.equipments = equipments != null ? new HashSet<>(equipments) : new HashSet<>();
            this.active = active;
        }

        public MeetingRoomId getId() { return id; }
        public String getName() { return name; }
        public String getLocation() { return location; }
        public Integer getCapacity() { return capacity; }
        public Set<Equipment> getEquipments() { return equipments; }
        public Boolean getActive() { return active; }
    }
}