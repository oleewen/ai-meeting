package com.only.ai.meetingroom.application.dto;

/**
 * 用户认证响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class UserAuthResponse {
    
    private boolean success;
    private String userId;
    private String username;
    private String role;
    private String message;

    public UserAuthResponse() {
    }

    public UserAuthResponse(boolean success, String userId, String username, String role, String message) {
        this.success = success;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserAuthResponse{" +
                "success=" + success +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}