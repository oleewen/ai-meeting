-- H2数据库初始数据
-- 会议室预约系统测试数据

-- 插入测试会议室数据
INSERT INTO meeting_room (id, name, location, capacity, equipments, description, is_active) VALUES
('room-001', '会议室A', '1楼东区', 10, 'PROJECTOR,WHITEBOARD,PHONE_CONFERENCE', '标准会议室，适合小型会议', true),
('room-002', '会议室B', '1楼西区', 20, 'PROJECTOR,WHITEBOARD,VIDEO_CONFERENCE,PHONE_CONFERENCE', '大型会议室，配备视频会议设备', true),
('room-003', '培训室C', '2楼北区', 50, 'PROJECTOR,WHITEBOARD,AUDIO_SYSTEM', '培训专用室，可容纳50人', true),
('room-004', '小会议室D', '2楼南区', 6, 'WHITEBOARD,PHONE_CONFERENCE', '小型讨论室', true),
('room-005', '董事会议室', '3楼', 15, 'PROJECTOR,WHITEBOARD,VIDEO_CONFERENCE,PHONE_CONFERENCE,AUDIO_SYSTEM', '高级会议室，董事会专用', true);

-- 插入测试用户数据
INSERT INTO user (id, name, email, role, is_active) VALUES
('user-001', '张三', 'zhangsan@company.com', 'USER', true),
('user-002', '李四', 'lisi@company.com', 'USER', true),
('user-003', '王五', 'wangwu@company.com', 'ADMIN', true),
('user-004', '赵六', 'zhaoliu@company.com', 'ROOM_ADMIN', true),
('user-005', '钱七', 'qianqi@company.com', 'USER', true);

-- 插入一些测试预约数据（未来的预约）
INSERT INTO booking (id, meeting_room_id, user_id, title, start_time, end_time, attendee_count, notes, status) VALUES
('booking-001', 'room-001', 'user-001', '项目启动会议', DATEADD('DAY', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP)), 8, '讨论新项目启动事宜', 'BOOKED'),
('booking-002', 'room-002', 'user-002', '月度总结会', DATEADD('DAY', 2, CURRENT_TIMESTAMP), DATEADD('HOUR', 1, DATEADD('DAY', 2, CURRENT_TIMESTAMP)), 15, '月度工作总结和下月计划', 'BOOKED'),
('booking-003', 'room-003', 'user-003', '新员工培训', DATEADD('DAY', 3, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, DATEADD('DAY', 3, CURRENT_TIMESTAMP)), 30, '新员工入职培训', 'BOOKED');