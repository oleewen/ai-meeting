# 会议室预约系统设计文档

## 概述

会议室预约系统是一个基于Spring Boot的Web应用程序，采用领域驱动设计(DDD)架构模式。系统提供RESTful API接口供前端调用，支持会议室查询、预约管理、签到功能等核心业务需求。

## 架构

### 整体架构
系统采用分层架构设计，遵循现有项目的多模块结构：

```
ai-meeting-boot (启动模块)
├── ai-meeting-api (API接口层)
├── ai-meeting-application (应用服务层)  
├── ai-meeting-domain (领域模型层)
├── ai-meeting-service (业务服务层)
├── ai-meeting-infrastructure (基础设施层)
├── ai-meeting-client (客户端模块)
└── ai-meeting-common (公共模块)
```

### 技术栈
- **后端框架**: Spring Boot 2.x
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis
- **前端技术**: HTML5 + CSS3 + JavaScript (原生或Vue.js)
- **构建工具**: Maven
- **应用服务器**: 内嵌Tomcat

## 组件和接口

### 领域模型 (Domain Layer)

#### 核心实体

**MeetingRoom (会议室)**
```java
public class MeetingRoom {
    private MeetingRoomId id;
    private String name;
    private String location;
    private int capacity;
    private Set<Equipment> equipments;
    private boolean active;
}
```

**Booking (预约)**
```java
public class Booking {
    private BookingId id;
    private MeetingRoomId meetingRoomId;
    private UserId userId;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeeCount;
    private String notes;
    private BookingStatus status;
    private LocalDateTime checkedInAt;
}
```

**User (用户)**
```java
public class User {
    private UserId id;
    private String username;
    private String email;
    private String displayName;
    private UserRole role;
}
```

#### 值对象

**TimeSlot (时间段)**
```java
public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public boolean overlaps(TimeSlot other);
    public Duration getDuration();
}
```

**Equipment (设备)**
```java
public enum Equipment {
    PROJECTOR, VIDEO_CONFERENCE, WHITEBOARD, LARGE_SCREEN
}
```

#### 领域服务

**BookingDomainService**
- 检查预约时间冲突
- 验证预约业务规则
- 计算可用时间段

### 应用服务层 (Application Layer)

**MeetingRoomApplicationService**
- 查询会议室可用性
- 获取会议室详细信息

**BookingApplicationService**  
- 创建预约
- 取消预约
- 签到操作
- 查询用户预约

**UserApplicationService**
- 用户认证
- 权限验证

### API接口层 (API Layer)

**REST API 端点**

```
GET /api/meeting-rooms/search
POST /api/bookings
DELETE /api/bookings/{id}
POST /api/bookings/{id}/checkin
GET /api/bookings/my-bookings
POST /api/auth/login
```

### 基础设施层 (Infrastructure Layer)

**Repository实现**
- MeetingRoomRepositoryImpl (MyBatis)
- BookingRepositoryImpl (MyBatis)  
- UserRepositoryImpl (MyBatis)

**外部服务**
- EmailNotificationService (可选)
- LdapAuthenticationService (可选)

## 数据模型

### 数据库表设计

**meeting_rooms 表**
```sql
CREATE TABLE meeting_rooms (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    equipments JSON,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**bookings 表**
```sql
CREATE TABLE bookings (
    id VARCHAR(36) PRIMARY KEY,
    meeting_room_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    attendee_count INT,
    notes TEXT,
    status ENUM('ACTIVE', 'CANCELLED', 'CHECKED_IN') DEFAULT 'ACTIVE',
    checked_in_at DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (meeting_room_id) REFERENCES meeting_rooms(id),
    INDEX idx_meeting_room_time (meeting_room_id, start_time, end_time),
    INDEX idx_user_bookings (user_id, start_time)
);
```

**users 表**
```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    email VARCHAR(100),
    display_name VARCHAR(100),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 正确性属性

*属性是指在系统的所有有效执行中都应该成立的特征或行为——本质上是关于系统应该做什么的正式声明。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*

