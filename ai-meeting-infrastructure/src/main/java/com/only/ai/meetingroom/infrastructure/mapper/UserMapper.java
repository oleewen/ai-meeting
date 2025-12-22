package com.only.ai.meetingroom.infrastructure.mapper;

import com.only.ai.meetingroom.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 *
 * @author only
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper {
    /**
     * 根据ID查询
     */
    UserPO selectById(@Param("id") Long id);

    /**
     * 根据用户名查询
     */
    UserPO selectByUsername(@Param("username") String username);

    /**
     * 插入
     */
    int insert(UserPO user);

    /**
     * 更新
     */
    int update(UserPO user);
}

