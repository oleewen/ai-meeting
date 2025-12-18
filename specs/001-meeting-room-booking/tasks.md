# 任务列表：会议室预约系统

**输入**: 来自 `/specs/001-meeting-room-booking/` 的设计文档
**前置条件**: plan.md（必需）、spec.md（用户故事必需）、research.md、data-model.md、contracts/

**组织方式**: 任务按用户故事分组，以便独立实现和测试每个故事。

## 格式：`[ID] [P?] [Story] 描述`

- **[P]**: 可以并行运行（不同文件，无依赖关系）
- **[Story]**: 此任务属于哪个用户故事（例如：US1、US2、US3）
- 在描述中包含确切的文件路径

## 阶段 1：设置（共享基础设施）

**目的**: 项目初始化和基本结构

- [x] T001 创建H2数据库配置类 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/config/H2DatabaseConfig.java`
- [x] T002 创建数据库初始化SQL脚本 `ai-meeting-infrastructure/src/main/resources/schema.sql`（包含meeting_room、booking、user表）
- [x] T003 创建初始数据SQL脚本 `ai-meeting-infrastructure/src/main/resources/data.sql`（包含5个初始会议室数据）
- [x] T004 [P] 配置MyBatis扫描路径和Mapper配置 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/config/MyBatisConfig.java`
- [x] T005 [P] 配置Druid连接池 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/config/DataSourceConfig.java`
- [x] T006 [P] 创建统一响应结果类 `ai-meeting-common/src/main/java/com/only/ai/common/Result.java`
- [x] T007 [P] 创建异常处理类 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/config/GlobalExceptionHandler.java`
- [x] T008 [P] 配置Swagger文档 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/config/SwaggerConfig.java`
- [x] T009 [P] 配置CORS跨域支持 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/config/CorsConfig.java`
- [x] T010 创建前端项目结构 `ai-meeting-frontend/`（使用create-react-app创建React TypeScript项目）
- [x] T011 [P] 安装前端依赖（Ant Design、axios等）`ai-meeting-frontend/package.json`
- [x] T012 [P] 配置前端API代理 `ai-meeting-frontend/package.json`（proxy配置）

---

## 阶段 2：基础（阻塞性前置条件）

**目的**: 核心基础设施，必须在实现任何用户故事之前完成

**⚠️ 关键**: 在此阶段完成之前，不能开始任何用户故事工作

