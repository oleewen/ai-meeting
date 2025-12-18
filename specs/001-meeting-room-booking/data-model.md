# 数据模型设计

**功能**: 会议室预约系统  
**日期**: 2025-01-27

## 实体关系图

```
用户 (User)
  │
  │ 1:N
  │
  ▼
预约 (Booking) ──── N:1 ──── 会议室 (MeetingRoom)
```

## 实体定义

### 1. 会议室 (MeetingRoom)

**领域模型**: `com.only.ai.meeting.domain.model.MeetingRoom`

**属性**:
- `id` (Long): 会议室ID，主键
- `name` (String): 会议室名称，必填，最大长度100
- `location` (String): 地点/楼层，必填，最大长度100
- `capacity` (Integer): 可容纳人数，必填，最小值1
- `equipment` (String): 支持设备（JSON格式存储，如：["投影仪","视频会议","白板"]），可选
- `status` (String): 可用状态（AVAILABLE/UNAVAILABLE），默认AVAILABLE
- `description` (String): 描述信息，可选，最大长度500
- `createTime` (LocalDateTime): 创建时间
- `updateTime` (LocalDateTime): 更新时间

**验证规则**:
- 名称不能为空，长度1-100
- 地点不能为空，长度1-100
- 容量必须大于0
- 状态必须是AVAILABLE或UNAVAILABLE

**数据表**: `meeting_room`
```sql
CREATE TABLE meeting_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    equipment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_location (location),
    INDEX idx_status (status)
);
```

### 2. 预约 (Booking)

**领域模型**: `com.only.ai.meeting.domain.model.Booking`

**属性**:
- `id` (Long): 预约ID，主键
- `userId` (String): 预约人用户ID，必填
- `userName` (String): 预约人姓名，必填，最大长度50
- `roomId` (Long): 会议室ID，必填，外键关联meeting_room.id
- `roomName` (String): 会议室名称（冗余字段，便于查询），必填
- `date` (LocalDate): 预约日期，必填
- `startTime` (LocalTime): 开始时间，必填
- `endTime` (LocalTime): 结束时间，必填
- `subject` (String): 会议主题，必填，最大长度200
- `attendeeCount` (Integer): 参会人数，必填，最小值1
- `remark` (String): 备注，可选，最大长度500
- `status` (String): 状态（PENDING/SIGNED_IN/COMPLETED/CANCELLED/NOT_SIGNED_IN），默认PENDING
- `signInTime` (LocalDateTime): 签到时间，可选
- `createTime` (LocalDateTime): 创建时间
- `updateTime` (LocalDateTime): 更新时间

**状态转换**:
- PENDING（待开始） → SIGNED_IN（已签到）：用户在签到时间窗口内（会议开始前10分钟至开始后15分钟）进行签到
- PENDING（待开始） → CANCELLED（已取消）：用户主动取消预约
- PENDING（待开始） → COMPLETED（已结束）：会议结束时间后，如果用户已签到，自动转换为COMPLETED
- SIGNED_IN（已签到） → COMPLETED（已结束）：会议结束时间后自动转换
- PENDING（待开始） → NOT_SIGNED_IN（未签到）：会议结束时间后，如果状态仍为PENDING（用户未在签到时间窗口内签到），自动转换为NOT_SIGNED_IN

**验证规则**:
- 用户ID不能为空
- 会议室ID必须存在
- 日期不能是过去日期
- 开始时间必须早于结束时间
- 会议时长必须在30分钟到4小时之间
- 时间段不能与已有预约冲突（唯一约束：userId + roomId + date + startTime + endTime）
- 会议主题不能为空，长度1-200
- 参会人数必须大于0且不超过会议室容量

**数据表**: `booking`
```sql
CREATE TABLE booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    room_id BIGINT NOT NULL,
    room_name VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject VARCHAR(200) NOT NULL,
    attendee_count INT NOT NULL CHECK (attendee_count > 0),
    remark VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sign_in_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES meeting_room(id),
    INDEX idx_user_id (user_id),
    INDEX idx_room_id (room_id),
    INDEX idx_date (date),
    INDEX idx_status (status),
    INDEX idx_date_time (date, start_time, end_time),
    UNIQUE KEY uk_user_room_time (user_id, room_id, date, start_time, end_time)
);
```

### 3. 用户 (User)

**领域模型**: `com.only.ai.meeting.domain.model.User`

**说明**: 用户信息可能来自外部系统（如SSO、LDAP），本系统仅存储必要的用户信息用于权限控制和显示。

**属性**:
- `id` (String): 用户ID，主键
- `name` (String): 用户姓名，必填，最大长度50
- `email` (String): 邮箱，可选，最大长度100
- `department` (String): 部门，可选，最大长度100

**验证规则**:
- 用户ID不能为空
- 用户姓名不能为空，长度1-50

**数据表**: `user`（可选，如果用户信息需要本地存储）
```sql
CREATE TABLE user (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(100),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 枚举类型

### BookingStatus（预约状态）
```java
public enum BookingStatus {
    PENDING("待开始"),
    SIGNED_IN("已签到"),
    COMPLETED("已结束"),
    CANCELLED("已取消"),
    NOT_SIGNED_IN("未签到");
}
```

### RoomStatus（会议室状态）
```java
public enum RoomStatus {
    AVAILABLE("可用"),
    UNAVAILABLE("不可用");
}
```

## 数据访问接口

### MeetingRoomRepository
```java
public interface MeetingRoomRepository {
    MeetingRoom findById(Long id);
    List<MeetingRoom> findAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime);
    List<MeetingRoom> findByLocation(String location);
    void save(MeetingRoom room);
}
```

### BookingRepository
```java
public interface BookingRepository {
    Booking findById(Long id);
    List<Booking> findByUserId(String userId);
    List<Booking> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);
    List<Booking> findByRoomIdAndDate(Long roomId, LocalDate date);
    boolean existsConflict(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeBookingId);
    void save(Booking booking);
    void updateStatus(Long id, BookingStatus status);
    List<Booking> findCompletedBookings(LocalDateTime beforeTime);
}
```

## 数据初始化

### 初始会议室数据（data.sql）
```sql
INSERT INTO meeting_room (name, location, capacity, equipment, status, description) VALUES
('会议室A', '1楼', 10, '["投影仪","白板"]', 'AVAILABLE', '小型会议室，适合小组讨论'),
('会议室B', '1楼', 20, '["投影仪","视频会议","白板"]', 'AVAILABLE', '中型会议室，支持远程会议'),
('会议室C', '2楼', 30, '["投影仪","视频会议","白板","音响"]', 'AVAILABLE', '大型会议室，适合全体会议'),
('会议室D', '2楼', 8, '["投影仪"]', 'AVAILABLE', '小型会议室'),
('会议室E', '3楼', 15, '["投影仪","白板"]', 'AVAILABLE', '中型会议室');
```
