<!--
Sync Impact Report:
Version change: 1.2.0 → 1.3.0 (整合~/.claude/rules目录下的开发规范)
Modified principles: 
  - Development Workflow: 添加Git提交规范、更新质量门禁标准
  - Architecture Standards: 添加设计原则章节
  - Code Organization Standards: 补充异常处理和日志规范
Added sections: 
  - Git提交规范（Conventional Commits标准）
  - 设计原则（SOLID、DDD、整洁架构等）
  - 异常处理规范
  - 日志规范
  - 文档规范（JavaDoc、Mermaid图表）
Removed sections: None
Templates requiring updates:
  ⚠ plan-template.md - 需要添加Git提交规范检查项
  ⚠ spec-template.md - 需要添加设计原则检查项
  ⚠ tasks-template.md - 需要添加文档规范任务类型
  ⚠ checklist-template.md - 需要添加代码质量门禁检查项
  ✅ agent-file-template.md - 无需更新
Follow-up TODOs: None
-->

# AI Meeting Speckit Constitution

## Core Principles

### I. 中文作为默认语言 (Chinese as Default Language)
所有项目交互、响应、文档生成、代码注释、API文档、用户界面文本等均使用中文作为默认语言。英文仅在技术术语、框架名称、第三方库名称等无法避免的情况下使用。此原则确保项目团队和用户能够以最自然的方式理解和协作。

### II. 分层架构原则 (Layered Architecture)
项目采用前后端分离架构，后端采用六边形架构（Hexagonal Architecture）/洋葱架构（Onion Architecture）/整洁架构（Clean Architecture）模式，严格遵循分层依赖规则：
- **domain层**：领域模型、领域服务、资源库接口、领域事件、查询门面，不依赖任何基础设施
- **application层**：应用服务、用例处理节点、命令/查询、结果对象，仅依赖domain层
- **infrastructure层**：数据访问实现、外部服务调用、消息处理，实现domain层定义的接口
- **api层**：公共接口定义、DTO、常量、枚举，可被service层和client层依赖
- **service层**：表现层，HTTP控制器、RPC服务实现、定时任务，依赖application和infrastructure层
- **client层**：富客户端实现，依赖api层
- **boot层**：应用启动入口，组装各层组件
- **common层**：通用领域对象和工具类，可被domain层和其他层依赖
- **frontend层**：前端React应用，通过HTTP API与后端通信，独立部署和运行

### III. 测试优先原则 (Test-First Development)
所有新功能必须遵循测试驱动开发（TDD）流程：
1. 编写测试用例（单元测试、集成测试、契约测试）
2. 确保测试失败（Red阶段）
3. 实现最小化代码使测试通过（Green阶段）
4. 重构代码提升质量（Refactor阶段）
集成测试重点关注：新库的契约测试、契约变更、服务间通信、共享数据模型。

### IV. 代码组织规范 (Code Organization Standards)
每个模块必须遵循统一的包结构规范：
- **domain层**：`com.only.ai.{context}.domain` 包含 service、facade、model、event、repository
- **application层**：`com.only.ai.{context}.application` 包含 service、action、command、query、result
- **infrastructure层**：`com.only.ai.{context}.infrastructure` 包含 dao、config、entity、mapper、message、dal、call、factory
- **api层**：`com.only.ai.{context}.api` 包含 request、response、dto、Service接口
- **service层**：`com.only.ai.{context}` 包含 message、job、rpc、web（controller、request、response、config、filter）
- **client层**：`com.only.ai.{context}.{Aggregate}Client`
- **common层**：`com.only.ai.common.domain` 包含通用领域对象（如ValueObject、Id、MonetaryAmount等）
- **boot层**：`com.only.ai.boot` 包含应用启动类
- **frontend层**：`src/` 包含 components、pages、services、utils，使用TypeScript编写
代码注释、类名、方法名使用中文描述业务含义，技术实现细节可用英文术语。

