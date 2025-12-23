# AI会议议程推荐API文档

## 接口概述

提供根据会议主题自动生成会议议程推荐的API接口。

## 接口详情

### POST /api/bookings/agenda-suggestion

根据会议主题生成Markdown格式的会议议程推荐。

#### 请求

**请求头**：
```
Content-Type: application/json
```

**请求体**：
```json
{
  "subject": "产品规划会议"
}
```

**参数说明**：
- `subject` (string, 必填): 会议主题，不能为空

#### 响应

**成功响应** (200 OK)：
```json
{
  "success": true,
  "data": {
    "agenda": "## 会议目标\n\n讨论下一季度产品规划\n\n## 主要议题\n\n1. 市场分析\n2. 产品需求\n3. 技术方案\n\n## 时间分配\n\n- 市场分析：30分钟\n- 产品需求：45分钟\n- 技术方案：30分钟\n\n## 预期成果\n\n确定产品规划方向"
  }
}
```

**失败响应** (200 OK)：
```json
{
  "success": false,
  "code": "AGENDA_GENERATION_FAILED",
  "message": "AI服务暂时不可用，请稍后重试或手动输入备注"
}
```

#### 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| `AGENDA_GENERATION_FAILED` | AI服务调用失败 | 检查API配置，或手动输入备注 |
| `INVALID_PARAM` | 参数验证失败 | 检查请求参数 |

#### 业务规则

1. **内容长度验证**：生成的议程内容长度必须在50-500字符范围内
2. **超时处理**：API调用超时时间为3秒，超时后返回友好提示
3. **降级策略**：AI服务不可用时，不影响正常预约功能，用户可手动输入备注

#### 示例

**cURL示例**：
```bash
curl -X POST http://localhost:8082/api/bookings/agenda-suggestion \
  -H "Content-Type: application/json" \
  -d '{"subject": "产品规划会议"}'
```

**JavaScript示例**：
```javascript
fetch('/api/bookings/agenda-suggestion', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    subject: '产品规划会议'
  })
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    console.log('生成的议程:', data.data.agenda);
  } else {
    console.error('生成失败:', data.message);
  }
});
```

## 配置说明

### 腾讯云混元大模型API配置

在 `application-dev.properties`（或其他环境配置文件）中配置：

```properties
# 腾讯云混元大模型API配置
hunyuan.api.secret-id=your-secret-id
hunyuan.api.secret-key=your-secret-key
hunyuan.api.region=ap-beijing
```

**获取API密钥**：
1. 登录腾讯云控制台
2. 进入"访问管理" -> "API密钥管理"
3. 创建或查看SecretId和SecretKey

**注意事项**：
- 如果不配置API密钥，AI生成议程功能将不可用
- 实际部署时需要根据腾讯云API文档实现正确的签名认证
- 建议在生产环境中使用环境变量或配置中心管理敏感信息

