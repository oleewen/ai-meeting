<!--
Sync Impact Report:
Version change: 1.0.0 → 1.1.0 (template localization)
Modified principles: None
Added sections: None
Removed sections: None
Templates requiring updates:
  ✅ plan-template.md - 完整中文化，规范检查部分已更新
  ✅ spec-template.md - 完整中文化
  ✅ tasks-template.md - 完整中文化
  ✅ checklist-template.md - 完整中文化
  ✅ agent-file-template.md - 完整中文化
Follow-up TODOs: None
-->

# AI Meeting Speckit Constitution

## Core Principles

### I. 中文作为默认语言 (Chinese as Default Language)
所有项目交互、响应、文档生成、代码注释、API文档、用户界面文本等均使用中文作为默认语言。英文仅在技术术语、框架名称、第三方库名称等无法避免的情况下使用。此原则确保项目团队和用户能够以最自然的方式理解和协作。

### II. 分层架构原则 (Layered Architecture)
项目采用六边形架构（Hexagonal Architecture）/洋葱架构（Onion Architecture）/整洁架构（Clean Architecture）模式，严格遵循分层依赖规则：
- **domain层**：领域模型、领域服务、资源库接口、领域事件、查询门面，不依赖任何基础设施
- **application层**：应用服务、用例处理节点、命令/查询、结果对象，仅依赖domain层
- **infrastructure层**：数据访问实现、外部服务调用、消息处理，实现domain层定义的接口
- **api层**：公共接口定义、DTO、常量、枚举，可被service层和client层依赖
- **service层**：表现层，HTTP控制器、RPC服务实现，依赖application和infrastructure层
- **client层**：富客户端实现，依赖api层
- **boot层**：应用启动入口，组装各层组件

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
- **api层**：`com.only.ai.{context}.api|service|open` 包含 request、response、dto、Service接口
- **service层**：`com.only.ai.{context}` 包含 message、job、rpc、web（controller、request、response、config、filter）
- **client层**：`com.only.ai.{context}.{Aggregate}Client`
代码注释、类名、方法名使用中文描述业务含义，技术实现细节可用英文术语。

### V. 依赖管理原则 (Dependency Management)
- 模块间依赖必须遵循架构层次，禁止跨层依赖（如domain层不得依赖infrastructure层）
- 使用Maven进行依赖管理，版本号统一在父POM中管理
- 第三方依赖版本必须明确指定，避免使用SNAPSHOT版本（开发阶段除外）
- 新增依赖必须经过评审，避免引入不必要的依赖

## Architecture Standards

### 技术栈要求
- **语言**：Java 17
- **框架**：Spring Boot 2.7.10
- **构建工具**：Maven
- **编码规范**：UTF-8
- **数据库访问**：MyBatis
- **API文档**：Swagger（使用中文描述）

### 性能与约束
- API响应时间：P95延迟 < 200ms（除非业务场景特殊要求）
- 系统必须支持水平扩展
- 数据库连接池使用Druid，必须配置合理的连接数
- 日志必须结构化，便于监控和排查

## Development Workflow

### 代码审查要求
- 所有PR必须经过至少一人审查
- 审查重点：架构原则遵循、测试覆盖率、代码规范、中文注释质量
- 禁止合并未通过审查的代码

### 质量门禁
- 所有测试必须通过
- 代码覆盖率不低于70%（核心业务逻辑不低于80%）
- 静态代码检查必须通过（如SonarQube）
- 必须提供或更新相关中文文档

### 部署流程
- 开发环境：自动部署
- 测试环境：代码审查通过后自动部署
- 预发布环境：需要手动审批
- 生产环境：需要多级审批和回滚预案

## Governance

本规范文件是项目的最高指导原则，所有开发活动必须遵循本规范。规范修改必须：
1. 在规范文件中记录修改原因和影响范围
2. 更新版本号（遵循语义化版本：MAJOR.MINOR.PATCH）
3. 同步更新所有相关模板文件
4. 通知所有团队成员
5. 对于重大变更（MAJOR版本），需要团队评审和批准

所有PR和代码审查必须验证是否符合本规范。任何违反规范的情况必须说明理由并获得批准。

**Version**: 1.1.0 | **Ratified**: 2025-01-27 | **Last Amended**: 2025-01-27
