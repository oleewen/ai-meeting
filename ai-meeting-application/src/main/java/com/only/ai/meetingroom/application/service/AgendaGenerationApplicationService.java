package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.infrastructure.call.DeepSeekApiClient;
import com.only.ai.meetingroom.infrastructure.call.DeepSeekApiException;
import org.springframework.stereotype.Service;

/**
 * 议程生成应用服务
 *
 * @author only
 * @since 2024-01-01
 */
@Service
public class AgendaGenerationApplicationService {
    private static final int MIN_LENGTH = 50;
    private static final int MAX_LENGTH = 500;

    private final DeepSeekApiClient deepSeekApiClient;

    public AgendaGenerationApplicationService(DeepSeekApiClient deepSeekApiClient) {
        this.deepSeekApiClient = deepSeekApiClient;
    }

    /**
     * 根据会议主题生成会议议程
     *
     * @param subject 会议主题
     * @return 生成的Markdown格式议程内容
     * @throws AgendaGenerationException 当生成失败时抛出
     */
    public String generateAgenda(String subject) throws AgendaGenerationException {
        if (subject == null || subject.trim().isEmpty()) {
            throw new AgendaGenerationException("会议主题不能为空");
        }

        try {
            // 调用AI服务生成议程
            String agenda = deepSeekApiClient.generateAgenda(subject);

            // 验证内容长度
            if (agenda == null || agenda.trim().isEmpty()) {
                throw new AgendaGenerationException("生成的议程内容为空");
            }

            int length = agenda.trim().length();
            if (length < MIN_LENGTH || length > MAX_LENGTH) {
                throw new AgendaGenerationException(
                    String.format("生成的议程内容长度不符合要求（当前：%d字符，要求：%d-%d字符）", 
                        length, MIN_LENGTH, MAX_LENGTH)
                );
            }

            return agenda.trim();
        } catch (DeepSeekApiException e) {
            throw new AgendaGenerationException("AI服务调用失败: " + e.getMessage(), e);
        }
    }
}

