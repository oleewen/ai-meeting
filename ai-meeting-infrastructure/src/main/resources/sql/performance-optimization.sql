-- 会议室预约系统性能优化SQL脚本

-- 1. 额外的复合索引优化
-- 为常用查询组合创建复合索引

-- 会议室表优化索引
CREATE INDEX idx_meeting_rooms_location_capacity ON meeting_rooms(location, capacity, active);
CREATE INDEX idx_meeting_rooms_capacity_active ON meeting_rooms(capacity, active);

-- 预约表优化索引
-- 用户预约查询优化（按用户ID和状态查询）
CREATE INDEX idx_bookings_user_status ON bookings(user_id, status, start_time);

-- 用户预约日期范围查询优化
CREATE INDEX idx_bookings_user_date_range ON bookings(user_id, start_time, end_time, status);

-- 会议室冲突检测优化
CREATE INDEX idx_bookings_room_conflict ON bookings(meeting_room_id, status, start_time, end_time);

-- 今日预约查询优化
CREATE INDEX idx_bookings_daily ON bookings(DATE(start_time), user_id, status);

-- 2. 查询性能分析视图
-- 创建视图来简化常用查询

-- 活跃预约视图
CREATE VIEW active_bookings AS
SELECT 
    b.id,
    b.meeting_room_id,
    b.user_id,
    b.subject,
    b.start_time,
    b.end_time,
    b.attendee_count,
    b.status,
    b.checked_in_at,
    mr.name as room_name,
    mr.location as room_location,
    u.display_name as user_name
FROM bookings b
JOIN meeting_rooms mr ON b.meeting_room_id = mr.id
JOIN users u ON b.user_id = u.id
WHERE b.status = 'ACTIVE' AND mr.active = TRUE;

-- 今日预约视图
CREATE VIEW today_bookings AS
SELECT 
    b.*,
    mr.name as room_name,
    mr.location as room_location
FROM bookings b
JOIN meeting_rooms mr ON b.meeting_room_id = mr.id
WHERE DATE(b.start_time) = CURDATE()
AND b.status IN ('ACTIVE', 'CHECKED_IN')
ORDER BY b.start_time;

-- 3. 存储过程优化
-- 创建存储过程来优化复杂查询

DELIMITER //

-- 检查会议室时间冲突的存储过程
CREATE PROCEDURE CheckRoomConflict(
    IN p_room_id VARCHAR(36),
    IN p_start_time DATETIME,
    IN p_end_time DATETIME,
    OUT p_has_conflict BOOLEAN
)
BEGIN
    DECLARE conflict_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO conflict_count
    FROM bookings
    WHERE meeting_room_id = p_room_id
    AND status = 'ACTIVE'
    AND (
        (start_time <= p_start_time AND end_time > p_start_time) OR
        (start_time < p_end_time AND end_time >= p_end_time) OR
        (start_time >= p_start_time AND end_time <= p_end_time)
    );
    
    SET p_has_conflict = (conflict_count > 0);
END //

-- 获取用户预约统计的存储过程
CREATE PROCEDURE GetUserBookingStats(
    IN p_user_id VARCHAR(36),
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        COUNT(*) as total_bookings,
        SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_bookings,
        SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_bookings,
        SUM(CASE WHEN status = 'CHECKED_IN' THEN 1 ELSE 0 END) as checked_in_bookings,
        AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) as avg_duration_minutes
    FROM bookings
    WHERE user_id = p_user_id
    AND DATE(start_time) BETWEEN p_start_date AND p_end_date;
END //

DELIMITER ;

-- 4. 数据库配置优化建议
-- 以下是MySQL配置优化建议（需要在my.cnf中配置）

/*
# InnoDB优化配置
innodb_buffer_pool_size = 1G  # 根据服务器内存调整
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# 查询缓存优化
query_cache_type = 1
query_cache_size = 128M
query_cache_limit = 2M

# 连接优化
max_connections = 200
thread_cache_size = 16

# 临时表优化
tmp_table_size = 64M
max_heap_table_size = 64M

# 排序优化
sort_buffer_size = 2M
read_buffer_size = 1M
read_rnd_buffer_size = 2M
*/

-- 5. 定期维护脚本
-- 创建定期清理和优化的脚本

-- 清理过期的已取消预约（保留3个月）
CREATE EVENT IF NOT EXISTS cleanup_old_cancelled_bookings
ON SCHEDULE EVERY 1 WEEK
DO
DELETE FROM bookings 
WHERE status = 'CANCELLED' 
AND created_at < DATE_SUB(NOW(), INTERVAL 3 MONTH);

-- 优化表结构（每月执行）
CREATE EVENT IF NOT EXISTS optimize_tables
ON SCHEDULE EVERY 1 MONTH
DO
BEGIN
    OPTIMIZE TABLE meeting_rooms;
    OPTIMIZE TABLE users;
    OPTIMIZE TABLE bookings;
END;

-- 6. 监控查询
-- 用于监控系统性能的查询

-- 查看慢查询
-- SELECT * FROM mysql.slow_log WHERE start_time > DATE_SUB(NOW(), INTERVAL 1 DAY);

-- 查看表大小和索引使用情况
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)',
    ROUND((index_length / 1024 / 1024), 2) AS 'Index Size (MB)'
FROM information_schema.tables 
WHERE table_schema = DATABASE()
ORDER BY (data_length + index_length) DESC;

-- 查看索引使用统计
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    INDEX_NAME,
    COUNT_FETCH,
    COUNT_INSERT,
    COUNT_UPDATE,
    COUNT_DELETE
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE OBJECT_SCHEMA = DATABASE()
ORDER BY COUNT_FETCH DESC;

-- 7. 应用层缓存建议
/*
应用层性能优化建议：

1. Redis缓存策略：
   - 缓存活跃会议室列表（TTL: 1小时）
   - 缓存用户信息（TTL: 30分钟）
   - 缓存会议室详情（TTL: 1小时）

2. 数据库连接池配置：
   - 初始连接数：5
   - 最大连接数：20
   - 连接超时：30秒
   - 空闲超时：10分钟

3. 查询优化：
   - 使用分页查询避免大结果集
   - 合理使用索引覆盖查询
   - 避免SELECT *，只查询需要的字段
   - 使用批量操作减少数据库交互

4. 前端优化：
   - 实现前端缓存和本地存储
   - 使用防抖和节流优化搜索
   - 实现虚拟滚动处理大列表
   - 使用CDN加速静态资源
*/