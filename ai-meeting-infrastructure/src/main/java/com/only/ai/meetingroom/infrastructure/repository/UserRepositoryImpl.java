package com.only.ai.meetingroom.infrastructure.repository;

import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.model.UserId;
import com.only.ai.meetingroom.domain.repository.UserRepository;
import com.only.ai.meetingroom.infrastructure.mapper.UserMapper;
import com.only.ai.meetingroom.infrastructure.po.UserPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 用户仓储实现
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private UserMapper userMapper;

    @Override
    public Optional<User> findById(UserId id) {
        UserPO po = userMapper.selectById(id.value());
        return po != null ? Optional.of(po.toDomain()) : Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserPO po = userMapper.selectByUsername(username);
        return po != null ? Optional.of(po.toDomain()) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserPO po = userMapper.selectByEmail(email);
        return po != null ? Optional.of(po.toDomain()) : Optional.empty();
    }

    @Override
    public void save(User user) {
        UserPO po = UserPO.fromDomain(user);
        if (userMapper.selectById(po.getId()) != null) {
            userMapper.update(po);
        } else {
            userMapper.insert(po);
        }
    }

    @Override
    public void delete(UserId id) {
        userMapper.deleteById(id.value());
    }
}