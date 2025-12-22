package com.only.ai.meetingroom.infrastructure.po;

import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.model.UserId;
import com.only.ai.meetingroom.domain.model.UserRole;

import java.time.LocalDateTime;

/**
 * 用户持久化对象
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class UserPO {
    private String id;
    private String username;
    private String passwordHash;
    private String email;
    private String displayName;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;

    public UserPO() {}

    public static UserPO fromDomain(User user) {
        UserPO po = new UserPO();
        po.id = user.getId().value();
        po.username = user.getUsername();
        po.email = user.getEmail();
        po.displayName = user.getDisplayName();
        po.role = user.getRole().name();
        po.active = user.isActive();
        return po;
    }

    public User toDomain() {
        return new User(
                UserId.of(this.id),
                this.username,
                this.email,
                this.displayName,
                UserRole.valueOf(this.role)
        );
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}