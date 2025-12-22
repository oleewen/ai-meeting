-- H2数据库初始化脚本
-- 会议室预约系统

-- 创建会议室表
CREATE TABLE IF NOT EXISTS meeting_room (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    equipments VARCHAR(500),
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建会议室索引
CREATE INDEX IF NOT EXISTS idx_location ON meeting_room(location);
CREATE INDEX IF NOT EXISTS idx_capacity ON meeting_room(capacity);
CREATE INDEX IF NOT EXISTS idx_active ON meeting_room(is_active);

-- 创建预约表
CREATE TABLE IF NOT EXISTS booking (
    id VARCHAR(50) PRIMARY KEY,
    meeting_room_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    attendee_count INT NOT NULL,
    notes VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (meeting_room_id) REFERENCES meeting_room(id)
);

-- 创建预约索引
CREATE INDEX IF NOT EXISTS idx_meeting_room_time ON booking(meeting_room_id, start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_user_id ON booking(user_id);
CREATE INDEX IF NOT EXISTS idx_status ON booking(status);
CREATE INDEX IF NOT EXISTS idx_start_time ON booking(start_time);

-- 创建用户表（user是H2保留关键字，使用反引号）
CREATE TABLE IF NOT EXISTS "user" (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户索引
CREATE INDEX IF NOT EXISTS idx_email ON "user"(email);
CREATE INDEX IF NOT EXISTS idx_role ON "user"(role);
