# 实现计划：[功能]

**分支**: `[###-feature-name]` | **日期**: [DATE] | **规格**: [link]
**输入**: 来自 `/specs/[###-feature-name]/spec.md` 的功能规格

**注意**: 此模板由 `/speckit.plan` 命令填充。执行工作流请参见 `.specify/templates/commands/plan.md`。

## 摘要

[从功能规格中提取：主要需求 + 研究中的技术方法]

## 技术上下文

<!--
  操作要求：用项目的技术细节替换本节中的内容。
  这里提供的结构以咨询能力呈现，以指导迭代过程。
-->

**语言/版本**: [例如：Python 3.11、Swift 5.9、Rust 1.75 或 需要澄清]  
**主要依赖**: [例如：FastAPI、UIKit、LLVM 或 需要澄清]  
**存储**: [如适用，例如：PostgreSQL、CoreData、文件 或 不适用]  
**测试**: [例如：pytest、XCTest、cargo test 或 需要澄清]  
**目标平台**: [例如：Linux服务器、iOS 15+、WASM 或 需要澄清]
**项目类型**: [single/web/mobile - 确定源代码结构]  
**性能目标**: [特定于领域，例如：1000 req/s、10k lines/sec、60 fps 或 需要澄清]  
**约束**: [特定于领域，例如：<200ms p95、<100MB内存、离线能力 或 需要澄清]  
**规模/范围**: [特定于领域，例如：10k用户、1M LOC、50个屏幕 或 需要澄清]

## 规范检查

*门禁：必须在阶段0研究之前通过。阶段1设计后重新检查。*

**规范符合性检查**（基于 `.specify/memory/constitution.md`）：

- [ ] **语言要求**：所有文档、注释、API描述使用中文
- [ ] **架构分层**：新功能遵循domain → application → infrastructure → service的分层依赖规则，前后端分离
- [ ] **代码组织**：包结构符合规范（domain、application、infrastructure、api、service、client、common、boot、frontend）
- [ ] **测试优先**：已规划测试策略（单元测试、集成测试、契约测试），后端使用JUnit 5/Mockito，前端使用React Testing Library
- [ ] **依赖管理**：新增依赖已评审，版本号明确，无跨层依赖，Maven依赖统一在父POM管理
- [ ] **技术栈**：后端使用Java 17 + Spring Boot 2.7.10 + MyBatis，前端使用TypeScript + React 19 + Ant Design
- [ ] **JSON序列化**：API使用snake_case命名策略
- [ ] **性能约束**：API响应时间P95 < 200ms、扩展性等性能要求已考虑

**违反项说明**（如有）：[列出任何违反规范的情况及理由]

## 项目结构

### 文档（此功能）

```text
specs/[###-feature]/
├── plan.md              # 此文件（/speckit.plan 命令输出）
├── research.md          # 阶段0输出（/speckit.plan 命令）
├── data-model.md        # 阶段1输出（/speckit.plan 命令）
├── quickstart.md        # 阶段1输出（/speckit.plan 命令）
├── contracts/           # 阶段1输出（/speckit.plan 命令）
└── tasks.md             # 阶段2输出（/speckit.tasks 命令 - 不由 /speckit.plan 创建）
```

### 源代码（仓库根目录）
<!--
  操作要求：用此功能的具体布局替换下面的占位符树。
  删除未使用的选项，并用真实路径扩展所选结构
  （例如：apps/admin、packages/something）。交付的计划不得包含选项标签。
-->

```text
# [如果未使用则删除] 选项1：单项目（默认）
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [如果未使用则删除] 选项2：Web应用（当检测到"frontend" + "backend"时）
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [如果未使用则删除] 选项3：移动 + API（当检测到"iOS/Android"时）
api/
└── [与上面的backend相同]

ios/ 或 android/
└── [平台特定结构：功能模块、UI流程、平台测试]
```

**结构决策**: [记录所选结构并引用上面捕获的真实目录]

## 复杂度跟踪

> **仅在规范检查有必须证明的违反项时填写**

| 违反项 | 为什么需要 | 被拒绝的更简单替代方案的原因 |
|-----------|------------|-------------------------------------|
| [例如：第4个项目] | [当前需求] | [为什么3个项目不够] |
| [例如：Repository模式] | [具体问题] | [为什么直接DB访问不够] |
