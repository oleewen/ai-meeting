package com.only.ai.meetingroom.domain.repository;

import com.only.ai.meetingroom.domain.model.User;

import java.util.Optional;

/**
 * 用户仓储接口
 *
 * @author only
 * @since 2024-01-01
 */
public interface UserRepository {
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(User.UserId id);

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 保存用户
     */
    void save(User user);
}

