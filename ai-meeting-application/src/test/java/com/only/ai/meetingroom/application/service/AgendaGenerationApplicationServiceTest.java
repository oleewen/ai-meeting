package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.infrastructure.call.DeepSeekApiClient;
import com.only.ai.meetingroom.infrastructure.call.DeepSeekApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 议程生成应用服务测试
 *
 * @author only
 * @since 2024-01-01
 */
@ExtendWith(MockitoExtension.class)
class AgendaGenerationApplicationServiceTest {

    @Mock
    private DeepSeekApiClient deepSeekApiClient;

    private AgendaGenerationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AgendaGenerationApplicationService(deepSeekApiClient);
    }

    @Test
    void testGenerateAgenda_Success() throws Exception {
        // 准备测试数据
        String subject = "产品规划会议";
        String expectedAgenda = "## 会议目标\n\n讨论下一季度产品规划\n\n## 主要议题\n\n1. 市场分析\n2. 产品需求\n3. 技术方案\n\n## 时间分配\n\n- 市场分析：30分钟\n- 产品需求：45分钟\n- 技术方案：30分钟\n\n## 预期成果\n\n确定产品规划方向";

        // Mock行为
        when(deepSeekApiClient.generateAgenda(subject)).thenReturn(expectedAgenda);

        // 执行测试
        String result = service.generateAgenda(subject);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedAgenda, result);
        verify(deepSeekApiClient, times(1)).generateAgenda(subject);
    }

    @Test
    void testGenerateAgenda_EmptySubject() throws DeepSeekApiException {
        // 执行测试并验证异常
        assertThrows(AgendaGenerationException.class, () -> {
            service.generateAgenda("");
        });

        assertThrows(AgendaGenerationException.class, () -> {
            service.generateAgenda(null);
        });

        // 验证未调用API
        verify(deepSeekApiClient, never()).generateAgenda(anyString());
    }

    @Test
    void testGenerateAgenda_ContentTooShort() throws DeepSeekApiException {
        // 准备测试数据 - 内容太短（少于50字符）
        String subject = "短会议";
        String shortAgenda = "简短议程"; // 只有8个字符

        // Mock行为
        when(deepSeekApiClient.generateAgenda(subject)).thenReturn(shortAgenda);

        // 执行测试并验证异常
        AgendaGenerationException exception = assertThrows(AgendaGenerationException.class, () -> {
            service.generateAgenda(subject);
        });

        assertTrue(exception.getMessage().contains("长度不符合要求"));
        verify(deepSeekApiClient, times(1)).generateAgenda(subject);
    }

    @Test
    void testGenerateAgenda_ContentTooLong() throws Exception {
        // 准备测试数据 - 内容太长（超过500字符）
        String subject = "长会议";
        StringBuilder longAgenda = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            longAgenda.append("a");
        }

        // Mock行为
        when(deepSeekApiClient.generateAgenda(subject)).thenReturn(longAgenda.toString());

        // 执行测试并验证异常
        AgendaGenerationException exception = assertThrows(AgendaGenerationException.class, () -> {
            service.generateAgenda(subject);
        });

        assertTrue(exception.getMessage().contains("长度不符合要求"));
    }

    @Test
    void testGenerateAgenda_ApiException() throws Exception {
        // 准备测试数据
        String subject = "测试会议";

        // Mock行为 - API抛出异常
        when(deepSeekApiClient.generateAgenda(subject))
                .thenThrow(new DeepSeekApiException("AI服务暂时不可用"));

        // 执行测试并验证异常
        AgendaGenerationException exception = assertThrows(AgendaGenerationException.class, () -> {
            service.generateAgenda(subject);
        });

        assertTrue(exception.getMessage().contains("AI服务调用失败"));
        assertNotNull(exception.getCause());
    }

    @Test
    void testGenerateAgenda_EmptyContent() throws Exception {
        // 准备测试数据 - API返回空内容
        String subject = "测试会议";

        // Mock行为
        when(deepSeekApiClient.generateAgenda(subject)).thenReturn("");

        // 执行测试并验证异常
        AgendaGenerationException exception = assertThrows(AgendaGenerationException.class, () -> {
            service.generateAgenda(subject);
        });

        assertTrue(exception.getMessage().contains("内容为空"));
    }

    @Test
    void testGenerateAgenda_ValidLength() throws Exception {
        // 准备测试数据 - 内容长度在有效范围内
        String subject = "产品规划会议";
        StringBuilder validAgenda = new StringBuilder();
        validAgenda.append("## 会议目标\n\n讨论产品规划\n\n");
        validAgenda.append("## 主要议题\n\n1. 议题一\n2. 议题二\n3. 议题三\n\n");
        validAgenda.append("## 时间分配\n\n各议题30分钟\n\n");
        validAgenda.append("## 预期成果\n\n确定规划方向");
        // 确保长度在50-500字符之间
        String agenda = validAgenda.toString();
        assertTrue(agenda.length() >= 50 && agenda.length() <= 500);

        // Mock行为
        when(deepSeekApiClient.generateAgenda(subject)).thenReturn(agenda);

        // 执行测试
        String result = service.generateAgenda(subject);

        // 验证结果
        assertNotNull(result);
        assertEquals(agenda.trim(), result);
    }
}

