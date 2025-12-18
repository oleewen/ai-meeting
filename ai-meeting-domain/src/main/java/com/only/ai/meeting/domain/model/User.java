package com.only.ai.meeting.domain.model;

/**
 * 用户领域模型
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String department;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 验证用户信息
     */
    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (name == null || name.trim().isEmpty() || name.length() > 50) {
            throw new IllegalArgumentException("用户姓名不能为空且长度不能超过50");
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
