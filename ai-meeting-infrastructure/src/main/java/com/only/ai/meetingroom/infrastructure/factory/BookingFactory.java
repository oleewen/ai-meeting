package com.only.ai.meetingroom.infrastructure.factory;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.infrastructure.po.BookingPO;

/**
 * 预约记录工厂（PO和领域模型转换）
 *
 * @author only
 * @since 2024-01-01
 */
public class BookingFactory {
    /**
     * PO转领域模型
     */
    public static Booking toDomain(BookingPO po) {
        if (po == null) {
            return null;
        }
        Booking.BookingStatus status;
        switch (po.getStatus()) {
            case 0:
                status = Booking.BookingStatus.PENDING;
                break;
            case 1:
                status = Booking.BookingStatus.CHECKED_IN;
                break;
            case 2:
                status = Booking.BookingStatus.COMPLETED;
                break;
            case 3:
                status = Booking.BookingStatus.CANCELLED;
                break;
            default:
                status = Booking.BookingStatus.PENDING;
        }
        return new Booking(
                new Booking.BookingId(po.getId()),
                new com.only.ai.meetingroom.domain.model.Room.RoomId(po.getRoomId()),
                new com.only.ai.meetingroom.domain.model.User.UserId(po.getUserId()),
                po.getDate(),
                po.getStartTime(),
                po.getEndTime(),
                po.getSubject(),
                po.getAttendeeCount(),
                po.getRemark(),
                status,
                po.getCreateTime()
        );
    }

    /**
     * 领域模型转PO
     */
    public static BookingPO toPO(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingPO po = new BookingPO();
        po.setId(booking.getId() != null ? booking.getId().value() : null);
        po.setRoomId(booking.getRoomId().value());
        po.setUserId(booking.getUserId().value());
        po.setDate(booking.getDate());
        po.setStartTime(booking.getStartTime());
        po.setEndTime(booking.getEndTime());
        po.setSubject(booking.getSubject());
        po.setAttendeeCount(booking.getAttendeeCount());
        po.setRemark(booking.getRemark());
        int status;
        switch (booking.getStatus()) {
            case PENDING:
                status = 0;
                break;
            case CHECKED_IN:
                status = 1;
                break;
            case COMPLETED:
                status = 2;
                break;
            case CANCELLED:
                status = 3;
                break;
            default:
                status = 0;
        }
        po.setStatus(status);
        po.setCreateTime(booking.getCreateTime());
        return po;
    }
}