基于需求分析，以下是系统必须满足的关键正确性属性：

**属性 1: 可用会议室查询准确性**
*对于任何*日期和时间段查询，返回的会议室列表应该只包含在该时间段内真正可用（未被预约）的会议室
**验证: 需求 1.1**

**属性 2: 筛选条件一致性**
*对于任何*筛选条件组合（地点、容量、设备），返回的会议室应该满足所有指定的筛选条件
**验证: 需求 1.2**

**属性 3: 会议室信息完整性**
*对于任何*会议室显示请求，返回的信息应该包含名称、地点、容量和设备等所有必需字段
**验证: 需求 1.3**

**属性 4: 时间轴排期准确性**
*对于任何*会议室的当日时间轴查询，显示的预约信息应该包含该会议室当日的所有有效预约
**验证: 需求 1.4**

**属性 5: 排序功能正确性**
*对于任何*会议室列表和排序条件，排序后的结果应该按照指定条件（容量或位置）正确排列
**验证: 需求 1.5**

**属性 6: 预约创建成功性**
*对于任何*有效的预约请求（无时间冲突），系统应该成功创建预约记录并返回确认
**验证: 需求 2.1**

**属性 7: 时间冲突检测**
*对于任何*与现有预约时间重叠的新预约请求，系统应该拒绝预约并返回冲突信息
**验证: 需求 2.2**

**属性 8: 预约数据持久化**
*对于任何*成功创建的预约，系统应该正确保存所有输入信息（主题、参会人数等）并能准确检索
**验证: 需求 2.3**

**属性 9: 业务规则验证**
*对于任何*违反业务规则的预约请求（超出时间限制、不符合时长要求等），系统应该拒绝并返回具体错误信息
**验证: 需求 2.4**

**属性 10: 预约列表一致性**
*对于任何*成功创建的预约，该预约应该出现在创建用户的个人预约列表中
**验证: 需求 2.5**

**属性 11: 取消确认信息准确性**
*对于任何*取消预约请求，确认对话框应该包含正确的会议详细信息
**验证: 需求 3.1**

**属性 12: 预约取消状态更新**
*对于任何*确认的预约取消操作，预约状态应该更改为已取消，且会议室时间段应该被释放
**验证: 需求 3.2**

**属性 13: 取消时间限制规则**
*对于任何*预约取消请求，系统应该根据配置的时间限制规则决定是否允许取消
**验证: 需求 3.3**

**属性 14: 取消权限验证**
*对于任何*预约取消请求，只有预约创建者应该能够取消该预约，其他用户应该被拒绝
**验证: 需求 3.4**

**属性 15: 取消后列表状态同步**
*对于任何*成功取消的预约，用户的个人预约列表应该反映正确的取消状态
**验证: 需求 3.5**

**属性 16: 签到时间窗口验证**
*对于任何*签到请求，只有在允许的时间窗口内（会议开始前后的指定时间范围）才应该成功
**验证: 需求 4.1**

**属性 17: 签到时间限制**
*对于任何*在允许时间窗口外的签到尝试，系统应该拒绝并返回时间限制信息
**验证: 需求 4.2**

**属性 18: 签到权限控制**
*对于任何*签到请求，只有预约创建者或授权用户应该能够签到，其他用户应该被拒绝
**验证: 需求 4.3**

**属性 19: 签到时间记录**
*对于任何*成功的签到操作，系统应该在预约记录中准确记录签到时间
**验证: 需求 4.4**

**属性 20: 签到状态显示**
*对于任何*已签到的预约，查询时应该显示正确的签到状态和时间信息
**验证: 需求 4.5**

**属性 21: 个人预约时间排序**
*对于任何*用户的个人预约查询，返回的预约列表应该按时间正确排序
**验证: 需求 5.1**

**属性 22: 日期范围筛选准确性**
*对于任何*日期范围筛选条件，返回的预约应该只包含指定时间范围内的记录
**验证: 需求 5.2**

**属性 23: 状态筛选准确性**
*对于任何*状态筛选条件，返回的预约应该只包含匹配指定状态的记录
**验证: 需求 5.3**

