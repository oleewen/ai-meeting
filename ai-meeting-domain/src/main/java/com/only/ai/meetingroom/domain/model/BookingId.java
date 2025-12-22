package com.only.ai.meetingroom.domain.model;

import com.only.ai.common.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * 预约ID值对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class BookingId implements ValueObject<String> {
    private final String id;

    public BookingId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking id cannot be null or empty");
        }
        this.id = id;
    }

    public static BookingId generate() {
        return new BookingId(UUID.randomUUID().toString());
    }

    public static BookingId of(String id) {
        return new BookingId(id);
    }

    @Override
    public String value() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingId bookingId = (BookingId) o;
        return Objects.equals(id, bookingId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BookingId{" + "id='" + id + '\'' + '}';
    }
}