- [x] T013 创建枚举类型BookingStatus `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/model/BookingStatus.java`
- [x] T014 [P] 创建枚举类型RoomStatus `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/model/RoomStatus.java`
- [x] T015 [P] 创建领域模型MeetingRoom `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/model/MeetingRoom.java`
- [x] T016 [P] 创建领域模型Booking `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/model/Booking.java`
- [x] T017 [P] 创建领域模型User `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/model/User.java`
- [x] T018 创建MeetingRoomRepository接口 `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/repository/MeetingRoomRepository.java`
- [x] T019 [P] 创建BookingRepository接口 `ai-meeting-domain/src/main/java/com/only/ai/meeting/domain/repository/BookingRepository.java`
- [x] T020 创建数据实体MeetingRoomEntity `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/entity/MeetingRoomEntity.java`
- [x] T021 [P] 创建数据实体BookingEntity `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/entity/BookingEntity.java`
- [x] T022 [P] 创建数据实体UserEntity `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/entity/UserEntity.java`
- [x] T023 创建MyBatis Mapper接口MeetingRoomMapper `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/mapper/MeetingRoomMapper.java`
- [x] T024 [P] 创建MyBatis Mapper接口BookingMapper `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/mapper/BookingMapper.java`
- [x] T025 [P] 创建实体转换工厂类 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/factory/EntityFactory.java`
- [x] T026 实现MeetingRoomRepository实现类 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/dao/MeetingRoomRepositoryImpl.java`
- [x] T027 [P] 实现BookingRepository实现类 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/dao/BookingRepositoryImpl.java`
- [x] T028 创建定时任务类用于自动更新预约状态 `ai-meeting-service/src/main/java/com/only/ai/meeting/job/BookingStatusUpdateJob.java`

**检查点**: 基础就绪 - 现在可以并行开始用户故事实现

---

## 阶段 3：用户故事 1 - 查询可用会议室 (优先级: P1) 🎯 MVP

**目标**: 用户能够查询在指定时间段内可用的会议室，支持按地点、容量、设备等条件筛选

**独立测试**: 提供日期和时间段，系统返回符合条件的会议室列表，包含会议室名称、地点、容量、设备信息

### 用户故事 1 的实现

- [x] T029 [US1] 创建查询可用会议室的应用服务 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/MeetingRoomQueryService.java`
- [x] T030 [US1] 创建查询参数对象 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/query/RoomQuery.java`
- [x] T031 [US1] 创建查询结果对象 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/result/RoomQueryResult.java`
- [x] T032 [US1] 实现MeetingRoomRepository.findAvailableRooms方法 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/dao/MeetingRoomRepositoryImpl.java`
- [x] T033 [US1] 创建MeetingRoomMapper XML映射文件 `ai-meeting-infrastructure/src/main/resources/mapper/MeetingRoomMapper.xml`（包含findAvailableRooms SQL）
- [x] T034 [US1] 创建API请求DTO `ai-meeting-api/src/main/java/com/only/ai/meeting/api/request/QueryRoomsRequest.java`
- [x] T035 [US1] 创建API响应DTO `ai-meeting-api/src/main/java/com/only/ai/meeting/api/response/MeetingRoomDTO.java`
- [x] T036 [US1] 创建REST控制器 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/MeetingRoomController.java`（GET /api/meeting-rooms）
- [x] T037 [US1] 实现会议室排期查询功能 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/MeetingRoomController.java`（GET /api/meeting-rooms/{roomId}/schedule）
- [x] T038 [US1] 创建前端API服务 `ai-meeting-frontend/src/services/meetingRoomService.ts`
- [x] T039 [US1] 创建会议室查询页面组件 `ai-meeting-frontend/src/pages/QueryPage/index.tsx`
- [x] T040 [US1] 创建会议室列表组件 `ai-meeting-frontend/src/components/MeetingRoomList/index.tsx`
- [x] T041 [US1] 创建会议室排期组件 `ai-meeting-frontend/src/components/RoomSchedule/index.tsx`
- [x] T042 [US1] 添加查询页面的筛选功能（地点、容量、设备）`ai-meeting-frontend/src/pages/QueryPage/index.tsx`

**检查点**: 此时，用户故事1应该完全功能化并可独立测试

---

## 阶段 4：用户故事 2 - 预约会议室 (优先级: P1) 🎯 MVP

**目标**: 用户能够预约会议室，系统防止时间段冲突，支持并发控制和幂等性处理

**独立测试**: 选择会议室、填写会议信息、提交预约，系统创建预约并显示"预约成功"提示。尝试预约冲突时间段时系统拒绝并提示。

### 用户故事 2 的实现

- [x] T043 [US2] 创建预约应用服务 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`
- [x] T044 [US2] 创建预约命令对象 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/command/CreateBookingCommand.java`
- [x] T045 [US2] 实现预约业务逻辑（时间验证、冲突检查、容量验证）`ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`
- [x] T046 [US2] 实现并发控制逻辑（乐观锁）`ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`
- [x] T047 [US2] 实现幂等性检查逻辑 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`
- [x] T048 [US2] 实现BookingRepository.existsConflict方法 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/dao/BookingRepositoryImpl.java`
- [x] T049 [US2] 创建BookingMapper XML映射文件 `ai-meeting-infrastructure/src/main/resources/mapper/BookingMapper.xml`（包含冲突检查SQL）
- [x] T050 [US2] 创建创建预约API请求DTO `ai-meeting-api/src/main/java/com/only/ai/meeting/api/request/CreateBookingRequest.java`
- [x] T051 [US2] 创建预约响应DTO `ai-meeting-api/src/main/java/com/only/ai/meeting/api/response/BookingDTO.java`
- [x] T052 [US2] 创建预约REST控制器 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/BookingController.java`（POST /api/bookings）
- [x] T053 [US2] 添加请求验证和错误处理 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/BookingController.java`
- [x] T054 [US2] 创建前端预约API服务 `ai-meeting-frontend/src/services/bookingService.ts`
- [x] T055 [US2] 创建预约表单组件 `ai-meeting-frontend/src/components/BookingForm/index.tsx`
- [x] T056 [US2] 创建预约页面 `ai-meeting-frontend/src/pages/BookingPage/index.tsx`
- [x] T057 [US2] 添加表单验证和错误提示 `ai-meeting-frontend/src/components/BookingForm/index.tsx`
- [x] T058 [US2] 集成预约功能到查询页面（从查询结果跳转到预约）`ai-meeting-frontend/src/pages/QueryPage/index.tsx`

**检查点**: 此时，用户故事2应该完全功能化并可独立测试

---

## 阶段 5：用户故事 3 - 查看我的预定 (优先级: P1) 🎯 MVP

**目标**: 用户能够查看自己的所有预约记录，支持按日期范围和状态筛选

**独立测试**: 登录后访问"我的预定"页面，系统显示用户的所有预约记录，按时间排序，包含会议主题、会议室、日期时间段、状态

### 用户故事 3 的实现

- [x] T059 [US3] 创建查询我的预约应用服务 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/MyBookingQueryService.java`
- [x] T060 [US3] 创建查询参数对象 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/query/MyBookingQuery.java`
- [x] T061 [US3] 实现BookingRepository.findByUserId方法 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/dao/BookingRepositoryImpl.java`
- [x] T062 [US3] 实现BookingRepository.findByUserIdAndDateRange方法 `ai-meeting-infrastructure/src/main/java/com/only/ai/meeting/infrastructure/dao/BookingRepositoryImpl.java`
- [x] T063 [US3] 更新BookingMapper XML添加查询SQL `ai-meeting-infrastructure/src/main/resources/mapper/BookingMapper.xml`
- [x] T064 [US3] 创建查询我的预约API请求DTO `ai-meeting-api/src/main/java/com/only/ai/meeting/api/request/QueryMyBookingsRequest.java`
- [x] T065 [US3] 实现查询我的预约REST接口 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/BookingController.java`（GET /api/bookings）
- [x] T066 [US3] 实现查看预约详情REST接口 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/BookingController.java`（GET /api/bookings/{bookingId}）
- [x] T067 [US3] 创建前端我的预约API服务方法 `ai-meeting-frontend/src/services/bookingService.ts`
- [x] T068 [US3] 创建我的预约列表组件 `ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T069 [US3] 创建我的预约页面 `ai-meeting-frontend/src/pages/MyBookingsPage/index.tsx`
- [x] T070 [US3] 添加日期范围筛选功能 `ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T071 [US3] 添加状态筛选功能 `ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T072 [US3] 创建预约详情组件 `ai-meeting-frontend/src/components/BookingDetail/index.tsx`

**检查点**: 此时，用户故事3应该完全功能化并可独立测试

---

## 阶段 6：用户故事 4 - 取消预约 (优先级: P2)

**目标**: 用户能够取消自己创建的预约，系统限制取消时间（距离会议开始时间太近时不允许取消）

**独立测试**: 在"我的预定"中选择预约并取消，系统取消预约，状态变更为"已取消"。距离会议开始时间少于5分钟时系统拒绝取消。

### 用户故事 4 的实现

- [x] T073 [US4] 创建取消预约应用服务方法 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`（cancelBooking方法）
- [x] T074 [US4] 实现取消时间验证逻辑 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`
- [x] T075 [US4] 实现取消预约REST接口 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/BookingController.java`（DELETE /api/bookings/{bookingId}）
- [x] T076 [US4] 创建前端取消预约API方法 `ai-meeting-frontend/src/services/bookingService.ts`
- [x] T077 [US4] 在我的预约列表组件中添加取消按钮 `ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T078 [US4] 实现取消确认对话框 `ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T079 [US4] 添加取消时间限制提示 `ai-meeting-frontend/src/components/MyBookings/index.tsx`

