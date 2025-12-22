# 会议室预约系统使用说明

## 系统概述

会议室预约系统是一个基于Spring Boot开发的Web应用，支持用户查询、预约、取消和签到会议室。

## 技术栈

- **后端**: Spring Boot 2.7.10, Java 17
- **数据库**: MySQL
- **ORM**: MyBatis
- **前端**: HTML + CSS + JavaScript (原生)
- **架构**: 六边形架构（领域驱动设计）

## 功能特性

### 已实现功能

1. **用户认证**
   - 用户登录（账号密码）
   - 会话管理

2. **会议室查询**
   - 按日期和时间段查询可用会议室
   - 支持地点、容量、设备筛选
   - 查看会议室详情和当日排期

3. **会议室预约**
   - 创建预约（含冲突检测）
   - 时间范围限制（最多提前30天）
   - 时长限制（30分钟~4小时）
   - 提前预约截止时间（开始前5分钟）

4. **预约管理**
   - 查看我的预定列表
   - 按日期范围和状态筛选
   - 取消预约（需符合时间限制）
   - 会议签到（开始前10分钟至开始后15分钟）

## 部署步骤

### 方式一：使用H2内存数据库（推荐用于开发测试）

系统已配置为使用H2内存数据库，无需额外配置，启动时自动创建表结构和初始数据。

**配置说明**：
- 已在 `application-dev.properties` 中配置H2数据库
- 启动时自动执行 `schema-h2.sql` 创建表结构
- 启动时自动执行 `data-h2.sql` 插入初始数据
- H2控制台地址：http://localhost:8082/h2-console
  - JDBC URL: `jdbc:h2:mem:meeting_room`
  - 用户名: `sa`
  - 密码: （空）

### 方式二：使用MySQL数据库（生产环境）

#### 1. 数据库准备

执行数据库脚本创建表结构：

```bash
# 连接MySQL数据库
mysql -u root -p

# 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS meeting_room CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用数据库
USE meeting_room;

# 执行表结构脚本
SOURCE ai-meeting-infrastructure/src/main/resources/db/schema.sql

# 执行初始数据脚本
SOURCE ai-meeting-infrastructure/src/main/resources/db/init-data.sql
```

或者直接执行SQL文件：

```bash
mysql -u root -p meeting_room < ai-meeting-infrastructure/src/main/resources/db/schema.sql
mysql -u root -p meeting_room < ai-meeting-infrastructure/src/main/resources/db/init-data.sql
```

#### 2. 配置数据库连接

编辑 `ai-meeting-boot/src/main/resources/application-dev.properties`（或其他环境配置文件），修改数据库配置：

```properties
# 使用MySQL数据库
spring.datasource.type=mysql
db.url=jdbc:mysql://localhost:3306/meeting_room?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
db.username=root
db.password=your_password
db.maxactive=20
db.minidle=5
```

**注意**: 如果使用其他环境（如test、pre、prod），请修改对应的配置文件。

### 3. 编译项目

```bash
mvn clean install
```

### 4. 启动应用

```bash
cd ai-meeting-boot
mvn spring-boot:run
```

或者运行打包后的jar：

```bash
java -jar ai-meeting-boot/target/ai-meeting-boot.jar
```

### 5. 访问系统

根据配置文件中的端口（默认8082）访问：

- 登录页面: http://localhost:8082/index.html
- 预约页面: http://localhost:8082/booking.html
- H2控制台（仅H2模式）: http://localhost:8082/h2-console

**注意**: 如果修改了`application-dev.properties`中的`server.port`，请使用对应的端口号。

**H2数据库说明**：
- H2是内存数据库，应用重启后数据会丢失
- 适合开发和测试环境
- 生产环境建议使用MySQL等持久化数据库

## 默认账号

系统初始化了以下测试账号（密码均为 `password123`）：

- `admin` - 管理员
- `user1` - 用户1
- `user2` - 用户2

## API接口说明

### 认证接口

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/current` - 获取当前用户信息

### 会议室接口

- `POST /api/rooms/query` - 查询可用会议室
- `GET /api/rooms/{roomId}` - 获取会议室详情

### 预约接口

- `POST /api/bookings` - 创建预约
- `GET /api/bookings/my` - 获取我的预约列表
- `GET /api/bookings/{bookingId}` - 获取预约详情
- `POST /api/bookings/{bookingId}/cancel` - 取消预约
- `POST /api/bookings/{bookingId}/checkin` - 签到

## 业务规则

### 预约规则

1. **时间范围**: 只能预约未来30天内的会议室
2. **时长限制**: 会议时长必须在30分钟到4小时之间
3. **提前预约**: 会议开始前5分钟不可新预约或修改
4. **冲突检测**: 不允许时间段与已有预约冲突

### 取消规则

- 会议开始前5分钟不可取消预约

### 签到规则

- 签到时间窗口：会议开始前10分钟至开始后15分钟

## 项目结构

```
ai-meeting-speckit/
├── ai-meeting-api/          # API接口定义和DTO
├── ai-meeting-application/  # 应用服务层
├── ai-meeting-boot/         # 启动模块（包含前端静态资源）
├── ai-meeting-domain/       # 领域模型和服务
├── ai-meeting-infrastructure/ # 基础设施层（数据访问）
├── ai-meeting-service/      # Web控制器层
└── openspec/                # OpenSpec规范文档
```

## 注意事项

1. **密码安全**: 当前版本使用明文密码，生产环境应使用BCrypt等加密算法
2. **会话管理**: 当前使用HttpSession，生产环境可考虑JWT
3. **并发控制**: 预约冲突检测在应用层实现，高并发场景建议使用数据库锁
4. **ID生成**: Booking的ID由数据库自动生成，保存后会自动查询获取

## 后续优化建议

1. 添加单元测试和集成测试
2. 实现管理员功能（会议室管理、统计报表）
3. 支持重复预约（周期性会议）
4. 添加邮件/消息通知
5. 实现签退功能
6. 移动端适配
7. 对接统一身份认证系统

## 问题反馈

如有问题，请查看日志文件或联系开发团队。

