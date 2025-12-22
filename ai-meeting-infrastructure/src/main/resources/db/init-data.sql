-- 初始化用户数据（密码为：password123）
INSERT INTO `user` (`username`, `password`, `name`) VALUES
('admin', 'password123', '管理员'),
('user1', 'password123', '用户1'),
('user2', 'password123', '用户2')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 初始化会议室数据
INSERT INTO `room` (`name`, `location`, `capacity`, `equipment`, `status`) VALUES
('会议室A', '1楼', 10, '["投影仪", "白板"]', 0),
('会议室B', '1楼', 20, '["投影仪", "视频会议", "白板"]', 0),
('会议室C', '2楼', 30, '["投影仪", "视频会议", "白板", "音响"]', 0),
('会议室D', '2楼', 15, '["投影仪", "白板"]', 0),
('会议室E', '3楼', 50, '["投影仪", "视频会议", "白板", "音响", "大屏"]', 0)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

