# 变更：添加AI会议议程推荐功能

## 为什么
用户在创建会议预约时，经常需要手动编写会议议程和备注信息，这个过程耗时且容易遗漏重要内容。通过引入腾讯云混元大模型AI服务，系统可以根据会议主题自动生成Markdown格式的结构化会议议程推荐，并自动填入备注字段，提升用户体验，减少用户输入负担，同时确保会议议程的完整性和专业性。

## 变更内容
- **新增功能：AI议程推荐** - 提供API端点，根据会议主题生成Markdown格式的会议议程推荐
- **集成腾讯云混元大模型API** - 在基础设施层集成腾讯云混元大模型API（Hunyuan API），实现AI议程生成能力
- **自动填充备注** - 用户预览AI生成的议程后，确认后自动将Markdown格式的议程填入备注字段
- **前端交互优化** - 在预约页面会议主题输入框附近添加"AI生成议程"按钮（输入主题后立即显示），点击后在模态框中预览议程内容

## 影响
- **受影响规范**：
  - `room-booking` - 修改会议室预约需求，添加AI议程推荐场景
- **受影响代码**：
  - `ai-meeting-domain` - 可能需要新增领域服务（如AgendaGenerationService，可选）
  - `ai-meeting-application` - 新增议程生成应用服务（AgendaGenerationApplicationService）
  - `ai-meeting-infrastructure` - 新增腾讯云混元大模型客户端实现（HunyuanApiClient）
  - `ai-meeting-api` - 新增议程推荐API接口定义（AgendaSuggestionRequest/Response）
  - `ai-meeting-service` - 新增议程推荐Controller端点
  - `ai-meeting-boot` - 需要配置腾讯云混元大模型API密钥（API Key和Secret Key）
- **外部依赖**：需要接入腾讯云混元大模型API服务（需要API Key和Secret Key）
- **前端资源**：需要更新预约页面，添加AI生成议程的交互功能