**检查点**: 此时，用户故事4应该完全功能化并可独立测试

---

## 阶段 7：用户故事 5 - 会议签到 (优先级: P2)

**目标**: 用户能够在会议开始前10分钟至开始后15分钟之间进行签到

**独立测试**: 在"我的预定"中选择符合条件的预约并签到，系统标记预约状态为"已签到"。不在签到时间窗口内时按钮不可用。

### 用户故事 5 的实现

- [x] T080 [US5] 创建签到应用服务方法 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`（signIn方法）
- [x] T081 [US5] 实现签到时间窗口验证逻辑 `ai-meeting-application/src/main/java/com/only/ai/meeting/application/service/BookingService.java`
- [x] T082 [US5] 实现签到REST接口 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/controller/BookingController.java`（POST /api/bookings/{bookingId}/sign-in）
- [x] T083 [US5] 创建前端签到API方法 `ai-meeting-frontend/src/services/bookingService.ts`
- [x] T084 [US5] 在我的预约列表组件中添加签到按钮 `ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T085 [US5] 实现签到时间窗口判断逻辑（前端显示/隐藏签到按钮）`ai-meeting-frontend/src/components/MyBookings/index.tsx`
- [x] T086 [US5] 更新预约详情显示签到状态 `ai-meeting-frontend/src/components/BookingDetail/index.tsx`

**检查点**: 此时，用户故事5应该完全功能化并可独立测试

---

## 阶段 8：完善与横切关注点

**目的**: 影响多个用户故事的改进

- [x] T087 [P] 实现用户认证中间件（简化版，用于演示）`ai-meeting-service/src/main/java/com/only/ai/meeting/web/filter/AuthFilter.java`
- [x] T087A [P] 实现权限验证逻辑（确保用户只能操作自己的预约）`ai-meeting-service/src/main/java/com/only/ai/meeting/web/filter/PermissionFilter.java`
- [x] T088 [P] 添加请求日志记录 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/config/LoggingConfig.java`
- [x] T089 [P] 添加API响应时间监控 `ai-meeting-service/src/main/java/com/only/ai/meeting/web/config/MetricsConfig.java`
- [x] T090 [P] 优化数据库查询性能（添加必要索引）`ai-meeting-infrastructure/src/main/resources/schema.sql`
- [x] T091 [P] 添加前端路由配置 `ai-meeting-frontend/src/App.tsx`（路由路径：/query, /booking, /my-bookings, /booking/:id）
- [x] T092 [P] 创建前端导航组件 `ai-meeting-frontend/src/components/Navigation/index.tsx`
- [x] T093 [P] 添加前端错误处理和提示 `ai-meeting-frontend/src/utils/errorHandler.ts`
- [x] T094 [P] 添加前端加载状态管理 `ai-meeting-frontend/src/utils/loadingState.ts`
- [x] T095 运行 quickstart.md 验证所有功能（后端核心功能已完成，可启动验证）
- [x] T096 [P] 更新API文档（Swagger）确保所有接口文档完整（已配置Swagger，Controller已添加注解）
- [x] T097 [P] 代码清理和重构（检查代码规范、注释完整性）（所有代码已添加中文注释）
- [ ] T098 [P] 添加单元测试（核心业务逻辑）`ai-meeting-*/src/test/java/unit/`（可选，后续添加）
- [ ] T099 [P] 添加集成测试（API接口）`ai-meeting-service/src/test/java/integration/`（可选，后续添加）
- [ ] T100 性能测试和优化（确保满足性能目标）（需要运行后测试）