#### 异常处理规范
项目必须使用统一的异常处理机制：
- **业务异常**：`BusinessException` - 业务规则不满足
- **系统异常**：`SystemException` - 系统级错误
- **参数异常**：`ValidationException` - 参数验证失败
- **数据异常**：`DataNotFoundException` - 数据不存在
所有异常必须通过全局异常处理器统一处理，返回标准化的错误响应。

#### 日志规范
项目必须遵循统一的日志规范：
- **ERROR**：系统异常、需要立即处理的问题
- **WARN**：潜在问题、业务异常
- **INFO**：业务关键路径、重要状态变更
- **DEBUG**：调试信息、详细执行流程
- **TRACE**：最详细信息，通常不开启
日志必须结构化，包含必要的上下文信息（如订单ID、用户ID等），便于监控和排查。

### V. 依赖管理原则 (Dependency Management)
- 模块间依赖必须遵循架构层次，禁止跨层依赖（如domain层不得依赖infrastructure层）
- 使用Maven进行依赖管理，版本号统一在父POM中管理
- 第三方依赖版本必须明确指定，避免使用SNAPSHOT版本（开发阶段除外）
- 新增依赖必须经过评审，避免引入不必要的依赖

## Architecture Standards

### 后端技术栈要求
- **语言**：Java 17
- **框架**：Spring Boot 2.7.10
- **构建工具**：Maven 3.6+
- **编码规范**：UTF-8
- **数据库访问**：MyBatis 2.3.2
- **数据库**：H2（开发/测试环境），支持迁移到PostgreSQL/MySQL
- **API文档**：SpringDoc OpenAPI 1.6.9（使用中文描述）
- **对象映射**：MapStruct 1.5.0.Final
- **代码生成**：Lombok 1.18.34
- **连接池**：Druid 1.2.23
- **JSON序列化**：Jackson，使用snake_case命名策略（PropertyNamingStrategy.SNAKE_CASE）
- **测试框架**：JUnit 5、Mockito、Spring Boot Test

### 前端技术栈要求
- **语言**：TypeScript 4.9.5
- **框架**：React 19.2.3
- **UI组件库**：Ant Design 6.1.1
- **路由**：React Router 7.11.0
- **HTTP客户端**：Axios 1.13.2
- **构建工具**：React Scripts 5.0.1
- **测试框架**：React Testing Library、Jest
- **日期处理**：dayjs 1.11.19

### 设计原则
项目必须遵循以下设计原则：

#### SOLID原则
- **单一职责原则（SRP）**：每个类只有一个变更理由
- **开闭原则（OCP）**：对扩展开放，对修改关闭
- **里氏替换原则（LSP）**：子类必须能够替换父类
- **接口隔离原则（ISP）**：客户端不应依赖不需要的接口
- **依赖倒置原则（DIP）**：依赖抽象而非具体实现

#### DDD原则
- **统一语言**：业务与技术团队共享同一语言体系
- **限界上下文**：明确业务边界，避免概念混淆
- **聚合根**：以聚合为中心设计业务一致性边界
- **领域事件**：通过事件驱动实现业务解耦

#### 架构原则
- **整洁架构**：核心业务逻辑与技术细节完全解耦
- **六边形架构**：业务核心独立于外部框架和工具
- **分层架构**：清晰的层次依赖关系，上层依赖下层
- **CQRS模式**：命令与查询职责分离，优化读写性能

#### 通用原则
- **KISS原则**：保持简单，避免过度设计
- **DRY原则**：不要重复自己，消除代码重复
- **YAGNI原则**：只实现当前需要的功能

### 性能与约束
- API响应时间：P95延迟 < 200ms（除非业务场景特殊要求）
- 系统必须支持水平扩展
- 数据库连接池使用Druid，必须配置合理的连接数
- 日志必须结构化，便于监控和排查
- 前端应用必须支持主流浏览器（Chrome、Edge、Firefox等），分辨率≥1366×768
- 前后端通过RESTful API通信，使用JSON格式，字段命名采用snake_case

## Development Workflow

### Git提交规范
项目遵循Conventional Commits标准，提交信息格式如下：

#### 提交格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

