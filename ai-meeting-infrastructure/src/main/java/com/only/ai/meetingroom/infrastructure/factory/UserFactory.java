package com.only.ai.meetingroom.infrastructure.factory;

import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.infrastructure.po.UserPO;

/**
 * 用户工厂（PO和领域模型转换）
 *
 * @author only
 * @since 2024-01-01
 */
public class UserFactory {
    /**
     * PO转领域模型
     */
    public static User toDomain(UserPO po) {
        if (po == null) {
            return null;
        }
        return new User(
                new User.UserId(po.getId()),
                po.getUsername(),
                po.getPassword(),
                po.getName()
        );
    }

    /**
     * 领域模型转PO
     */
    public static UserPO toPO(User user) {
        if (user == null) {
            return null;
        }
        UserPO po = new UserPO();
        po.setId(user.getId().value());
        po.setUsername(user.getUsername());
        po.setPassword(user.getPassword());
        po.setName(user.getName());
        return po;
    }
}

