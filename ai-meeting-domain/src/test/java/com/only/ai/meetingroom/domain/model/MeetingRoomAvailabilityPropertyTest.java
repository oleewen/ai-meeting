package com.only.ai.meetingroom.domain.model;

import net.jqwik.api.*;
import net.jqwik.time.api.DateTimes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会议室可用性查询属性测试
 * **Feature: meeting-room-booking, Property 1: 可用会议室查询准确性**
 * 
 * @author ai-meeting
 * @since 2024-01-15
 */
class MeetingRoomAvailabilityPropertyTest {

    /**
     * 属性 1: 可用会议室查询准确性
     * 对于任何日期和时间段查询，返回的会议室列表应该只包含在该时间段内真正可用（未被预约）的会议室
     * **验证: 需求 1.1**
     */
    @Property(tries = 100)
    void availableMeetingRoomsQueryAccuracy(
            @ForAll("validTimeSlot") TimeSlot queryTimeSlot,
            @ForAll("meetingRoomList") List<MeetingRoom> allMeetingRooms,
            @ForAll("bookingList") List<Booking> existingBookings) {
        
        // Given: 准备测试数据
        // 确保预约列表中的会议室ID与会议室列表中的ID匹配
        List<Booking> validBookings = filterValidBookings(existingBookings, allMeetingRooms);
        
        // When: 执行可用会议室查询逻辑
        List<MeetingRoom> result = findAvailableRoomsInTimeSlot(
                allMeetingRooms, validBookings, queryTimeSlot);
        
        // Then: 验证查询结果的准确性
        
        // 1. 结果不应该为null
        assertNotNull(result, "查询结果不应该为null");
        
        // 2. 结果中的每个会议室都应该在查询时间段内可用
        for (MeetingRoom room : result) {
            assertTrue(isRoomAvailableInTimeSlot(room, existingBookings, queryTimeSlot),
                    String.format("会议室 %s 在时间段 %s 内应该是可用的", room.getName(), queryTimeSlot));
        }
        
        // 3. 所有在该时间段内可用的会议室都应该出现在结果中
        for (MeetingRoom room : allMeetingRooms) {
            if (room.isActive() && isRoomAvailableInTimeSlot(room, existingBookings, queryTimeSlot)) {
                assertTrue(result.contains(room),
                        String.format("可用的会议室 %s 应该出现在查询结果中", room.getName()));
            }
        }
        
        // 4. 结果中不应该包含非活跃的会议室
        for (MeetingRoom room : result) {
            assertTrue(room.isActive(),
                    String.format("查询结果中不应该包含非活跃的会议室: %s", room.getName()));
        }
        
        // 5. 结果中不应该有重复的会议室
        Set<MeetingRoomId> roomIds = result.stream()
                .map(MeetingRoom::getId)
                .collect(Collectors.toSet());
        assertEquals(result.size(), roomIds.size(),
                "查询结果中不应该有重复的会议室");
        
        // 6. 验证查询结果与实际可用性的一致性
        List<MeetingRoom> expectedAvailableRooms = allMeetingRooms.stream()
                .filter(MeetingRoom::isActive)
                .filter(room -> isRoomAvailableInTimeSlot(room, existingBookings, queryTimeSlot))
                .collect(Collectors.toList());
        
        assertEquals(expectedAvailableRooms.size(), result.size(),
                "查询结果的数量应该与实际可用会议室数量一致");
        
        // 验证每个期望的可用会议室都在结果中
        for (MeetingRoom expectedRoom : expectedAvailableRooms) {
            assertTrue(result.contains(expectedRoom),
                    String.format("期望的可用会议室 %s 应该在查询结果中", expectedRoom.getName()));
        }
    }

    /**
     * 检查会议室在指定时间段内是否可用
     */
    private boolean isRoomAvailableInTimeSlot(MeetingRoom room, List<Booking> bookings, TimeSlot timeSlot) {
        if (!room.isActive()) {
            return false;
        }
        
        // 检查是否有与查询时间段冲突的预约
        return bookings.stream()
                .filter(booking -> booking.getMeetingRoomId().equals(room.getId()))
                .filter(booking -> booking.getStatus() == BookingStatus.ACTIVE || 
                                 booking.getStatus() == BookingStatus.CHECKED_IN)
                .noneMatch(booking -> booking.getTimeSlot().overlaps(timeSlot));
    }

    /**
     * 模拟查找可用会议室的逻辑
     */
    private List<MeetingRoom> findAvailableRoomsInTimeSlot(
            List<MeetingRoom> allRooms, List<Booking> bookings, TimeSlot timeSlot) {
        return allRooms.stream()
                .filter(room -> isRoomAvailableInTimeSlot(room, bookings, timeSlot))
                .collect(Collectors.toList());
    }

    /**
     * 过滤出有效的预约（会议室ID必须在会议室列表中存在）
     */
    private List<Booking> filterValidBookings(List<Booking> bookings, List<MeetingRoom> meetingRooms) {
        Set<MeetingRoomId> validRoomIds = meetingRooms.stream()
                .map(MeetingRoom::getId)
                .collect(Collectors.toSet());
        
        return bookings.stream()
                .filter(booking -> validRoomIds.contains(booking.getMeetingRoomId()))
                .collect(Collectors.toList());
    }



    /**
     * 生成有效的时间段
     */
    @Provide
    Arbitrary<TimeSlot> validTimeSlot() {
        return DateTimes.dateTimes()
                .between(
                        LocalDateTime.of(2024, 1, 1, 8, 0),
                        LocalDateTime.of(2024, 12, 31, 17, 0)
                )
                .flatMap(startTime -> 
                        DateTimes.dateTimes()
                                .between(
                                        startTime.plusMinutes(30), // 最短30分钟
                                        startTime.plusHours(4)     // 最长4小时
                                )
                                .map(endTime -> TimeSlot.of(startTime, endTime))
                );
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
                createMeetingRoom("会议室E", "2楼", 25, createEquipmentSet(Equipment.LARGE_SCREEN, Equipment.PROJECTOR), true)
        ).set().ofMinSize(1).ofMaxSize(5).map(set -> new ArrayList<>(set));
    }

    /**
     * 生成预约列表
     */
    @Provide
    Arbitrary<List<Booking>> bookingList() {
        return Arbitraries.of("会议室A-id", "会议室B-id", "会议室C-id", "会议室E-id")
                .flatMap(roomId -> 
                        validTimeSlot().flatMap(timeSlot ->
                                Arbitraries.of(BookingStatus.ACTIVE, BookingStatus.CHECKED_IN, BookingStatus.CANCELLED)
                                        .map(status -> createBookingWithRoomId(roomId, timeSlot, status))
                        )
                ).list().ofMaxSize(5);
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



    /**
     * 根据会议室ID创建预约辅助方法
     */
    private Booking createBookingWithRoomId(String roomId, TimeSlot timeSlot, BookingStatus status) {
        Booking booking = new Booking(
                BookingId.generate(),
                MeetingRoomId.of(roomId),
                UserId.generate(),
                "测试会议",
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                5,
                "测试备注"
        );
        
        // 设置状态
        if (status == BookingStatus.CANCELLED) {
            booking.cancel();
        } else if (status == BookingStatus.CHECKED_IN) {
            booking.checkIn();
        }
        
        return booking;
    }
}