-- H2数据库初始化脚本
-- 会议室预约系统

-- 创建会议室表
CREATE TABLE IF NOT EXISTS meeting_room (
    id VARCHAR(50) PRIMARY KEY COMMENT '会议室ID',
    name VARCHAR(100) NOT NULL COMMENT '会议室名称',
    location VARCHAR(200) NOT NULL COMMENT '会议室位置',
    capacity INT NOT NULL COMMENT '容纳人数',
    equipments VARCHAR(500) COMMENT '设备列表，逗号分隔',
    description VARCHAR(500) COMMENT '会议室描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建会议室索引
CREATE INDEX IF NOT EXISTS idx_location ON meeting_room(location);
CREATE INDEX IF NOT EXISTS idx_capacity ON meeting_room(capacity);
CREATE INDEX IF NOT EXISTS idx_active ON meeting_room(is_active);

-- 创建预约表
CREATE TABLE IF NOT EXISTS booking (
    id VARCHAR(50) PRIMARY KEY COMMENT '预约ID',
    meeting_room_id VARCHAR(50) NOT NULL COMMENT '会议室ID',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '会议标题',
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束时间',
    attendee_count INT NOT NULL COMMENT '参会人数',
    notes VARCHAR(1000) COMMENT '备注',
    status VARCHAR(20) NOT NULL COMMENT '预约状态：BOOKED,CANCELLED,COMPLETED,CHECKED_IN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (meeting_room_id) REFERENCES meeting_room(id)
);

-- 创建预约索引
CREATE INDEX IF NOT EXISTS idx_meeting_room_time ON booking(meeting_room_id, start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_user_id ON booking(user_id);
CREATE INDEX IF NOT EXISTS idx_status ON booking(status);
CREATE INDEX IF NOT EXISTS idx_start_time ON booking(start_time);

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id VARCHAR(50) PRIMARY KEY COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '用户姓名',
    email VARCHAR(200) NOT NULL UNIQUE COMMENT '邮箱',
    role VARCHAR(20) NOT NULL COMMENT '角色：USER,ADMIN,ROOM_ADMIN',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建用户索引
CREATE INDEX IF NOT EXISTS idx_email ON user(email);
CREATE INDEX IF NOT EXISTS idx_role ON user(role);
