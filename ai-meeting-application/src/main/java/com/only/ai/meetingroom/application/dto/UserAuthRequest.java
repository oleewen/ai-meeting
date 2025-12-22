package com.only.ai.meetingroom.application.dto;

/**
 * 用户认证请求DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class UserAuthRequest {
    
    private String username;
    private String password;

    public UserAuthRequest() {
    }

    public UserAuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserAuthRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}