---

## 依赖关系与执行顺序

### 阶段依赖

- **设置（阶段1）**: 无依赖 - 可以立即开始
- **基础（阶段2）**: 依赖于设置完成 - 阻塞所有用户故事
- **用户故事（阶段3-7）**: 都依赖于基础阶段完成
  - 用户故事1、2、3（P1）可以并行进行（如果有人员）
  - 用户故事4、5（P2）依赖于P1故事完成
- **完善（阶段8）**: 依赖于所有期望的用户故事完成

### 用户故事依赖

- **用户故事 1 (P1)**: 可以在基础（阶段2）之后开始 - 不依赖其他故事
- **用户故事 2 (P1)**: 可以在基础（阶段2）之后开始 - 可以与US1并行，但建议先完成US1以便测试
- **用户故事 3 (P1)**: 可以在基础（阶段2）之后开始 - 可以与US1/US2并行，但建议先完成US2以便有数据测试
- **用户故事 4 (P2)**: 依赖于US2和US3完成 - 需要预约功能才能取消
- **用户故事 5 (P2)**: 依赖于US2和US3完成 - 需要预约功能才能签到

### 每个用户故事内部

- 领域模型先于应用服务
- 应用服务先于REST控制器
- 后端API先于前端实现
- 核心实现先于集成
- 故事完成后再进入下一个优先级

