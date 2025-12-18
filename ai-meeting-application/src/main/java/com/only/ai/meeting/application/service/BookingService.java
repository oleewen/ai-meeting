package com.only.ai.meeting.application.service;

import com.only.ai.meeting.application.command.CreateBookingCommand;
import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.BookingStatus;
import com.only.ai.meeting.domain.model.MeetingRoom;
import com.only.ai.meeting.domain.repository.BookingRepository;
import com.only.ai.meeting.domain.repository.MeetingRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约应用服务
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    /**
     * 创建预约
     */
    @Transactional
    public Booking createBooking(CreateBookingCommand command) {
        // 验证会议室是否存在
        MeetingRoom room = meetingRoomRepository.findById(command.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("会议室不存在");
        }

        // 验证会议室是否可用
        if (!room.isAvailable()) {
            throw new IllegalArgumentException("会议室不可用");
        }

        // 验证参会人数
        if (command.getAttendeeCount() > room.getCapacity()) {
            throw new IllegalArgumentException("参会人数超过会议室容量");
        }

        // 验证时间范围（只允许未来30天内预约）
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(30);
        if (command.getDate().isBefore(today) || command.getDate().isAfter(maxDate)) {
            throw new IllegalArgumentException("只能预约未来30天内的会议室");
        }

        // 验证不能预约过去的时间段或已开始的时间段
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime meetingStart = LocalDateTime.of(command.getDate(), command.getStartTime());
        if (meetingStart.isBefore(now)) {
            throw new IllegalArgumentException("不允许预约过去的时间段或已开始的时间段");
        }

        // 验证会议时长（30分钟到4小时）
        long minutes = java.time.Duration.between(command.getStartTime(), command.getEndTime()).toMinutes();
        if (minutes < 30 || minutes > 240) {
            throw new IllegalArgumentException("会议时长必须在30分钟到4小时之间");
        }

        // 检查时间冲突（乐观锁：先检查再插入）
        if (bookingRepository.existsConflict(
                command.getRoomId(),
                command.getDate(),
                command.getStartTime(),
                command.getEndTime(),
                null
        )) {
            throw new IllegalArgumentException("该时间段已被预约");
        }

        // 幂等性检查：检查是否已存在相同的预约
        // 通过数据库唯一约束保证，这里可以添加额外的检查

        // 创建预约
        Booking booking = new Booking();
        booking.setUserId(command.getUserId());
        booking.setUserName(command.getUserName());
        booking.setRoomId(command.getRoomId());
        booking.setRoomName(room.getName());
        booking.setDate(command.getDate());
        booking.setStartTime(command.getStartTime());
        booking.setEndTime(command.getEndTime());
        booking.setSubject(command.getSubject());
        booking.setAttendeeCount(command.getAttendeeCount());
        booking.setRemark(command.getRemark());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreateTime(LocalDateTime.now());

        booking.validate();
        bookingRepository.save(booking);

        return booking;
    }

    /**
     * 取消预约
     */
    @Transactional
    public void cancelBooking(Long bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("预约不存在");
        }

        // 权限验证：只能取消自己的预约
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作他人的预约");
        }

        // 验证取消时间限制（距离会议开始时间少于5分钟时不允许取消）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime meetingStart = LocalDateTime.of(booking.getDate(), booking.getStartTime());
        long minutesBeforeStart = java.time.Duration.between(now, meetingStart).toMinutes();
        if (minutesBeforeStart < 5 && minutesBeforeStart > 0) {
            throw new IllegalArgumentException("距离会议开始时间少于5分钟，不允许取消");
        }

        booking.cancel();
        bookingRepository.updateStatus(bookingId, BookingStatus.CANCELLED);
    }

    /**
     * 签到
     */
    @Transactional
    public void signIn(Long bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("预约不存在");
        }

        // 权限验证
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作他人的预约");
        }

        // 验证签到时间窗口
        LocalDateTime now = LocalDateTime.now();
        if (!booking.canSignIn(now)) {
            throw new IllegalArgumentException("不在签到时间窗口内（会议开始前10分钟至开始后15分钟）");
        }

        booking.signIn(now);
        bookingRepository.updateStatus(bookingId, BookingStatus.SIGNED_IN);
    }
}
