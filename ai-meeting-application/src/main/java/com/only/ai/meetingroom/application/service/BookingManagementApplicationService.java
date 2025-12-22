package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.service.BookingDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约管理应用服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class BookingManagementApplicationService {
    private final BookingRepository bookingRepository;
    private final BookingDomainService bookingDomainService;

    public BookingManagementApplicationService(BookingRepository bookingRepository,
                                                BookingDomainService bookingDomainService) {
        this.bookingRepository = bookingRepository;
        this.bookingDomainService = bookingDomainService;
    }

    /**
     * 获取用户的预约列表
     */
    public List<Booking> getUserBookings(User.UserId userId) {
        return bookingRepository.findByUserId(userId);
    }

    /**
     * 根据日期范围获取用户的预约列表
     */
    public List<Booking> getUserBookingsByDateRange(User.UserId userId, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * 获取预约详情
     */
    public Booking getBookingDetail(Booking.BookingId bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));
    }

    /**
     * 取消预约
     */
    @Transactional
    public Booking cancelBooking(Booking.BookingId bookingId, User.UserId userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        // 验证权限
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalStateException("只能取消自己创建的预约");
        }

        // 验证取消时间
        bookingDomainService.validateCancelTime(booking);

        // 取消预约
        Booking cancelledBooking = booking.cancel();
        return bookingRepository.save(cancelledBooking);
    }

    /**
     * 签到
     */
    @Transactional
    public Booking checkIn(Booking.BookingId bookingId, User.UserId userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        // 验证权限
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalStateException("只能对自己的预约进行签到");
        }

        // 验证签到时间
        bookingDomainService.validateCheckInTime(booking);

        // 签到
        Booking checkedInBooking = booking.checkIn();
        return bookingRepository.save(checkedInBooking);
    }
}

