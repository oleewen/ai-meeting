# AI Meeting Speckit - Agent 工程文档

**版本**: 1.0.0  
**最后更新**: 2025-01-27  
**目的**: 本文档帮助 AI Agent 快速理解工程的功能需求、技术设计、测试场景和开发规范

---

## 📋 目录

1. [工程概述](#工程概述)
2. [技术栈](#技术栈)
3. [架构设计](#架构设计)
4. [功能需求](#功能需求)
5. [测试场景](#测试场景)
6. [API 接口](#api-接口)
7. [数据模型](#数据模型)
8. [开发规范](#开发规范)
9. [快速开始](#快速开始)
10. [项目结构](#项目结构)

---

## 工程概述

**AI Meeting Speckit** 是一个会议室预约管理系统，支持员工查询可用会议室、创建预约、取消预约、会议签到、查看个人预约记录等功能。

### 核心功能

- ✅ **会议室查询**: 根据日期、时间段、地点、容量、设备等条件查询可用会议室
- ✅ **预约管理**: 创建、取消会议室预约，支持时间段冲突检测
- ✅ **签到功能**: 会议开始前后签到确认
- ✅ **我的预定**: 查看和管理个人预约记录
- ✅ **权限控制**: 用户只能查看和操作自己的预约

### 业务规则

- **预约时间范围**: 仅允许预约未来30天内的会议室，不允许预约过去或已开始的时间段
- **会议时长限制**: 最短30分钟，最长4小时
- **取消时间限制**: 距离会议开始时间少于5分钟时不允许取消
- **签到时间窗口**: 会议开始前10分钟至开始后15分钟
- **并发处理**: 采用先到先得策略，使用乐观锁和唯一约束防止冲突
- **幂等性**: 通过用户+会议室+时间段的唯一性约束和请求去重机制保证

---

## 技术栈

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 2.7.10 | 应用框架 |
| MyBatis | 2.3.2 | ORM框架 |
| H2 Database | 2.1.214 | 内存数据库（开发/测试） |
| Maven | 3.6+ | 构建工具 |
| Lombok | 1.18.34 | 代码生成 |
| MapStruct | 1.5.0.Final | 对象映射 |
| Druid | 1.2.23 | 数据库连接池 |
| SpringDoc OpenAPI | 1.6.9 | API文档生成 |
| Jackson | 2.13.5 | JSON序列化（snake_case） |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| TypeScript | 4.9.5 | 编程语言 |
| React | 19.2.3 | UI框架 |
| Ant Design | 6.1.1 | UI组件库 |
| React Router | 7.11.0 | 路由管理 |
| Axios | 1.13.2 | HTTP客户端 |
| dayjs | 1.11.19 | 日期处理 |
| React Scripts | 5.0.1 | 构建工具 |

### 测试框架

- **后端**: JUnit 5、Mockito、Spring Boot Test
- **前端**: React Testing Library、Jest

---

## 架构设计

### 分层架构

项目采用**六边形架构（Hexagonal Architecture）**/**洋葱架构（Onion Architecture）**/**整洁架构（Clean Architecture）**模式，严格遵循分层依赖规则：

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (React)                     │
│              ai-meeting-frontend/                       │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP/REST API
┌──────────────────────▼──────────────────────────────────┐
│                    Service Layer                        │
│              ai-meeting-service/                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │  web/controller  │  rpc/  │  job/  │  message/   │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                Application Layer                        │
│          ai-meeting-application/                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │  service/  │  action/  │  command/  │  query/    │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                  Domain Layer                           │
│            ai-meeting-domain/                           │
│  ┌──────────────────────────────────────────────────┐   │
│  │  model/  │  service/  │  repository/  │  event/  │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│            Infrastructure Layer                         │
│        ai-meeting-infrastructure/                       │
│  ┌──────────────────────────────────────────────────┐   │
│  │  dao/  │  entity/  │  mapper/  │  config/        │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### 模块依赖关系

```
client ──┐
         │
service ─┼── api ──┐
         │         │
boot ────┼─────────┼─── application ──┐
         │         │                  │
         └─────────┴──────────────────┼── domain
                                      │
                                      └── infrastructure
```

### 模块职责

#### 1. **domain** - 领域服务层
- **职责**: 领域模型、领域服务、资源库接口、领域事件、查询门面
- **包结构**: `com.only.ai.meeting.domain`
  - `model/`: 领域对象（MeetingRoom、Booking、User）
  - `service/`: 领域服务
  - `facade/`: 查询门面
  - `repository/`: 资源库接口
  - `event/`: 领域事件
- **依赖**: 不依赖任何基础设施层

#### 2. **application** - 应用服务层
- **职责**: 面向用例或用户故事，实现处理流程和节点
- **包结构**: `com.only.ai.meeting.application`
  - `service/`: 应用服务（BookingService、MeetingRoomQueryService）
  - `action/`: 处理节点
  - `command/`: 命令对象（CreateBookingCommand）
  - `query/`: 查询对象（RoomQuery）
  - `result/`: 结果对象（RoomQueryResult）
- **依赖**: 仅依赖 domain 层

#### 3. **infrastructure** - 资源层
- **职责**: 实现数据访问、外部服务调用
- **包结构**: `com.only.ai.meeting.infrastructure`
  - `dao/`: 数据访问对象
  - `entity/`: 数据实体（DO）
  - `mapper/`: MyBatis Mapper
  - `config/`: 数据库配置
  - `factory/`: 实体转换工厂
- **依赖**: 实现 domain 层定义的接口

#### 4. **api** - 公共 API 包
- **职责**: 公共接口定义、DTO、常量、枚举
- **包结构**: `com.only.ai.meeting.api`
  - `request/`: 请求对象（CreateBookingRequest）
  - `response/`: 响应对象（BookingDTO、MeetingRoomDTO）
  - `dto/`: 数据传输对象
- **依赖**: 可被 service 层和 client 层依赖

#### 5. **service** - 表现层
- **职责**: HTTP 控制器、RPC 服务实现、定时任务
- **包结构**: `com.only.ai.meeting`
  - `web/controller/`: REST 控制器（BookingController、MeetingRoomController）
  - `web/request/`: Web 请求对象
  - `web/response/`: Web 响应对象
  - `web/config/`: Web 配置（CORS、Swagger）
  - `rpc/`: RPC 服务实现
  - `job/`: 定时任务
  - `message/`: 消息处理
- **依赖**: 依赖 application 和 infrastructure 层

#### 6. **client** - 富客户端
- **职责**: 实现富客户端
- **包结构**: `com.only.ai.meeting.{Aggregate}Client`
- **依赖**: 依赖 api 层

#### 7. **boot** - 启动模块
- **职责**: 应用启动入口，组装各层组件
- **主类**: `com.only.ai.boot.ApplicationStarter`
- **配置**: `application.properties`、`application-*.properties`

#### 8. **common** - 通用模块
- **职责**: 通用领域对象和工具类
- **包结构**: `com.only.ai.common`
- **依赖**: 可被 domain 层和其他层依赖

#### 9. **frontend** - 前端应用
- **职责**: React 前端应用，通过 HTTP API 与后端通信
- **结构**: 
  - `src/components/`: React 组件
  - `src/pages/`: 页面组件
  - `src/services/`: API 服务
  - `src/utils/`: 工具函数
- **独立部署**: 前后端分离，独立运行

---

## 功能需求

### 用户故事 1: 查询可用会议室 (P1)

**作为** 普通员工  
**我希望** 能够查询在指定时间段内可用的会议室  
**以便** 快速找到适合我会议需求的会议室

**验收场景**:
1. 用户输入日期和时间段，系统显示该时间段内可用的会议室列表
2. 用户应用筛选条件（地点、容量、设备），系统只显示符合筛选条件的会议室
3. 该时间段内所有会议室都被占用时，系统显示"暂无可用会议室"
4. 用户选择某个会议室，系统显示该会议室当日的完整时间轴排期

### 用户故事 2: 预约会议室 (P1)

**作为** 普通员工  
**我希望** 能够预约会议室  
**以便** 确保我的会议有固定的场所

**验收场景**:
1. 用户选择会议室并填写会议信息，系统创建预约并显示"预约成功"
2. 时间段与已有预约冲突时，系统拒绝预约并提示冲突信息
3. 预约时间不在允许范围内时，系统拒绝预约并提示允许的时间范围
4. 会议时长不符合要求时，系统拒绝预约并提示时长限制
5. 用户成功预约后，新预约出现在"我的预定"列表中
6. 多个用户同时尝试预约同一时间段时，第一个用户成功，其他用户收到"已被预约"提示

### 用户故事 3: 查看我的预定 (P1)

**作为** 普通员工  
**我希望** 能够查看我所有的会议室预约记录  
**以便** 管理我的会议安排

**验收场景**:
1. 用户访问"我的预定"页面，系统显示用户的所有预约记录，按时间排序
2. 用户选择日期范围筛选，系统只显示该日期范围内的预约
3. 用户选择状态筛选，系统只显示对应状态的预约
4. 用户点击某个预约，系统显示该预约的详细信息

### 用户故事 4: 取消预约 (P2)

**作为** 普通员工  
**我希望** 能够取消我创建的预约  
**以便** 在计划变更时释放会议室资源

**验收场景**:
1. 用户选择取消预约并确认，系统取消预约，状态变更为"已取消"
2. 距离会议开始时间太近时，系统拒绝取消并提示不允许取消的原因
3. 用户确认取消操作，系统显示取消确认信息

### 用户故事 5: 会议签到 (P2)

**作为** 普通员工  
**我希望** 能够在会议开始前或开始时签到  
**以便** 确认会议按计划使用

**验收场景**:
1. 用户在签到时间窗口内点击签到按钮，系统标记预约状态为"已签到"
2. 当前时间不在签到时间窗口内时，系统不显示签到按钮或按钮置灰
3. 用户已签到后，系统显示"已签到"状态

### 功能需求列表

| ID | 需求 | 优先级 |
|----|------|--------|
| FR-001 | 系统必须允许用户通过日期和时间段查询可用会议室 | P1 |
| FR-002 | 系统必须支持按地点、容量、设备等条件筛选会议室 | P1 |
| FR-003 | 系统必须显示会议室的详细信息 | P1 |
| FR-004 | 系统必须允许用户创建会议室预约 | P1 |
| FR-005 | 系统必须防止时间段冲突，拒绝与已有预约重叠的新预约 | P1 |
| FR-006 | 系统必须限制预约时间范围（只允许未来30天内预约） | P1 |
| FR-007 | 系统必须限制会议时长（最短30分钟，最长4小时） | P1 |
| FR-008 | 系统必须限制预约修改时间（会议开始前5分钟不可新预约或修改） | P1 |
| FR-009 | 系统必须允许用户查看自己的所有预约记录 | P1 |
| FR-010 | 系统必须支持按日期范围和状态筛选预约记录 | P1 |
| FR-011 | 系统必须显示预约的详细信息 | P1 |
| FR-012 | 系统必须允许用户取消自己创建的预约 | P2 |
| FR-013 | 系统必须限制取消时间（距离会议开始时间太近时不允许取消） | P2 |
| FR-014 | 系统必须允许用户在签到时间窗口内进行签到 | P2 |
| FR-015 | 系统必须记录签到状态，标记预约为"已签到" | P2 |
| FR-016 | 系统必须要求用户登录后才能使用所有功能 | P1 |
| FR-017 | 系统必须确保用户只能查看和操作自己的预约 | P1 |
| FR-018 | 系统必须支持PC浏览器访问 | P1 |
| FR-019 | 系统必须适配常见办公环境分辨率（≥ 1366×768） | P1 |
| FR-020 | 系统必须采用先到先得策略处理并发预约冲突 | P1 |
| FR-021 | 系统必须防止重复预约（幂等性检查） | P1 |
| FR-022 | 系统必须在会议结束时间后自动将预约状态标记为"已结束" | P1 |
| FR-023 | 系统必须统一使用服务器系统时间进行所有时间判断 | P1 |

---

## 测试场景

### 单元测试

- **领域模型测试**: 验证 MeetingRoom、Booking 等领域的业务规则
- **领域服务测试**: 验证预约冲突检测、状态转换等业务逻辑
- **应用服务测试**: 验证用例处理流程
- **数据访问测试**: 验证 Repository 实现

### 集成测试

- **API 集成测试**: 验证 REST API 端到端流程
- **数据库集成测试**: 验证数据持久化和查询
- **并发测试**: 验证并发预约冲突处理
- **契约测试**: 验证 API 契约符合 OpenAPI 规范

### 前端测试

- **组件测试**: 验证 React 组件渲染和交互
- **页面测试**: 验证页面流程和用户交互
- **API 服务测试**: 验证 API 调用和错误处理

### 关键测试场景

1. **并发预约冲突**: 多个用户同时尝试预约同一时间段
2. **时间段冲突检测**: 新预约与已有预约时间重叠
3. **时间范围验证**: 不允许预约过去或已开始的时间段
4. **会议时长验证**: 验证最短30分钟、最长4小时限制
5. **取消时间限制**: 距离会议开始时间少于5分钟不允许取消
6. **签到时间窗口**: 会议开始前10分钟至开始后15分钟
7. **权限控制**: 用户只能查看和操作自己的预约
8. **幂等性**: 防止重复预约（相同用户+会议室+时间段）

---

## API 接口

### 基础信息

- **Base URL**: `http://localhost:8082/api`
- **API 文档**: `http://localhost:8082/swagger-ui.html`
- **数据格式**: JSON（字段命名采用 snake_case）
- **认证方式**: MVP 阶段使用简化版认证（通过 Header 或 Session 传递用户ID）

### 会议室相关接口

#### 1. 查询可用会议室

```
GET /api/meeting-rooms
```

**请求参数**:
- `date` (required): 预约日期，格式：yyyy-MM-dd
- `startTime` (required): 开始时间，格式：HH:mm
- `endTime` (required): 结束时间，格式：HH:mm
- `location` (optional): 地点筛选
- `minCapacity` (optional): 最小容量
- `equipment` (optional): 设备筛选（多个设备用逗号分隔）

**响应示例**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "name": "会议室A",
      "location": "1楼",
      "capacity": 10,
      "equipment": ["投影仪", "白板"],
      "status": "AVAILABLE",
      "description": "小型会议室，适合小组讨论"
    }
  ]
}
```

#### 2. 查看会议室当日排期

```
GET /api/meeting-rooms/{roomId}/schedule?date=2025-01-27
```

**响应示例**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "room_id": 1,
    "room_name": "会议室A",
    "date": "2025-01-27",
    "time_slots": [
      {
        "start_time": "09:00",
        "end_time": "10:00",
        "status": "OCCUPIED",
        "booking_id": 1,
        "subject": "项目评审会议"
      },
      {
        "start_time": "10:00",
        "end_time": "11:00",
        "status": "AVAILABLE",
        "booking_id": null,
        "subject": null
      }
    ]
  }
}
```

### 预约相关接口

#### 3. 创建预约

```
POST /api/bookings
```

**请求体**:
```json
{
  "room_id": 1,
  "date": "2025-01-27",
  "start_time": "09:00",
  "end_time": "10:00",
  "subject": "项目评审会议",
  "attendee_count": 8,
  "remark": "需要投影仪"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "预约成功",
  "data": {
    "id": 1,
    "user_id": "user001",
    "user_name": "测试用户",
    "room_id": 1,
    "room_name": "会议室A",
    "date": "2025-01-27",
    "start_time": "09:00",
    "end_time": "10:00",
    "subject": "项目评审会议",
    "attendee_count": 8,
    "remark": "需要投影仪",
    "status": "PENDING",
    "create_time": "2025-01-27 08:00:00"
  }
}
```

#### 4. 查看我的预定

```
GET /api/bookings?startDate=2025-01-01&endDate=2025-01-31&status=PENDING
```

**请求参数**:
- `startDate` (optional): 开始日期
- `endDate` (optional): 结束日期
- `status` (optional): 状态筛选（PENDING/SIGNED_IN/COMPLETED/CANCELLED/NOT_SIGNED_IN）

#### 5. 查看预约详情

```
GET /api/bookings/{bookingId}
```

#### 6. 取消预约

```
DELETE /api/bookings/{bookingId}
```

#### 7. 会议签到

```
POST /api/bookings/{bookingId}/sign-in
```

---

## 数据模型

### 实体关系图

```
用户 (User)
  │
  │ 1:N
  │
  ▼
预约 (Booking) ──── N:1 ──── 会议室 (MeetingRoom)
```

### 核心实体

#### 1. 会议室 (MeetingRoom)

**领域模型**: `com.only.ai.meeting.domain.model.MeetingRoom`

**属性**:
- `id` (Long): 会议室ID，主键
- `name` (String): 会议室名称，必填，最大长度100
- `location` (String): 地点/楼层，必填，最大长度100
- `capacity` (Integer): 可容纳人数，必填，最小值1
- `equipment` (String): 支持设备（JSON格式存储），可选
- `status` (String): 可用状态（AVAILABLE/UNAVAILABLE），默认AVAILABLE
- `description` (String): 描述信息，可选，最大长度500
- `createTime` (LocalDateTime): 创建时间
- `updateTime` (LocalDateTime): 更新时间

**数据表**: `meeting_room`

#### 2. 预约 (Booking)

**领域模型**: `com.only.ai.meeting.domain.model.Booking`

**属性**:
- `id` (Long): 预约ID，主键
- `userId` (String): 预约人用户ID，必填
- `userName` (String): 预约人姓名，必填，最大长度50
- `roomId` (Long): 会议室ID，必填，外键关联meeting_room.id
- `roomName` (String): 会议室名称（冗余字段），必填
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
- PENDING → SIGNED_IN: 用户在签到时间窗口内进行签到
- PENDING → CANCELLED: 用户主动取消预约
- PENDING → COMPLETED: 会议结束时间后，如果用户已签到，自动转换为COMPLETED
- SIGNED_IN → COMPLETED: 会议结束时间后自动转换
- PENDING → NOT_SIGNED_IN: 会议结束时间后，如果状态仍为PENDING，自动转换为NOT_SIGNED_IN

**唯一约束**: `uk_user_room_time` (user_id, room_id, date, start_time, end_time)

**数据表**: `booking`

#### 3. 用户 (User)

**领域模型**: `com.only.ai.meeting.domain.model.User`

**属性**:
- `id` (String): 用户ID，主键
- `name` (String): 用户姓名，必填，最大长度50
- `email` (String): 邮箱，可选，最大长度100
- `department` (String): 部门，可选，最大长度100

**数据表**: `user`（可选，如果用户信息需要本地存储）

### 枚举类型

#### BookingStatus（预约状态）
```java
public enum BookingStatus {
    PENDING("待开始"),
    SIGNED_IN("已签到"),
    COMPLETED("已结束"),
    CANCELLED("已取消"),
    NOT_SIGNED_IN("未签到");
}
```

#### RoomStatus（会议室状态）
```java
public enum RoomStatus {
    AVAILABLE("可用"),
    UNAVAILABLE("不可用");
}
```

---

## 开发规范

### 代码规范

1. **语言要求**: 所有文档、注释、API描述使用中文
2. **编码规范**: UTF-8
3. **命名规范**: 
   - Java: 驼峰命名（类名大驼峰，方法名小驼峰）
   - 数据库: 下划线命名（snake_case）
   - JSON: 下划线命名（snake_case）
4. **注释要求**: 代码注释使用中文描述业务含义，技术实现细节可用英文术语

### 架构规范

1. **分层依赖**: 严格遵循分层依赖规则，禁止跨层依赖
2. **包结构**: 每个模块必须遵循统一的包结构规范
3. **依赖管理**: 使用Maven进行依赖管理，版本号统一在父POM中管理

### 测试规范

1. **测试优先**: 所有新功能必须遵循TDD流程
2. **测试覆盖率**: 代码覆盖率不低于70%（核心业务逻辑不低于80%）
3. **测试类型**: 单元测试、集成测试、契约测试

### 性能约束

1. **API响应时间**: P95延迟 < 200ms（除非业务场景特殊要求）
2. **查询响应时间**: 正常数据量下不超过1秒
3. **并发支持**: 支持至少50个用户同时查询和预约
4. **数据规模**: 支持至少1000个会议室和10000条预约记录

### 质量门禁

1. 所有测试必须通过
2. 代码覆盖率不低于70%
3. 静态代码检查必须通过
4. 必须提供或更新相关中文文档

---

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Node.js 16+ (前端开发)
- npm 或 yarn (前端包管理)

### 后端启动

1. **配置数据库**（H2内存数据库，无需额外配置）

2. **启动应用**:
```bash
cd ai-meeting-boot
mvn spring-boot:run
```

3. **验证启动**:
- 应用地址: http://localhost:8082
- H2控制台: http://localhost:8082/h2-console
- Swagger API文档: http://localhost:8082/swagger-ui.html

### 前端启动

1. **安装依赖**:
```bash
cd ai-meeting-frontend
npm install
```

2. **启动开发服务器**:
```bash
npm start
```

前端应用将在 http://localhost:3000 启动。

### 数据库初始化

数据库初始化脚本位于：
- `ai-meeting-infrastructure/src/main/resources/schema.sql` - 表结构
- `ai-meeting-infrastructure/src/main/resources/data.sql` - 初始数据

---

## 项目结构

### 后端代码结构

```
ai-meeting/
├── ai-meeting-domain/          # 领域服务层
│   └── src/main/java/com/only/ai/meeting/domain/
│       ├── service/             # 领域服务
│       ├── facade/             # 查询门面
│       ├── model/              # 领域模型
│       ├── event/              # 领域事件
│       └── repository/         # 资源库接口
│
├── ai-meeting-application/     # 应用服务层
│   └── src/main/java/com/only/ai/meeting/application/
│       ├── service/            # 应用服务
│       ├── action/              # 处理节点
│       ├── command/             # 命令对象
│       ├── query/               # 查询对象
│       └── result/              # 结果对象
│
├── ai-meeting-infrastructure/  # 资源层
│   └── src/main/java/com/only/ai/meeting/infrastructure/
│       ├── dao/                # 数据访问对象
│       ├── config/              # 数据库配置
│       ├── entity/              # 数据实体（DO）
│       ├── mapper/              # MyBatis Mapper
│       └── factory/             # 实体转换工厂
│
├── ai-meeting-api/             # 公共API包
│   └── src/main/java/com/only/ai/meeting/api/
│       ├── request/             # 请求DTO
│       ├── response/            # 响应DTO
│       └── dto/                 # 数据传输对象
│
├── ai-meeting-service/         # 表现层
│   └── src/main/java/com/only/ai/meeting/
│       └── web/
│           ├── controller/      # REST控制器
│           ├── request/          # Web请求对象
│           ├── response/         # Web响应对象
│           └── config/           # Web配置
│
├── ai-meeting-client/          # 富客户端
│   └── src/main/java/com/only/ai/meeting/
│       └── {Aggregate}Client/
│
├── ai-meeting-common/          # 通用模块
│   └── src/main/java/com/only/ai/common/
│
└── ai-meeting-boot/            # 启动模块
    └── src/main/
        ├── java/com/only/ai/boot/
        │   └── ApplicationStarter.java
        └── resources/
            ├── application.properties
            └── application-*.properties
```

### 前端代码结构

```
ai-meeting-frontend/
├── src/
│   ├── components/             # React组件
│   │   ├── MeetingRoomList/
│   │   ├── BookingForm/
│   │   ├── MyBookings/
│   │   └── common/
│   ├── pages/                  # 页面组件
│   │   ├── QueryPage/
│   │   ├── BookingPage/
│   │   └── MyBookingsPage/
│   ├── services/               # API服务
│   ├── utils/                  # 工具函数
│   └── App.tsx
├── public/
└── package.json
```

### 规范文档结构

```
specs/
└── 001-meeting-room-booking/
    ├── spec.md                 # 功能规格
    ├── plan.md                 # 实现计划
    ├── data-model.md           # 数据模型设计
    ├── quickstart.md           # 快速开始指南
    ├── research.md             # 研究文档
    ├── tasks.md                # 任务清单
    ├── contracts/
    │   └── openapi.yaml        # API契约定义
    └── checklists/
        └── checklist.md        # 检查清单
```

---

## 参考文档

- [工程结构介绍](./readme.md)
- [功能规格文档](./specs/001-meeting-room-booking/spec.md)
- [实现计划](./specs/001-meeting-room-booking/plan.md)
- [数据模型设计](./specs/001-meeting-room-booking/data-model.md)
- [快速开始指南](./specs/001-meeting-room-booking/quickstart.md)
- [API契约定义](./specs/001-meeting-room-booking/contracts/openapi.yaml)
- [项目规范](./.specify/memory/constitution.md)

---

**文档维护**: 本文档应随项目发展持续更新，确保信息准确性和完整性。

