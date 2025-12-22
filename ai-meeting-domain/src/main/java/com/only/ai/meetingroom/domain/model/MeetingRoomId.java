package com.only.ai.meetingroom.domain.model;

import com.only.ai.common.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * 会议室ID值对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class MeetingRoomId implements ValueObject<String> {
    private final String id;

    public MeetingRoomId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting room id cannot be null or empty");
        }
        this.id = id;
    }

    public static MeetingRoomId generate() {
        return new MeetingRoomId(UUID.randomUUID().toString());
    }

    public static MeetingRoomId of(String id) {
        return new MeetingRoomId(id);
    }

    @Override
    public String value() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingRoomId that = (MeetingRoomId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MeetingRoomId{" + "id='" + id + '\'' + '}';
    }
}