package com.only.ai.meetingroom.infrastructure.mapper;

import com.only.ai.meetingroom.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    UserPO selectById(@Param("id") String id);
    
    /**
     * 根据用户名查询用户
     */
    UserPO selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    UserPO selectByEmail(@Param("email") String email);
    
    /**
     * 插入用户
     */
    int insert(UserPO user);
    
    /**
     * 更新用户
     */
    int update(UserPO user);
    
    /**
     * 删除用户
     */
    int deleteById(@Param("id") String id);
}