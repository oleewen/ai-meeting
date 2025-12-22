package com.only.ai.meetingroom.domain.model;

import net.jqwik.api.*;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 会议室排序功能属性测试
 * **Feature: meeting-room-booking, Property 5: 排序功能正确性**
 */
class MeetingRoomSortPropertyTest {

    public enum SortField {
        CAPACITY, LOCATION
    }

    public enum SortOrder {
        ASC, DESC
    }

    public static class SortCriteria {
        private final SortField field;
        private final SortOrder order;

        public SortCriteria(SortField field, SortOrder order) {
            this.field = field;
            this.order = order;
        }

        public SortField getField() { return field; }
        public SortOrder getOrder() { return order; }
    }

    /**
     * 属性 5: 排序功能正确性
     * **验证: 需求 1.5**
     */
    @Property(tries = 100)
    void sortFunctionCorrectness(
            @ForAll("meetingRoomList") List<MeetingRoom> originalList,
            @ForAll("sortCriteria") SortCriteria criteria) {
        
        List<MeetingRoom> sortedResult = sortMeetingRooms(originalList, criteria);
        
        assertNotNull(sortedResult, "排序结果不应该为null");
        assertEquals(originalList.size(), sortedResult.size(), "排序结果的数量应该与原列表一致");
        
        for (MeetingRoom originalRoom : originalList) {
            assertTrue(sortedResult.contains(originalRoom),
                    String.format("原始会议室 %s 应该在排序结果中", originalRoom.getName()));
        }
        
        Set<MeetingRoomId> roomIds = sortedResult.stream()
                .map(MeetingRoom::getId)
                .collect(Collectors.toSet());
        assertEquals(sortedResult.size(), roomIds.size(), "排序结果中不应该有重复的会议室");
        
        if (sortedResult.size() > 1) {
            switch (criteria.getField()) {
                case CAPACITY:
                    verifySortByCapacity(sortedResult, criteria.getOrder());
                    break;
                case LOCATION:
                    verifySortByLocation(sortedResult, criteria.getOrder());
                    break;
            }
        }
        
        if (originalList.isEmpty()) {
            assertTrue(sortedResult.isEmpty(), "空列表排序后应该仍为空");
        }
        
        if (originalList.size() == 1) {
            assertEquals(1, sortedResult.size(), "单元素列表排序后应该仍为单元素");
            assertEquals(originalList.get(0), sortedResult.get(0), "单元素列表排序后元素应该保持不变");
        }
    }

    private void verifySortByCapacity(List<MeetingRoom> sortedList, SortOrder order) {
        for (int i = 0; i < sortedList.size() - 1; i++) {
            MeetingRoom current = sortedList.get(i);
            MeetingRoom next = sortedList.get(i + 1);
            
            if (order == SortOrder.ASC) {
                assertTrue(current.getCapacity() <= next.getCapacity(),
                        String.format("按容量升序排序时，%s(容量:%d) 应该在 %s(容量:%d) 之前或相等",
                                current.getName(), current.getCapacity(),
                                next.getName(), next.getCapacity()));
            } else {
                assertTrue(current.getCapacity() >= next.getCapacity(),
                        String.format("按容量降序排序时，%s(容量:%d) 应该在 %s(容量:%d) 之前或相等",
                                current.getName(), current.getCapacity(),
                                next.getName(), next.getCapacity()));
            }
        }
    }

    private void verifySortByLocation(List<MeetingRoom> sortedList, SortOrder order) {
        for (int i = 0; i < sortedList.size() - 1; i++) {
            MeetingRoom current = sortedList.get(i);
            MeetingRoom next = sortedList.get(i + 1);
            
            int comparison = current.getLocation().compareTo(next.getLocation());
            
            if (order == SortOrder.ASC) {
                assertTrue(comparison <= 0,
                        String.format("按位置升序排序时，%s(位置:%s) 应该在 %s(位置:%s) 之前或相等",
                                current.getName(), current.getLocation(),
                                next.getName(), next.getLocation()));
            } else {
                assertTrue(comparison >= 0,
                        String.format("按位置降序排序时，%s(位置:%s) 应该在 %s(位置:%s) 之前或相等",
                                current.getName(), current.getLocation(),
                                next.getName(), next.getLocation()));
            }
        }
    }

    private List<MeetingRoom> sortMeetingRooms(List<MeetingRoom> rooms, SortCriteria criteria) {
        List<MeetingRoom> result = new ArrayList<>(rooms);
        
        Comparator<MeetingRoom> comparator;
        
        switch (criteria.getField()) {
            case CAPACITY:
                comparator = Comparator.comparingInt(MeetingRoom::getCapacity);
                break;
            case LOCATION:
                comparator = Comparator.comparing(MeetingRoom::getLocation);
                break;
            default:
                throw new IllegalArgumentException("不支持的排序字段: " + criteria.getField());
        }
        
        if (criteria.getOrder() == SortOrder.DESC) {
            comparator = comparator.reversed();
        }
        
        result.sort(comparator);
        return result;
    }

    @Provide
    Arbitrary<List<MeetingRoom>> meetingRoomList() {
        return Arbitraries.of(
                createMeetingRoom("会议室A", "1楼东侧", 10, Collections.singleton(Equipment.PROJECTOR)),
                createMeetingRoom("会议室B", "2楼西侧", 20, Collections.singleton(Equipment.VIDEO_CONFERENCE)),
                createMeetingRoom("会议室C", "1楼西侧", 15, Collections.singleton(Equipment.WHITEBOARD)),
                createMeetingRoom("会议室D", "3楼东侧", 8, Collections.emptySet()),
                createMeetingRoom("会议室E", "2楼东侧", 25, createEquipmentSet(Equipment.LARGE_SCREEN, Equipment.PROJECTOR)),
                createMeetingRoom("会议室F", "1楼中央", 12, createEquipmentSet(Equipment.PROJECTOR, Equipment.WHITEBOARD))
        ).set().ofMinSize(0).ofMaxSize(6).map(set -> new ArrayList<>(set));
    }

    @Provide
    Arbitrary<SortCriteria> sortCriteria() {
        return Arbitraries.of(
                new SortCriteria(SortField.CAPACITY, SortOrder.ASC),
                new SortCriteria(SortField.CAPACITY, SortOrder.DESC),
                new SortCriteria(SortField.LOCATION, SortOrder.ASC),
                new SortCriteria(SortField.LOCATION, SortOrder.DESC)
        );
    }

    private Set<Equipment> createEquipmentSet(Equipment... equipments) {
        Set<Equipment> equipmentSet = new HashSet<>();
        Collections.addAll(equipmentSet, equipments);
        return equipmentSet;
    }

    private MeetingRoom createMeetingRoom(String name, String location, int capacity, Set<Equipment> equipments) {
        return new MeetingRoom(
                MeetingRoomId.of(name + "-" + UUID.randomUUID().toString().substring(0, 8)),
                name,
                location,
                capacity,
                equipments
        );
    }
}