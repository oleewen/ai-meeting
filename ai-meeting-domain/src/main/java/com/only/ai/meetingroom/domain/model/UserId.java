package com.only.ai.meetingroom.domain.model;

import com.only.ai.common.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * 用户ID值对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class UserId implements ValueObject<String> {
    private final String id;

    public UserId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User id cannot be null or empty");
        }
        this.id = id;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public static UserId of(String id) {
        return new UserId(id);
    }

    @Override
    public String value() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(id, userId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserId{" + "id='" + id + '\'' + '}';
    }
}