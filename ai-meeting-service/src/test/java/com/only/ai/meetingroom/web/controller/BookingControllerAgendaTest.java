package com.only.ai.meetingroom.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.only.ai.meetingroom.api.booking.request.AgendaSuggestionRequest;
import com.only.ai.meetingroom.api.booking.response.AgendaSuggestionResponse;
import com.only.ai.meetingroom.api.common.response.ApiResponse;
import com.only.ai.meetingroom.application.service.AgendaGenerationApplicationService;
import com.only.ai.meetingroom.application.service.AgendaGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 议程推荐API测试
 *
 * @author only
 * @since 2024-01-01
 */
@WebMvcTest(BookingController.class)
class BookingControllerAgendaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendaGenerationApplicationService agendaGenerationApplicationService;

    @MockBean
    private com.only.ai.meetingroom.application.service.BookingCreateApplicationService bookingCreateApplicationService;

    @MockBean
    private com.only.ai.meetingroom.application.service.BookingManagementApplicationService bookingManagementApplicationService;

    @MockBean
    private com.only.ai.meetingroom.domain.repository.RoomRepository roomRepository;

    private MockHttpSession session;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("userId", 1L);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGenerateAgendaSuggestion_Success() throws Exception {
        // 准备测试数据
        String subject = "产品规划会议";
        String expectedAgenda = "## 会议目标\n\n讨论下一季度产品规划\n\n## 主要议题\n\n1. 市场分析\n2. 产品需求\n3. 技术方案\n\n## 时间分配\n\n- 市场分析：30分钟\n- 产品需求：45分钟\n- 技术方案：30分钟\n\n## 预期成果\n\n确定产品规划方向";

        // Mock行为
        when(agendaGenerationApplicationService.generateAgenda(subject)).thenReturn(expectedAgenda);

        // 构建请求
        AgendaSuggestionRequest request = new AgendaSuggestionRequest();
        request.setSubject(subject);

        // 执行测试
        mockMvc.perform(post("/api/bookings/agenda-suggestion")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.agenda").value(expectedAgenda));
    }

    @Test
    void testGenerateAgendaSuggestion_EmptySubject() throws Exception {
        // 构建请求
        AgendaSuggestionRequest request = new AgendaSuggestionRequest();
        request.setSubject("");

        // 执行测试
        mockMvc.perform(post("/api/bookings/agenda-suggestion")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGenerateAgendaSuggestion_NotLoggedIn() throws Exception {
        // 构建请求
        AgendaSuggestionRequest request = new AgendaSuggestionRequest();
        request.setSubject("产品规划会议");

        // 执行测试（无session）
        mockMvc.perform(post("/api/bookings/agenda-suggestion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGenerateAgendaSuggestion_ServiceException() throws Exception {
        // 准备测试数据
        String subject = "产品规划会议";

        // Mock行为 - 服务抛出异常
        when(agendaGenerationApplicationService.generateAgenda(subject))
                .thenThrow(new AgendaGenerationException("AI服务暂时不可用"));

        // 构建请求
        AgendaSuggestionRequest request = new AgendaSuggestionRequest();
        request.setSubject(subject);

        // 执行测试
        mockMvc.perform(post("/api/bookings/agenda-suggestion")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AGENDA_GENERATION_FAILED"))
                .andExpect(jsonPath("$.message").value("AI服务暂时不可用"));
    }
}

