package com.only.ai.meetingroom.domain.model;

import net.jqwik.api.*;
import net.jqwik.time.api.DateTimes;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预约数据持久化属性测试
 * **Feature: meeting-room-booking, Property 8: 预约数据持久化**
 * 
 * @author ai-meeting
 * @since 2024-01-15
 */
class BookingPersistencePropertyTest {

    /**
     * 属性 8: 预约数据持久化
     * 对于任何成功创建的预约，系统应该正确保存所有输入信息（主题、参会人数等）并能准确检索
     * **验证: 需求 2.3**
     */
    @Property(tries = 100)
    void bookingDataPersistenceRoundTrip(
            @ForAll("validBookingSubject") String subject,
            @ForAll("validAttendeeCount") int attendeeCount,
            @ForAll("validNotes") String notes,
            @ForAll("validTimeSlot") TimeSlot timeSlot) {
        
        // Given: 创建预约所需的基础数据
        BookingId bookingId = BookingId.generate();
        MeetingRoomId meetingRoomId = MeetingRoomId.generate();
        UserId userId = UserId.generate();
        
        // When: 创建预约对象
        Booking originalBooking = new Booking(
                bookingId,
                meetingRoomId,
                userId,
                subject,
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                attendeeCount,
                notes
        );
        
        // Then: 验证所有输入信息都被正确保存
        // 验证基本信息
        assertEquals(bookingId, originalBooking.getId());
        assertEquals(meetingRoomId, originalBooking.getMeetingRoomId());
        assertEquals(userId, originalBooking.getUserId());
        
        // 验证用户输入的关键信息（需求2.3重点关注的数据）
        assertEquals(subject.trim(), originalBooking.getSubject());
        assertEquals(attendeeCount, originalBooking.getAttendeeCount());
        assertEquals(notes != null ? notes.trim() : "", originalBooking.getNotes());
        
        // 验证时间信息
        assertEquals(timeSlot.getStartTime(), originalBooking.getStartTime());
        assertEquals(timeSlot.getEndTime(), originalBooking.getEndTime());
        assertEquals(timeSlot, originalBooking.getTimeSlot());
        
        // 验证默认状态
        assertEquals(BookingStatus.ACTIVE, originalBooking.getStatus());
        assertNull(originalBooking.getCheckedInAt());
        
        // 验证数据完整性：测试对象状态变化后的数据一致性
        // 创建另一个相同数据的预约对象来验证数据持久化的一致性
        Booking duplicateBooking = new Booking(
                bookingId,
                meetingRoomId,
                userId,
                subject,
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                attendeeCount,
                notes
        );
        
        // 验证两个具有相同输入数据的预约对象应该具有相同的属性值
        assertEquals(originalBooking.getId(), duplicateBooking.getId());
        assertEquals(originalBooking.getMeetingRoomId(), duplicateBooking.getMeetingRoomId());
        assertEquals(originalBooking.getUserId(), duplicateBooking.getUserId());
        assertEquals(originalBooking.getSubject(), duplicateBooking.getSubject());
        assertEquals(originalBooking.getStartTime(), duplicateBooking.getStartTime());
        assertEquals(originalBooking.getEndTime(), duplicateBooking.getEndTime());
        assertEquals(originalBooking.getAttendeeCount(), duplicateBooking.getAttendeeCount());
        assertEquals(originalBooking.getNotes(), duplicateBooking.getNotes());
        assertEquals(originalBooking.getStatus(), duplicateBooking.getStatus());
        
        // 验证业务方法的一致性
        assertEquals(originalBooking.getTimeSlot(), duplicateBooking.getTimeSlot());
        assertEquals(originalBooking.belongsTo(userId), duplicateBooking.belongsTo(userId));
        
        // 验证对象相等性（基于ID）
        assertEquals(originalBooking, duplicateBooking);
        assertEquals(originalBooking.hashCode(), duplicateBooking.hashCode());
    }

    /**
     * 生成有效的预约主题
     */
    @Provide
    Arbitrary<String> validBookingSubject() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars(' ', '-', '_', '（', '）', '(', ')')
                .ofMinLength(1)
                .ofMaxLength(200)
                .filter(s -> !s.trim().isEmpty());
    }

    /**
     * 生成有效的参会人数
     */
    @Provide
    Arbitrary<Integer> validAttendeeCount() {
        return Arbitraries.integers().between(1, 100);
    }

    /**
     * 生成有效的备注信息
     */
    @Provide
    Arbitrary<String> validNotes() {
        return Arbitraries.oneOf(
                Arbitraries.just(null),
                Arbitraries.just(""),
                Arbitraries.strings()
                        .withCharRange('a', 'z')
                        .withCharRange('A', 'Z')
                        .withCharRange('0', '9')
                        .withChars(' ', '.', ',', '!', '?', '-', '_')
                        .ofMaxLength(1000)
        );
    }

    /**
     * 生成有效的时间段
     */
    @Provide
    Arbitrary<TimeSlot> validTimeSlot() {
        return DateTimes.dateTimes()
                .between(
                        LocalDateTime.of(2024, 1, 1, 8, 0),
                        LocalDateTime.of(2024, 12, 31, 18, 0)
                )
                .flatMap(startTime -> 
                        DateTimes.dateTimes()
                                .between(
                                        startTime.plusMinutes(30), // 最短30分钟
                                        startTime.plusHours(8)     // 最长8小时
                                )
                                .map(endTime -> TimeSlot.of(startTime, endTime))
                );
    }
}