-- 会议室预约系统数据库表结构
-- 创建日期: 2025-01-27

-- 会议室表
CREATE TABLE IF NOT EXISTS meeting_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '会议室名称',
    location VARCHAR(100) NOT NULL COMMENT '地点/楼层',
    capacity INT NOT NULL CHECK (capacity > 0) COMMENT '可容纳人数',
    equipment TEXT COMMENT '支持设备（JSON格式）',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '可用状态',
    description VARCHAR(500) COMMENT '描述信息',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '会议室表';

-- 创建索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_meeting_room_location ON meeting_room(location);
CREATE INDEX IF NOT EXISTS idx_meeting_room_status ON meeting_room(status);

-- 预约表
CREATE TABLE IF NOT EXISTS booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL COMMENT '预约人用户ID',
    user_name VARCHAR(50) NOT NULL COMMENT '预约人姓名',
    room_id BIGINT NOT NULL COMMENT '会议室ID',
    room_name VARCHAR(100) NOT NULL COMMENT '会议室名称（冗余字段）',
    date DATE NOT NULL COMMENT '预约日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    subject VARCHAR(200) NOT NULL COMMENT '会议主题',
    attendee_count INT NOT NULL CHECK (attendee_count > 0) COMMENT '参会人数',
    remark VARCHAR(500) COMMENT '备注',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    sign_in_time TIMESTAMP COMMENT '签到时间',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (room_id) REFERENCES meeting_room(id)
) COMMENT '预约表';

-- 创建唯一约束（如果不存在）
CREATE UNIQUE INDEX IF NOT EXISTS uk_user_room_time ON booking(user_id, room_id, date, start_time, end_time);

-- 创建索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_booking_user_id ON booking(user_id);
CREATE INDEX IF NOT EXISTS idx_booking_room_id ON booking(room_id);
CREATE INDEX IF NOT EXISTS idx_booking_date ON booking(date);
CREATE INDEX IF NOT EXISTS idx_booking_status ON booking(status);
CREATE INDEX IF NOT EXISTS idx_booking_date_time ON booking(date, start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_booking_user_date_status ON booking(user_id, date, status);

-- 用户表（可选，如果用户信息需要本地存储）
CREATE TABLE IF NOT EXISTS user (
    id VARCHAR(50) PRIMARY KEY COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(100) COMMENT '部门',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';
