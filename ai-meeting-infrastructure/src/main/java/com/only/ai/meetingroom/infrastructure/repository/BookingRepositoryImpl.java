package com.only.ai.meetingroom.infrastructure.repository;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.BookingId;
import com.only.ai.meetingroom.domain.model.BookingStatus;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;
import com.only.ai.meetingroom.domain.model.UserId;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.infrastructure.mapper.BookingMapper;
import com.only.ai.meetingroom.infrastructure.po.BookingPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预约仓储实现
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Repository
public class BookingRepositoryImpl implements BookingRepository {

    @Resource
    private BookingMapper bookingMapper;

    @Override
    public Optional<Booking> findById(BookingId id) {
        BookingPO po = bookingMapper.selectById(id.value());
        return po != null ? Optional.of(po.toDomain()) : Optional.empty();
    }

    @Override
    public List<Booking> findConflictingBookings(MeetingRoomId meetingRoomId, TimeSlot timeSlot) {
        List<BookingPO> pos = bookingMapper.selectConflictingBookings(
                meetingRoomId.value(), timeSlot.getStartTime(), timeSlot.getEndTime());
        return pos.stream().map(BookingPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findActiveBookingsByRoomAndDate(MeetingRoomId meetingRoomId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<BookingPO> pos = bookingMapper.selectActiveBookingsByRoomAndDateRange(
                meetingRoomId.value(), startOfDay, endOfDay);
        return pos.stream().map(BookingPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserId(UserId userId) {
        List<BookingPO> pos = bookingMapper.selectByUserId(userId.value());
        return pos.stream().map(BookingPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserIdAndDateRange(UserId userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<BookingPO> pos = bookingMapper.selectByUserIdAndDateRange(
                userId.value(), startDateTime, endDateTime);
        return pos.stream().map(BookingPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserIdAndStatus(UserId userId, BookingStatus status) {
        List<BookingPO> pos = bookingMapper.selectByUserIdAndStatus(userId.value(), status.name());
        return pos.stream().map(BookingPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserIdAndDateRangeAndStatus(UserId userId, LocalDate startDate, LocalDate endDate, BookingStatus status) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<BookingPO> pos = bookingMapper.selectByUserIdAndDateRangeAndStatus(
                userId.value(), startDateTime, endDateTime, status.name());
        return pos.stream().map(BookingPO::toDomain).collect(Collectors.toList());
    }

    @Override
    public void save(Booking booking) {
        BookingPO po = BookingPO.fromDomain(booking);
        if (bookingMapper.selectById(po.getId()) != null) {
            bookingMapper.update(po);
        } else {
            bookingMapper.insert(po);
        }
    }

    @Override
    public void delete(BookingId id) {
        bookingMapper.deleteById(id.value());
    }
}