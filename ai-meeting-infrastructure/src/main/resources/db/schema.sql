-- 会议室表
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '会议室名称',
    `location` VARCHAR(100) NOT NULL COMMENT '地点/楼层',
    `capacity` INT NOT NULL COMMENT '可容纳人数',
    `equipment` TEXT COMMENT '支持设备（JSON格式）',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态（0-可用，1-不可用）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_status` (`status`),
    INDEX `idx_location` (`location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议室表';

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 预约记录表
CREATE TABLE IF NOT EXISTS `booking` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `room_id` BIGINT NOT NULL COMMENT '会议室ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `date` DATE NOT NULL COMMENT '日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `subject` VARCHAR(200) NOT NULL COMMENT '会议主题',
    `attendee_count` INT COMMENT '参会人数',
    `remark` VARCHAR(500) COMMENT '备注',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态（0-待开始，1-已签到，2-已结束，3-已取消）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_room_date` (`room_id`, `date`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_date_time` (`date`, `start_time`, `end_time`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