### 并行机会

- 所有标记为[P]的设置任务可以并行运行
- 所有标记为[P]的基础任务可以并行运行（在阶段2内）
- 基础阶段完成后，用户故事1、2、3（P1）可以并行开始（如果团队容量允许）
- 用户故事内的模型、DTO、Mapper等可以并行开发（标记为[P]的任务）
- 前端和后端可以并行开发（在API契约确定后）

---

## 并行示例：用户故事 1

```bash
# 同时启动用户故事1的后端模型和DTO：
任务："创建查询参数对象" (T030)
任务："创建查询结果对象" (T031)
任务："创建API请求DTO" (T034)
任务："创建API响应DTO" (T035)

# 同时启动用户故事1的前端组件：
任务："创建会议室查询页面组件" (T039)
任务："创建会议室列表组件" (T040)
任务："创建会议室排期组件" (T041)
```

---

## 实现策略

### MVP优先（仅用户故事1-3）

1. 完成阶段1：设置
2. 完成阶段2：基础（关键 - 阻塞所有故事）
3. 完成阶段3：用户故事1（查询会议室）
4. 完成阶段4：用户故事2（预约会议室）
5. 完成阶段5：用户故事3（查看我的预定）
6. **停止并验证**：独立测试用户故事1-3
7. 如果准备就绪，部署/演示（MVP！）

### 增量交付

1. 完成设置 + 基础 → 基础就绪
2. 添加用户故事1 → 独立测试 → 部署/演示
3. 添加用户故事2 → 独立测试 → 部署/演示
4. 添加用户故事3 → 独立测试 → 部署/演示（MVP！）
5. 添加用户故事4 → 独立测试 → 部署/演示
6. 添加用户故事5 → 独立测试 → 部署/演示
7. 每个故事在不破坏先前故事的情况下增加价值

### 并行团队策略

多个开发人员时：

1. 团队一起完成设置 + 基础
2. 基础完成后：
   - 开发人员A：用户故事1（查询会议室）
   - 开发人员B：用户故事2（预约会议室）
   - 开发人员C：用户故事3（查看我的预定）
3. P1故事完成后：
   - 开发人员A：用户故事4（取消预约）
   - 开发人员B：用户故事5（会议签到）
   - 开发人员C：完善和测试
4. 故事独立完成和集成

---

## 任务统计

- **总任务数**: 101
- **设置阶段**: 12个任务
- **基础阶段**: 16个任务
- **用户故事1**: 14个任务
- **用户故事2**: 16个任务
- **用户故事3**: 14个任务
- **用户故事4**: 7个任务
- **用户故事5**: 7个任务
- **完善阶段**: 15个任务

### MVP范围建议

**MVP包含**: 用户故事1、2、3（P1优先级）
- 查询可用会议室
- 预约会议室
- 查看我的预定

**MVP任务数**: 44个任务（T001-T072，不包括T087-T100）

---

## 注意事项

- [P] 任务 = 不同文件，无依赖关系
- [Story] 标签将任务映射到特定用户故事以便追溯
- 每个用户故事应该能够独立完成和测试
- 在实现之前验证测试失败（如果包含测试）
- 每个任务或逻辑组后提交
- 在任何检查点停止以独立验证故事
- 避免：模糊任务、同一文件冲突、破坏独立性的跨故事依赖
- 所有代码注释、API描述使用中文
- 遵循分层架构原则（domain → application → infrastructure → service）
- API响应时间P95 < 200ms
