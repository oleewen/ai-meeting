package com.only.ai.meetingroom.domain.repository;

import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.model.UserId;

import java.util.Optional;

/**
 * 用户仓储接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public interface UserRepository {
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(UserId id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 保存用户
     */
    void save(User user);
    
    /**
     * 删除用户
     */
    void delete(UserId id);
}