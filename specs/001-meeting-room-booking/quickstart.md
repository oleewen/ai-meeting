# 快速开始指南

**功能**: 会议室预约系统  
**日期**: 2025-01-27

## 环境要求

- Java 17+
- Maven 3.6+
- Node.js 16+ (前端开发)
- npm 或 yarn (前端包管理)

## 后端启动步骤

### 1. 配置数据库

系统使用H2内存数据库，无需额外配置。数据库会在应用启动时自动初始化。

如需修改数据库配置，编辑 `ai-meeting-boot/src/main/resources/application.properties`:

```properties
# H2数据库配置
spring.datasource.url=jdbc:h2:mem:meetingdb;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2控制台（开发环境）
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### 2. 初始化数据

数据库初始化脚本位于：
- `ai-meeting-infrastructure/src/main/resources/schema.sql` - 表结构
- `ai-meeting-infrastructure/src/main/resources/data.sql` - 初始数据

### 3. 启动应用

```bash
# 在项目根目录执行
cd ai-meeting-boot
mvn spring-boot:run
```

或使用IDE直接运行 `ApplicationStarter` 类。

### 4. 验证启动

- 应用启动后访问：http://localhost:8080
- H2控制台：http://localhost:8080/h2-console
- Swagger API文档：http://localhost:8080/swagger-ui.html

## 前端启动步骤

### 1. 创建前端项目（如果尚未创建）

```bash
# 在项目根目录创建前端项目
npx create-react-app ai-meeting-frontend --template typescript
cd ai-meeting-frontend
```

### 2. 安装依赖

```bash
npm install antd axios
# 或
yarn add antd axios
```

### 3. 配置代理

在 `package.json` 中添加代理配置（开发环境）：

```json
{
  "proxy": "http://localhost:8080"
}
```

### 4. 启动开发服务器

```bash
npm start
# 或
yarn start
```

前端应用将在 http://localhost:3000 启动。

## API测试

### 使用Swagger UI

1. 访问 http://localhost:8080/swagger-ui.html
2. 查看所有API接口
3. 直接在Swagger UI中测试API

### 使用curl命令

```bash
# 查询可用会议室
curl -X GET "http://localhost:8080/api/meeting-rooms?date=2025-01-27&startTime=09:00&endTime=10:00"

# 创建预约
curl -X POST "http://localhost:8080/api/bookings" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "date": "2025-01-27",
    "startTime": "09:00",
    "endTime": "10:00",
    "subject": "项目评审会议",
    "attendeeCount": 8,
    "remark": "需要投影仪"
  }'

# 查看我的预定
curl -X GET "http://localhost:8080/api/bookings"
```

## 开发工作流

### 1. 数据库变更

1. 修改 `data-model.md` 中的实体定义
2. 更新 `schema.sql` 中的表结构
3. 更新对应的领域模型类
4. 更新MyBatis Mapper和Entity类

### 2. API变更

1. 修改 `contracts/openapi.yaml` 中的API定义
2. 更新对应的Controller、Request、Response类
3. 更新Swagger注解
4. 更新前端API服务

### 3. 功能开发

1. 在domain层定义领域模型和接口
2. 在application层实现应用服务
3. 在infrastructure层实现数据访问
4. 在service层实现REST控制器
5. 编写单元测试和集成测试
6. 更新前端页面和组件

## 测试

### 运行后端测试

```bash
# 运行所有测试
mvn test

# 运行特定模块的测试
cd ai-meeting-domain
mvn test
```

### 运行前端测试

```bash
cd ai-meeting-frontend
npm test
```

## 常见问题

### 1. H2数据库连接失败

- 检查 `application.properties` 中的数据库配置
- 确认H2依赖已正确添加到pom.xml

### 2. 端口被占用

- 修改 `application.properties` 中的 `server.port` 配置
- 或使用 `-Dserver.port=8081` 启动参数

### 3. 前端无法连接后端

- 检查后端是否已启动
- 检查代理配置是否正确
- 检查CORS配置是否允许前端域名

### 4. 数据库初始化失败

- 检查SQL脚本语法是否正确
- 检查表是否已存在（H2内存数据库重启后会清空）
- 查看应用启动日志中的错误信息

## 下一步

- 查看 [data-model.md](./data-model.md) 了解数据模型设计
- 查看 [contracts/openapi.yaml](./contracts/openapi.yaml) 了解API接口定义
- 查看 [research.md](./research.md) 了解技术选型决策
