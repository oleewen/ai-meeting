package com.only.ai.meetingroom.infrastructure.call;

/**
 * DeepSeek大模型API客户端接口
 *
 * @author only
 * @since 2024-01-01
 */
public interface DeepSeekApiClient {
    /**
     * 根据会议主题生成会议议程
     *
     * @param subject 会议主题
     * @return 生成的Markdown格式议程内容
     * @throws DeepSeekApiException 当API调用失败时抛出
     */
    String generateAgenda(String subject) throws DeepSeekApiException;
}

