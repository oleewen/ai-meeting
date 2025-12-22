package com.only.ai.meetingroom.domain.model;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会议室筛选功能属性测试
 * **Feature: meeting-room-booking, Property 2: 筛选条件一致性**
 * 
 * @author ai-meeting
 * @since 2024-01-15
 */
class MeetingRoomFilterPropertyTest {

    /**
     * 属性 2: 筛选条件一致性
     * 对于任何筛选条件组合（地点、容量、设备），返回的会议室应该满足所有指定的筛选条件
     * **验证: 需求 1.2**
     */
    @Property(tries = 100)
    void filterConditionsConsistency(
            @ForAll("meetingRoomList") List<MeetingRoom> allMeetingRooms,
            @ForAll("filterLocation") String location,
            @ForAll("filterCapacity") Integer minCapacity,
            @ForAll("filterEquipments") Set<Equipment> requiredEquipments) {
        
        // Given: 准备测试数据和筛选条件
        
        // When: 执行筛选逻辑
        List<MeetingRoom> result = filterMeetingRooms(
                allMeetingRooms, location, minCapacity, requiredEquipments);
        
        // Then: 验证筛选结果的一致性
        
        // 1. 结果不应该为null
        assertNotNull(result, "筛选结果不应该为null");
        
        // 2. 结果中的每个会议室都应该满足所有筛选条件
        for (MeetingRoom room : result) {
            // 验证地点筛选条件
            if (location != null && !location.trim().isEmpty()) {
                assertEquals(location, room.getLocation(),
                        String.format("会议室 %s 的地点应该匹配筛选条件: %s", room.getName(), location));
            }
            
            // 验证容量筛选条件
            if (minCapacity != null && minCapacity > 0) {
                assertTrue(room.getCapacity() >= minCapacity,
                        String.format("会议室 %s 的容量 %d 应该大于等于最小容量要求 %d", 
                                room.getName(), room.getCapacity(), minCapacity));
            }
            
            // 验证设备筛选条件
            if (requiredEquipments != null && !requiredEquipments.isEmpty()) {
                for (Equipment equipment : requiredEquipments) {
                    assertTrue(room.hasEquipment(equipment),
                            String.format("会议室 %s 应该包含所需设备: %s", room.getName(), equipment.getDescription()));
                }
            }
            
            // 验证只返回活跃的会议室
            assertTrue(room.isActive(),
                    String.format("筛选结果中不应该包含非活跃的会议室: %s", room.getName()));
        }
        
        // 3. 所有满足筛选条件的会议室都应该出现在结果中
        for (MeetingRoom room : allMeetingRooms) {
            if (room.isActive() && meetsCriteria(room, location, minCapacity, requiredEquipments)) {
                assertTrue(result.contains(room),
                        String.format("满足筛选条件的会议室 %s 应该出现在结果中", room.getName()));
            }
        }
        
        // 4. 结果中不应该有重复的会议室
        Set<MeetingRoomId> roomIds = result.stream()
                .map(MeetingRoom::getId)
                .collect(Collectors.toSet());
        assertEquals(result.size(), roomIds.size(),
                "筛选结果中不应该有重复的会议室");
        
        // 5. 验证筛选结果与期望结果的一致性
        List<MeetingRoom> expectedResult = allMeetingRooms.stream()
                .filter(MeetingRoom::isActive)
                .filter(room -> meetsCriteria(room, location, minCapacity, requiredEquipments))
                .collect(Collectors.toList());
        
        assertEquals(expectedResult.size(), result.size(),
                "筛选结果的数量应该与期望数量一致");
        
        // 验证每个期望的会议室都在结果中
        for (MeetingRoom expectedRoom : expectedResult) {
            assertTrue(result.contains(expectedRoom),
                    String.format("期望的会议室 %s 应该在筛选结果中", expectedRoom.getName()));
        }
        
        // 6. 验证空筛选条件的情况
        if (isEmptyFilter(location, minCapacity, requiredEquipments)) {
            // 如果没有筛选条件，应该返回所有活跃的会议室
            List<MeetingRoom> allActiveRooms = allMeetingRooms.stream()
                    .filter(MeetingRoom::isActive)
                    .collect(Collectors.toList());
            assertEquals(allActiveRooms.size(), result.size(),
                    "没有筛选条件时应该返回所有活跃的会议室");
        }
    }

