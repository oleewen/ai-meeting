package com.only.ai.meetingroom.infrastructure.repository;

import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.repository.UserRepository;
import com.only.ai.meetingroom.infrastructure.factory.UserFactory;
import com.only.ai.meetingroom.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户仓储实现
 *
 * @author only
 * @since 2024-01-01
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(User.UserId id) {
        return Optional.ofNullable(UserFactory.toDomain(userMapper.selectById(id.value())));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(UserFactory.toDomain(userMapper.selectByUsername(username)));
    }

    @Override
    public void save(User user) {
        if (user.getId() == null || userMapper.selectById(user.getId().value()) == null) {
            userMapper.insert(UserFactory.toPO(user));
        } else {
            userMapper.update(UserFactory.toPO(user));
        }
    }
}

