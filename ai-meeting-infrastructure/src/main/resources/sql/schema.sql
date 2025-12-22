-- 会议室预约系统数据库表结构

-- 会议室表
CREATE TABLE meeting_rooms (
    id VARCHAR(36) PRIMARY KEY COMMENT '会议室ID',
    name VARCHAR(100) NOT NULL COMMENT '会议室名称',
    location VARCHAR(200) NOT NULL COMMENT '会议室位置',
    capacity INT NOT NULL COMMENT '容量',
    equipments JSON COMMENT '设备列表',
    active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_location (location),
    INDEX idx_capacity (capacity),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议室表';

-- 用户表
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password_hash VARCHAR(255) COMMENT '密码哈希',
    email VARCHAR(100) COMMENT '邮箱',
    display_name VARCHAR(100) COMMENT '显示名称',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER' COMMENT '用户角色',
    active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 预约表
CREATE TABLE bookings (
    id VARCHAR(36) PRIMARY KEY COMMENT '预约ID',
    meeting_room_id VARCHAR(36) NOT NULL COMMENT '会议室ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    subject VARCHAR(200) NOT NULL COMMENT '会议主题',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    attendee_count INT COMMENT '参会人数',
    notes TEXT COMMENT '备注',
    status ENUM('ACTIVE', 'CANCELLED', 'CHECKED_IN') DEFAULT 'ACTIVE' COMMENT '预约状态',
    checked_in_at DATETIME NULL COMMENT '签到时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (meeting_room_id) REFERENCES meeting_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_meeting_room_time (meeting_room_id, start_time, end_time),
    INDEX idx_user_bookings (user_id, start_time),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_date_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 插入测试数据
INSERT INTO meeting_rooms (id, name, location, capacity, equipments) VALUES
('room-001', '会议室A', '1楼东区', 8, '["PROJECTOR", "WHITEBOARD"]'),
('room-002', '会议室B', '1楼西区', 12, '["PROJECTOR", "VIDEO_CONFERENCE", "LARGE_SCREEN"]'),
('room-003', '会议室C', '2楼东区', 6, '["WHITEBOARD"]'),
('room-004', '会议室D', '2楼西区', 20, '["PROJECTOR", "VIDEO_CONFERENCE", "WHITEBOARD", "LARGE_SCREEN"]');

INSERT INTO users (id, username, email, display_name, role) VALUES
('user-001', 'admin', 'admin@company.com', '系统管理员', 'ADMIN'),
('user-002', 'zhangsan', 'zhangsan@company.com', '张三', 'USER'),
('user-003', 'lisi', 'lisi@company.com', '李四', 'USER'),
('user-004', 'wangwu', 'wangwu@company.com', '王五', 'USER');