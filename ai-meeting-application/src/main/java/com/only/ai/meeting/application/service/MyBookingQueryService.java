package com.only.ai.meeting.application.service;

import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.BookingStatus;
import com.only.ai.meeting.domain.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 我的预约查询应用服务
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Service
public class MyBookingQueryService {

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * 查询我的预约
     */
    public List<Booking> queryMyBookings(String userId, LocalDate startDate, LocalDate endDate, String status) {
        List<Booking> bookings;

        if (startDate != null && endDate != null) {
            bookings = bookingRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        } else {
            bookings = bookingRepository.findByUserId(userId);
        }

        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            BookingStatus bookingStatus = BookingStatus.valueOf(status);
            bookings = bookings.stream()
                    .filter(booking -> booking.getStatus() == bookingStatus)
                    .collect(Collectors.toList());
        }

        return bookings;
    }

    /**
     * 获取预约详情
     */
    public Booking getBookingDetail(Long bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            return null;
        }
        // 权限验证：只能查看自己的预约
        if (!booking.getUserId().equals(userId)) {
            return null;
        }
        return booking;
    }
}