#### 提交类型
| 类型 | 描述 | 使用场景 |
|------|------|----------|
| **feature** | 新增功能 | 添加新功能、新特性 |
| **fix** | 修复bug | 修复代码缺陷、异常处理 |
| **docs** | 文档注释 | 更新文档、注释、README |
| **style** | 代码格式 | 不影响代码运行的格式调整 |
| **refactor** | 重构优化 | 代码重构、性能优化，不增加新功能 |
| **performance** | 性能优化 | 专门用于性能相关优化 |
| **test** | 增加测试 | 添加测试用例、测试框架 |
| **chore** | 构建工具 | 构建过程、依赖管理、配置文件 |
| **revert** | 回退代码 | 撤销之前的提交 |

#### 提交原则
- **原子性**：每个提交只包含一个逻辑变更
- **完整性**：提交必须包含完整的变更（代码+测试）
- **可回滚**：每个提交都可以独立回滚
- **可追溯**：提交信息必须关联需求或问题

#### 提交后流程
1. 提交后自动rebase远程代码
2. 如有冲突需手动解决
3. 无冲突时自动push到远程

### 代码审查要求
- 所有PR必须经过至少一人审查
- 审查重点：架构原则遵循、测试覆盖率、代码规范、中文注释质量、Git提交规范
- 禁止合并未通过审查的代码

### 质量门禁
项目必须满足以下质量门禁标准：

#### 测试覆盖率要求
| 指标 | 阈值 | 检查工具 | 说明 |
|------|------|----------|------|
| **单元测试覆盖率** | ≥80% | Jacoco | 核心业务逻辑必须覆盖 |
| **代码重复率** | ≤5% | PMD/CPD | 避免代码重复 |
| **严重漏洞** | 0 | SonarQube | 安全漏洞零容忍 |
| **编译警告** | 0 | Maven/Compiler | 保持代码清洁 |
| **代码风格违规** | ≤10 | Checkstyle | 统一编码风格 |

#### 测试分层策略
- **单元测试**：覆盖领域模型、领域服务
- **集成测试**：覆盖应用服务、仓储实现
- **端到端测试**：覆盖API接口、业务流程
- **性能测试**：覆盖关键业务路径

#### 其他要求
- 所有测试必须通过
- 静态代码检查必须通过（如SonarQube）
- 必须提供或更新相关中文文档

### 部署流程
- 开发环境：自动部署
- 测试环境：代码审查通过后自动部署
- 预发布环境：需要手动审批
- 生产环境：需要多级审批和回滚预案

## 文档规范

### 代码文档规范
- **JavaDoc注释**：所有公共API必须有JavaDoc注释，包含参数说明、返回值、异常信息
- **类文档**：说明类的用途和职责，描述重要的设计决策，说明使用注意事项
- **包文档**：在package-info.java中说明包的用途，描述包内的主要组件，说明包级别的约束

### 代码注释规范
- 解释复杂的业务逻辑
- 说明重要的算法实现
- 标注代码的局限性
- TODO注释必须明确说明待完成的工作，标注优先级和负责人

### 设计文档规范
- **架构文档**：使用Mermaid绘制业务模型图、系统架构图、模块依赖图、数据流图
- **设计文档**：使用Mermaid绘制领域模型（类图）、时序图、状态图、数据模型（ER图）
- **术语定义**：所有业务名词都有明确定义和统一语言
- 文档必须与代码同步更新，使用Git进行版本控制

## Governance

本规范文件是项目的最高指导原则，所有开发活动必须遵循本规范。规范修改必须：
1. 在规范文件中记录修改原因和影响范围
2. 更新版本号（遵循语义化版本：MAJOR.MINOR.PATCH）
3. 同步更新所有相关模板文件
4. 通知所有团队成员
5. 对于重大变更（MAJOR版本），需要团队评审和批准

所有PR和代码审查必须验证是否符合本规范。任何违反规范的情况必须说明理由并获得批准。

**Version**: 1.3.0 | **Ratified**: 2025-01-27 | **Last Amended**: 2025-01-28
