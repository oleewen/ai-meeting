-- H2数据库初始数据

-- 初始化用户数据（密码为：password123）
-- 使用MERGE INTO语法，如果记录已存在则更新，不存在则插入
MERGE INTO "user" (id, username, password, name) KEY(id) VALUES
(1, 'admin', 'password123', '管理员'),
(2, 'user1', 'password123', '用户1'),
(3, 'user2', 'password123', '用户2');

-- 初始化会议室数据
MERGE INTO room (id, name, location, capacity, equipment, status) KEY(id) VALUES
(1, '会议室A', '1楼', 10, '["投影仪", "白板"]', 0),
(2, '会议室B', '1楼', 20, '["投影仪", "视频会议", "白板"]', 0),
(3, '会议室C', '2楼', 30, '["投影仪", "视频会议", "白板", "音响"]', 0),
(4, '会议室D', '2楼', 15, '["投影仪", "白板"]', 0),
(5, '会议室E', '3楼', 50, '["投影仪", "视频会议", "白板", "音响", "大屏"]', 0);