    /**
     * 检查会议室是否满足筛选条件
     */
    private boolean meetsCriteria(MeetingRoom room, String location, Integer minCapacity, Set<Equipment> requiredEquipments) {
        // 检查地点条件
        if (location != null && !location.trim().isEmpty()) {
            if (!location.equals(room.getLocation())) {
                return false;
            }
        }
        
        // 检查容量条件
        if (minCapacity != null && minCapacity > 0) {
            if (room.getCapacity() < minCapacity) {
                return false;
            }
        }
        
        // 检查设备条件
        if (requiredEquipments != null && !requiredEquipments.isEmpty()) {
            for (Equipment equipment : requiredEquipments) {
                if (!room.hasEquipment(equipment)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 检查是否为空筛选条件
     */
    private boolean isEmptyFilter(String location, Integer minCapacity, Set<Equipment> requiredEquipments) {
        return (location == null || location.trim().isEmpty()) &&
               (minCapacity == null || minCapacity <= 0) &&
               (requiredEquipments == null || requiredEquipments.isEmpty());
    }

    /**
     * 模拟会议室筛选逻辑
     */
    private List<MeetingRoom> filterMeetingRooms(List<MeetingRoom> allRooms, String location, 
                                               Integer minCapacity, Set<Equipment> requiredEquipments) {
        return allRooms.stream()
                .filter(MeetingRoom::isActive)
                .filter(room -> meetsCriteria(room, location, minCapacity, requiredEquipments))
                .collect(Collectors.toList());
    }

    /**
     * 生成会议室列表
     */
    @Provide
    Arbitrary<List<MeetingRoom>> meetingRoomList() {
        return Arbitraries.of(
                createMeetingRoom("会议室A", "1楼", 10, Collections.singleton(Equipment.PROJECTOR), true),
                createMeetingRoom("会议室B", "2楼", 20, Collections.singleton(Equipment.VIDEO_CONFERENCE), true),
                createMeetingRoom("会议室C", "3楼", 15, Collections.singleton(Equipment.WHITEBOARD), true),
                createMeetingRoom("会议室D", "1楼", 8, Collections.emptySet(), false), // 非活跃
                createMeetingRoom("会议室E", "2楼", 25, createEquipmentSet(Equipment.LARGE_SCREEN, Equipment.PROJECTOR), true),
                createMeetingRoom("会议室F", "1楼", 12, createEquipmentSet(Equipment.PROJECTOR, Equipment.WHITEBOARD), true),
                createMeetingRoom("会议室G", "3楼", 30, createEquipmentSet(Equipment.VIDEO_CONFERENCE, Equipment.LARGE_SCREEN), true),
                createMeetingRoom("会议室H", "2楼", 6, Collections.singleton(Equipment.WHITEBOARD), true)
        ).set().ofMinSize(3).ofMaxSize(8).map(set -> new ArrayList<>(set));
    }

    /**
     * 生成筛选地点条件
     */
    @Provide
    Arbitrary<String> filterLocation() {
        return Arbitraries.oneOf(
                Arbitraries.just(null),           // 无地点筛选
                Arbitraries.just(""),             // 空字符串
                Arbitraries.just("1楼"),          // 有效地点
                Arbitraries.just("2楼"),
                Arbitraries.just("3楼"),
                Arbitraries.just("不存在的地点")    // 不存在的地点
        );
    }

    /**
     * 生成筛选容量条件
     */
    @Provide
    Arbitrary<Integer> filterCapacity() {
        return Arbitraries.oneOf(
                Arbitraries.just(null),           // 无容量筛选
                Arbitraries.just(0),              // 无效容量
                Arbitraries.just(-1),             // 负数容量
                Arbitraries.integers().between(5, 35)  // 有效容量范围
        );
    }

    /**
     * 生成筛选设备条件
     */
    @Provide
    Arbitrary<Set<Equipment>> filterEquipments() {
        return Arbitraries.oneOf(
                Arbitraries.just(null),                                    // 无设备筛选
                Arbitraries.just(Collections.emptySet()),                  // 空设备集合
                Arbitraries.just(Collections.singleton(Equipment.PROJECTOR)),
                Arbitraries.just(Collections.singleton(Equipment.VIDEO_CONFERENCE)),
                Arbitraries.just(Collections.singleton(Equipment.WHITEBOARD)),
                Arbitraries.just(Collections.singleton(Equipment.LARGE_SCREEN)),
                Arbitraries.just(createEquipmentSet(Equipment.PROJECTOR, Equipment.WHITEBOARD)),
                Arbitraries.just(createEquipmentSet(Equipment.VIDEO_CONFERENCE, Equipment.LARGE_SCREEN)),
                Arbitraries.just(createEquipmentSet(Equipment.PROJECTOR, Equipment.VIDEO_CONFERENCE, Equipment.WHITEBOARD))
        );
    }

    /**
     * 创建设备集合辅助方法
     */
    private Set<Equipment> createEquipmentSet(Equipment... equipments) {
        Set<Equipment> equipmentSet = new HashSet<>();
        for (Equipment equipment : equipments) {
            equipmentSet.add(equipment);
        }
        return equipmentSet;
    }

    /**
     * 创建会议室辅助方法
     */
    private MeetingRoom createMeetingRoom(String name, String location, int capacity, 
                                        Set<Equipment> equipments, boolean active) {
        MeetingRoom room = new MeetingRoom(
                MeetingRoomId.of(name + "-id"),
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
}