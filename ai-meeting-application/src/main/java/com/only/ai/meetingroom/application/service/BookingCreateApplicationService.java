package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.repository.RoomRepository;
import com.only.ai.meetingroom.domain.service.BookingDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约创建应用服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class BookingCreateApplicationService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingDomainService bookingDomainService;

    public BookingCreateApplicationService(BookingRepository bookingRepository,
                                           RoomRepository roomRepository,
                                           BookingDomainService bookingDomainService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bookingDomainService = bookingDomainService;
    }

    /**
     * 创建预约
     */
    @Transactional
    public Booking createBooking(Room.RoomId roomId, User.UserId userId, LocalDate date,
                                  LocalTime startTime, LocalTime endTime, String subject,
                                  Integer attendeeCount, String remark) {
        // 验证会议室存在
        roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("会议室不存在"));

        // 验证业务规则
        bookingDomainService.validateBookingTimeRange(date, startTime, endTime);

        // 检查冲突
        bookingDomainService.checkConflict(roomId, date, startTime, endTime, null);

        // 创建预约
        Booking booking = new Booking(
                null, // ID由数据库生成
                roomId,
                userId,
                date,
                startTime,
                endTime,
                subject,
                attendeeCount,
                remark,
                Booking.BookingStatus.PENDING,
                null // 创建时间由数据库生成
        );

        // 保存并返回包含ID的预约
        return bookingRepository.save(booking);
    }
}

