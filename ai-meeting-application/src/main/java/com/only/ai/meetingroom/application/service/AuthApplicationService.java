package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户认证应用服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class AuthApplicationService {
    private final UserRepository userRepository;

    public AuthApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 用户登录
     */
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.verifyPassword(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据ID获取用户
     */
    public Optional<User> getUserById(User.UserId userId) {
        return userRepository.findById(userId);
    }
}

