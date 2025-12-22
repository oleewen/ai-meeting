package com.only.ai.meetingroom.infrastructure.repository;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.infrastructure.factory.BookingFactory;
import com.only.ai.meetingroom.infrastructure.mapper.BookingMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预约记录仓储实现
 *
 * @author only
 * @since 2024-01-01
 */
@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private final BookingMapper bookingMapper;

    public BookingRepositoryImpl(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Optional<Booking> findById(Booking.BookingId id) {
        return Optional.ofNullable(BookingFactory.toDomain(bookingMapper.selectById(id.value())));
    }

    @Override
    public List<Booking> findByUserId(com.only.ai.meetingroom.domain.model.User.UserId userId) {
        return bookingMapper.selectByUserId(userId.value()).stream()
                .map(BookingFactory::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserIdAndDateRange(com.only.ai.meetingroom.domain.model.User.UserId userId,
                                                   LocalDate startDate, LocalDate endDate) {
        return bookingMapper.selectByUserIdAndDateRange(userId.value(), startDate, endDate).stream()
                .map(BookingFactory::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByRoomIdAndDate(Room.RoomId roomId, LocalDate date) {
        return bookingMapper.selectByRoomIdAndDate(roomId.value(), date).stream()
                .map(BookingFactory::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasConflict(Room.RoomId roomId, LocalDate date, LocalTime startTime, LocalTime endTime,
                               Booking.BookingId excludeBookingId) {
        int count = bookingMapper.countConflicts(
                roomId.value(),
                date,
                startTime,
                endTime,
                excludeBookingId != null ? excludeBookingId.value() : null
        );
        return count > 0;
    }

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null || bookingMapper.selectById(booking.getId().value()) == null) {
            // 新建预约
            com.only.ai.meetingroom.infrastructure.po.BookingPO po = BookingFactory.toPO(booking);
            bookingMapper.insert(po);
            // 重新查询以获取生成的ID
            if (po.getId() != null) {
                return findById(new Booking.BookingId(po.getId()))
                        .orElseThrow(() -> new IllegalStateException("保存预约后无法查询到记录"));
            } else {
                throw new IllegalStateException("保存预约后未生成ID");
            }
        } else {
            // 更新预约
            bookingMapper.update(BookingFactory.toPO(booking));
            return booking;
        }
    }

    @Override
    public void delete(Booking booking) {
        bookingMapper.delete(booking.getId().value());
    }
}

