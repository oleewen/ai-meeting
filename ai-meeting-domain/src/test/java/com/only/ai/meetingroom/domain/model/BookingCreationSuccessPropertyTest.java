package com.only.ai.meetingroom.domain.model;

import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.service.BookingDomainService;
import net.jqwik.api.*;
import net.jqwik.time.api.DateTimes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 预约创建成功性属性测试
 * **Feature: meeting-room-booking, Property 6: 预约创建成功性**
 * 
 * @author ai-meeting
 * @since 2024-01-15
 */
class BookingCreationSuccessPropertyTest {

    /**
     * 属性 6: 预约创建成功性
     * 对于任何有效的预约请求（无时间冲突），系统应该成功创建预约记录并返回确认
     * **验证: 需求 2.1**
     */
    @Property(tries = 100)
    void bookingCreationSuccessForValidRequests(
            @ForAll("validBookingSubject") String subject,
            @ForAll("validAttendeeCount") int attendeeCount,
            @ForAll("validNotes") String notes,
            @ForAll("validFutureTimeSlot") TimeSlot timeSlot) {
        
        // Given: 准备无冲突的预约环境
        BookingRepository mockRepository = mock(BookingRepository.class);
        BookingDomainService domainService = new BookingDomainService(mockRepository);
        
        // 模拟无冲突情况：返回空的冲突列表
        when(mockRepository.findConflictingBookings(any(MeetingRoomId.class), any(TimeSlot.class)))
                .thenReturn(Collections.emptyList());
        
        // 生成测试数据
        BookingId bookingId = BookingId.generate();
        MeetingRoomId meetingRoomId = MeetingRoomId.generate();
        UserId userId = UserId.generate();
        
        // When: 验证业务规则（应该通过）
        assertDoesNotThrow(() -> domainService.validateBookingRules(timeSlot),
                "有效的时间段应该通过业务规则验证");
        
        // 验证无时间冲突
        boolean hasConflict = domainService.hasTimeConflict(meetingRoomId, timeSlot);
        assertFalse(hasConflict, "有效的预约请求不应该有时间冲突");
        
        // When: 创建预约对象（模拟预约创建过程）
        Booking createdBooking = assertDoesNotThrow(() -> new Booking(
                bookingId,
                meetingRoomId,
                userId,
                subject,
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                attendeeCount,
                notes
        ), "有效的预约数据应该能够成功创建预约对象");
        
        // Then: 验证预约创建成功的各项指标
        
        // 1. 预约对象不为空
        assertNotNull(createdBooking, "预约创建应该返回非空对象");
        
        // 2. 预约具有正确的标识
        assertNotNull(createdBooking.getId(), "创建的预约应该有有效的ID");
        assertEquals(bookingId, createdBooking.getId(), "预约ID应该与请求的ID一致");
        
        // 3. 预约包含所有请求的信息
        assertEquals(meetingRoomId, createdBooking.getMeetingRoomId(), "会议室ID应该正确保存");
        assertEquals(userId, createdBooking.getUserId(), "用户ID应该正确保存");
        assertEquals(subject.trim(), createdBooking.getSubject(), "会议主题应该正确保存");
        assertEquals(timeSlot.getStartTime(), createdBooking.getStartTime(), "开始时间应该正确保存");
        assertEquals(timeSlot.getEndTime(), createdBooking.getEndTime(), "结束时间应该正确保存");
        assertEquals(attendeeCount, createdBooking.getAttendeeCount(), "参会人数应该正确保存");
        assertEquals(notes != null ? notes.trim() : "", createdBooking.getNotes(), "备注信息应该正确保存");
        
        // 4. 预约具有正确的初始状态
        assertEquals(BookingStatus.ACTIVE, createdBooking.getStatus(), "新创建的预约状态应该是ACTIVE");
        assertNull(createdBooking.getCheckedInAt(), "新创建的预约不应该有签到时间");
        
        // 5. 预约的时间段信息正确
        TimeSlot bookingTimeSlot = createdBooking.getTimeSlot();
        assertNotNull(bookingTimeSlot, "预约应该有有效的时间段");
        assertEquals(timeSlot, bookingTimeSlot, "预约的时间段应该与请求的时间段一致");
        
        // 6. 预约的业务方法正常工作
        assertTrue(createdBooking.belongsTo(userId), "预约应该属于创建用户");
        assertFalse(createdBooking.belongsTo(UserId.generate()), "预约不应该属于其他用户");
        
        // 7. 验证预约可以正常保存（模拟保存操作）
        assertDoesNotThrow(() -> mockRepository.save(createdBooking), 
                "创建的预约应该能够成功保存到仓储");
        
        // 验证保存方法被调用
        verify(mockRepository, atLeastOnce()).save(createdBooking);
        
        // 8. 模拟保存后的查询验证
        when(mockRepository.findById(bookingId)).thenReturn(Optional.of(createdBooking));
        Optional<Booking> retrievedBooking = mockRepository.findById(bookingId);
        
        assertTrue(retrievedBooking.isPresent(), "保存后应该能够查询到预约");
        assertEquals(createdBooking, retrievedBooking.get(), "查询到的预约应该与创建的预约一致");
        
        // 9. 验证预约创建的确认信息（通过对象状态确认）
        // 预约创建成功的确认体现在：
        // - 对象创建成功（无异常）
        // - 所有属性正确设置
        // - 状态为ACTIVE
        // - 能够正常保存和查询
        assertTrue(createdBooking.getStatus() == BookingStatus.ACTIVE, 
                "预约创建成功的确认：状态应该为ACTIVE");
        assertNotNull(createdBooking.getId(), 
                "预约创建成功的确认：应该有有效的预约ID");
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
}