**属性 24: 预约列表信息完整性**
*对于任何*预约列表查询，每条预约应该显示主题、会议室、时间和状态等所有必需信息
**验证: 需求 5.4**

**属性 25: 预约详情完整性**
*对于任何*预约详情查询，应该显示完整的预约信息和当前可用的操作选项
**验证: 需求 5.5**

**属性 26: 数据一致性保护**
*对于任何*关键操作中的网络中断或系统故障，系统应该防止产生重复或不一致的数据状态
**验证: 需求 6.5**

**属性 27: 权限访问控制**
*对于任何*预约数据访问请求，系统应该验证用户权限并拒绝未授权访问
**验证: 需求 7.2**

**属性 28: 操作权限验证**
*对于任何*数据修改操作，系统应该验证用户对该数据的操作权限
**验证: 需求 7.3**

**属性 29: 安全错误处理**
*对于任何*系统错误，返回的错误信息应该清晰但不暴露敏感数据
**验证: 需求 7.4**

**属性 30: 操作失败数据完整性**
*对于任何*失败的操作，现有数据的完整性应该不受影响
**验证: 需求 7.5**

## 错误处理

### 业务异常处理
- **预约冲突异常**: 当检测到时间冲突时，返回具体的冲突时间信息
- **权限异常**: 当用户尝试未授权操作时，返回权限错误信息
- **业务规则违反异常**: 当违反预约规则时，返回具体的规则说明
- **资源不存在异常**: 当访问不存在的会议室或预约时，返回404错误

### 系统异常处理
- **数据库连接异常**: 实现连接池和重试机制
- **网络超时异常**: 设置合理的超时时间和降级策略
- **并发冲突异常**: 使用乐观锁处理并发更新冲突
- **系统资源异常**: 监控系统资源使用情况，实现熔断机制

### 错误响应格式
```json
{
    "success": false,
    "errorCode": "BOOKING_CONFLICT",
    "message": "会议室在该时间段已被预约",
    "details": {
        "conflictTime": "2024-01-15 14:00-16:00",
        "conflictBooking": "技术评审会议"
    },
    "timestamp": "2024-01-15T10:30:00Z"
}
```

## 测试策略

### 双重测试方法

系统将采用单元测试和基于属性的测试相结合的综合测试策略：

**单元测试**用于验证：
- 特定的业务场景和边界条件
- 组件间的集成点
- 具体的错误处理流程
- API接口的输入输出格式

**基于属性的测试**用于验证：
- 上述30个正确性属性的通用性
- 系统在各种随机输入下的行为一致性
- 业务规则在大量数据组合下的正确性

### 基于属性的测试配置

**测试框架**: 使用JUnit 5 + jqwik进行基于属性的测试
**测试配置**: 每个属性测试运行最少100次迭代以确保充分的随机性覆盖
**测试标记**: 每个基于属性的测试必须使用以下格式标记：
`**Feature: meeting-room-booking, Property {number}: {property_text}**`

### 测试数据生成策略

**智能生成器设计**:
- 时间段生成器：生成合理的会议时间范围（工作时间内，合理时长）
- 会议室生成器：生成具有不同容量和设备配置的会议室
- 用户生成器：生成具有不同权限级别的用户
- 预约生成器：生成符合业务规则的预约数据

**边界条件覆盖**:
- 时间边界：工作日边界、节假日、跨天会议
- 容量边界：最小/最大容量限制
- 权限边界：不同用户角色的操作权限
- 并发边界：同时操作相同资源的场景

### 集成测试

**数据库集成测试**:
- 使用H2内存数据库进行快速测试
- 验证MyBatis映射和SQL查询的正确性
- 测试事务边界和数据一致性

**API集成测试**:
- 使用MockMvc测试REST API端点
- 验证请求/响应格式和HTTP状态码
- 测试认证和授权流程

**端到端测试**:
- 模拟完整的用户操作流程
- 验证前后端集成的正确性
- 测试关键业务场景的完整性