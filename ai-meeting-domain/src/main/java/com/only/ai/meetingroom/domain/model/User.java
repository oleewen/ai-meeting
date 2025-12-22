package com.only.ai.meetingroom.domain.model;

import java.util.Objects;

/**
 * 用户实体
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class User {
    private UserId id;
    private String username;
    private String email;
    private String displayName;
    private UserRole role;
    private boolean active;

    public User(UserId id, String username, String email, String displayName, UserRole role) {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        this.id = id;
        this.username = username.trim();
        this.email = email != null ? email.trim() : null;
        this.displayName = displayName != null ? displayName.trim() : username.trim();
        this.role = role;
        this.active = true;
    }

    /**
     * 检查用户是否有权限访问指定预约
     */
    public boolean canAccessBooking(Booking booking) {
        return role.isAdmin() || booking.belongsTo(this.id);
    }

    /**
     * 检查用户是否有权限修改指定预约
     */
    public boolean canModifyBooking(Booking booking) {
        return role.isAdmin() || booking.belongsTo(this.id);
    }

    /**
     * 激活用户
     */
    public void activate() {
        this.active = true;
    }

    /**
     * 停用用户
     */
    public void deactivate() {
        this.active = false;
    }

    // Getters
    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}