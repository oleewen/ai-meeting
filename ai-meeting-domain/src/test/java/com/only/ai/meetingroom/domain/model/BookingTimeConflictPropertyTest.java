package com.only.ai.meetingroom.domain.model;

import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.service.BookingDomainService;
import net.jqwik.api.*;
import net.jqwik.time.api.DateTimes;

import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 时间冲突检测属性测试
 * **Feature: meeting-room-booking, Property 7: 时间冲突检测**
 * 
 * @author ai-meeting
 * @since 2024-01-15
 */
class BookingTimeConflictPropertyTest {

    /**
     * 属性 7: 时间冲突检测
     * 对于任何与现有预约时间重叠的新预约请求，系统应该拒绝预约并返回冲突信息
     * **验证: 需求 2.2**
     */
    @Property(tries = 100)
    void timeConflictDetectionForOverlappingBookings(
            @ForAll("overlappingTimeSlots") TimeSlot[] timeSlots) {
        
        TimeSlot timeSlot1 = timeSlots[0];
        TimeSlot timeSlot2 = timeSlots[1];
        
        // Given: 准备有冲突预约的环境
        BookingRepository mockRepository = mock(BookingRepository.class);
        BookingDomainService domainService = new BookingDomainService(mockRepository);
        
        // 生成测试数据
        MeetingRoomId meetingRoomId = MeetingRoomId.generate();
        BookingId existingBookingId = BookingId.generate();
        UserId existingUserId = UserId.generate();
        
        // 创建现有的预约（模拟已存在的预约）
        Booking existingBooking = new Booking(
                existingBookingId,
                meetingRoomId,
                existingUserId,
                "现有会议",
                timeSlot1.getStartTime(),
                timeSlot1.getEndTime(),
                5,
                "现有预约备注"
        );
        
        // 模拟仓储返回冲突的预约
        when(mockRepository.findConflictingBookings(eq(meetingRoomId), any(TimeSlot.class)))
                .thenReturn(Arrays.asList(existingBooking));
        
        // When: 检查时间冲突
        boolean hasConflict = domainService.hasTimeConflict(meetingRoomId, timeSlot2);
        
        // Then: 验证冲突检测结果
        
        // 1. 系统应该检测到时间冲突
        assertTrue(hasConflict, 
                "当新预约时间与现有预约重叠时，系统应该检测到时间冲突");
        
        // 2. 系统应该能够返回冲突的预约信息
        List<Booking> conflictingBookings = domainService.getConflictingBookings(meetingRoomId, timeSlot2);
        
        assertNotNull(conflictingBookings, "冲突检测应该返回冲突预约列表");
        assertFalse(conflictingBookings.isEmpty(), "当存在冲突时，冲突预约列表不应该为空");
        assertTrue(conflictingBookings.contains(existingBooking), 
                "冲突预约列表应该包含与新预约时间重叠的现有预约");
        
        // 3. 验证冲突信息的准确性
        for (Booking conflictBooking : conflictingBookings) {
            TimeSlot conflictTimeSlot = conflictBooking.getTimeSlot();
            assertTrue(timeSlot2.overlaps(conflictTimeSlot), 
                    "返回的冲突预约时间段应该与新预约时间段重叠");
            assertEquals(meetingRoomId, conflictBooking.getMeetingRoomId(), 
                    "冲突预约应该属于同一个会议室");
        }
        
        // 4. 验证仓储方法被正确调用
        verify(mockRepository, atLeastOnce()).findConflictingBookings(meetingRoomId, timeSlot2);
    }

    /**
     * 生成有效的未来时间段（符合业务规则）
     */
    @Provide
    Arbitrary<TimeSlot> validFutureTimeSlot() {
        // 生成未来1-29天内的工作时间段
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureStart = now.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime futureEnd = now.plusDays(29).withHour(17).withMinute(30).withSecond(0).withNano(0);
        
        return DateTimes.dateTimes()
                .between(futureStart, futureEnd)
                .filter(startTime -> {
                    // 只选择工作日（周一到周五）
                    int dayOfWeek = startTime.getDayOfWeek().getValue();
                    return dayOfWeek >= 1 && dayOfWeek <= 5;
                })
                .filter(startTime -> {
                    // 只选择工作时间（8:00-17:30）
                    int hour = startTime.getHour();
                    return hour >= 8 && hour < 17;
                })
                .flatMap(startTime -> {
                    // 生成合理的结束时间（30分钟到4小时）
                    LocalDateTime minEndTime = startTime.plusMinutes(30);
                    LocalDateTime maxEndTime = startTime.plusHours(4);
                    
                    // 确保不超过当天18:00
                    LocalDateTime dayEnd = startTime.withHour(18).withMinute(0).withSecond(0).withNano(0);
                    if (maxEndTime.isAfter(dayEnd)) {
                        maxEndTime = dayEnd;
                    }
                    
                    return DateTimes.dateTimes()
                            .between(minEndTime, maxEndTime)
                            .map(endTime -> TimeSlot.of(startTime, endTime));
                });
    }

    /**
     * 生成两个重叠的时间段
     */
    @Provide
    Arbitrary<TimeSlot[]> overlappingTimeSlots() {
        return validFutureTimeSlot().flatMap(baseTimeSlot -> {
            // 基于第一个时间段生成与其重叠的第二个时间段
            LocalDateTime baseStart = baseTimeSlot.getStartTime();
            LocalDateTime baseEnd = baseTimeSlot.getEndTime();
            
            // 生成重叠的开始时间（在基础时间段内或稍早）
            LocalDateTime overlapStart = baseStart.minusMinutes(60); // 最多提前1小时
            LocalDateTime overlapEnd = baseEnd.plusMinutes(60);     // 最多延后1小时
            
            return DateTimes.dateTimes()
                    .between(overlapStart, baseEnd.minusMinutes(15)) // 确保开始时间在基础结束时间之前
                    .flatMap(newStart -> {
                        // 生成重叠的结束时间（确保与基础时间段重叠）
                        LocalDateTime minNewEnd = baseStart.plusMinutes(15); // 确保与基础开始时间重叠
                        LocalDateTime maxNewEnd = overlapEnd;
                        
                        return DateTimes.dateTimes()
                                .between(minNewEnd, maxNewEnd)
                                .filter(newEnd -> newEnd.isAfter(newStart.plusMinutes(30))) // 确保最少30分钟
                                .map(newEnd -> new TimeSlot[] {
                                        baseTimeSlot,
                                        TimeSlot.of(newStart, newEnd)
                                });
                    });
        });
    }
}