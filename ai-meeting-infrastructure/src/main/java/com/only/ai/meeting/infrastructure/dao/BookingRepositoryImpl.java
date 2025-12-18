package com.only.ai.meeting.infrastructure.dao;

import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.BookingStatus;
import com.only.ai.meeting.domain.repository.BookingRepository;
import com.only.ai.meeting.infrastructure.entity.BookingEntity;
import com.only.ai.meeting.infrastructure.factory.EntityFactory;
import com.only.ai.meeting.infrastructure.mapper.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约资源库实现类
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Repository
public class BookingRepositoryImpl implements BookingRepository {

    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public Booking findById(Long id) {
        BookingEntity entity = bookingMapper.findById(id);
        return EntityFactory.toDomainModel(entity);
    }

    @Override
    public List<Booking> findByUserId(String userId) {
        List<BookingEntity> entities = bookingMapper.findByUserId(userId);
        return EntityFactory.toBookingDomainModelList(entities);
    }

    @Override
    public List<Booking> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        List<BookingEntity> entities = bookingMapper.findByUserIdAndDateRange(userId, startDate, endDate);
        return EntityFactory.toBookingDomainModelList(entities);
    }

    @Override
    public List<Booking> findByRoomIdAndDate(Long roomId, LocalDate date) {
        List<BookingEntity> entities = bookingMapper.findByRoomIdAndDate(roomId, date);
        return EntityFactory.toBookingDomainModelList(entities);
    }

    @Override
    public boolean existsConflict(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeBookingId) {
        int count = bookingMapper.countConflicts(roomId, date, startTime, endTime, excludeBookingId);
        return count > 0;
    }

    @Override
    public void save(Booking booking) {
        BookingEntity entity = EntityFactory.toEntity(booking);
        if (entity.getId() == null) {
            bookingMapper.insert(entity);
            booking.setId(entity.getId());
        } else {
            // 更新操作需要单独实现
            bookingMapper.updateStatus(entity.getId(), entity.getStatus());
        }
    }

    @Override
    public void updateStatus(Long id, BookingStatus status) {
        bookingMapper.updateStatus(id, status.name());
    }

    @Override
    public List<Booking> findCompletedBookings(LocalDateTime beforeTime) {
        List<BookingEntity> entities = bookingMapper.findCompletedBookings(beforeTime);
        return EntityFactory.toBookingDomainModelList(entities);
    }
}
