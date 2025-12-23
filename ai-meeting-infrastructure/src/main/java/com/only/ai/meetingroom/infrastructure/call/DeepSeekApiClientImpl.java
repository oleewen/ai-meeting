package com.only.ai.meetingroom.infrastructure.call;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek大模型API客户端实现
 * 使用OpenAI兼容的API接口
 *
 * @author only
 * @since 2024-01-01
 */
@Component
public class DeepSeekApiClientImpl implements DeepSeekApiClient {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekApiClientImpl.class);
    
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String PROMPT_TEMPLATE = 
        "请根据以下会议主题，生成一份结构化的会议议程，使用Markdown格式。\n" +
        "议程应包含以下部分：\n" +
        "1. 会议目标\n" +
        "2. 主要议题（3-5个）\n" +
        "3. 时间分配\n" +
        "4. 预期成果\n\n" +
        "会议主题：%s\n\n" +
        "请生成专业的会议议程，内容要简洁明了，总长度控制在500字符以内。";

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String model;

    @Value("${deepseek.api.timeout:30}")
    private int timeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DeepSeekApiClientImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warn("DeepSeek API密钥未配置，AI生成议程功能将不可用");
        } else {
            logger.info("DeepSeek API客户端初始化成功，模型: {}", model);
        }
    }

    @Override
    public String generateAgenda(String subject) throws DeepSeekApiException {
        if (subject == null || subject.trim().isEmpty()) {
            throw new DeepSeekApiException("会议主题不能为空");
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new DeepSeekApiException("AI服务未配置，请检查API密钥配置");
        }

        try {
            // 构建提示词
            String prompt = String.format(PROMPT_TEMPLATE, subject);

            // 构建请求体（OpenAI兼容格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);
            
            // 设置参数
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);
            requestBody.put("stream", false);

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            logger.debug("调用DeepSeek API，主题: {}, 模型: {}", subject, model);

            // 发送请求
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
                );
            } catch (ResourceAccessException e) {
                // 超时或网络错误
                throw new DeepSeekApiException("AI服务请求超时，请稍后重试", e);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                // HTTP错误
                String errorMessage = "AI服务调用失败";
                if (e.getResponseBodyAsString() != null) {
                    try {
                        JsonNode errorNode = objectMapper.readTree(e.getResponseBodyAsString());
                        if (errorNode.has("error") && errorNode.get("error").has("message")) {
                            errorMessage += ": " + errorNode.get("error").get("message").asText();
                        }
                    } catch (Exception parseEx) {
                        errorMessage += ": " + e.getMessage();
                    }
                } else {
                    errorMessage += ": " + e.getMessage();
                }
                throw new DeepSeekApiException(errorMessage, e);
            }

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                // OpenAI兼容格式: {"choices": [{"message": {"content": "..."}}]}
                JsonNode choices = jsonNode.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode firstChoice = choices.get(0);
                    JsonNode messageNode = firstChoice.path("message");
                    String content = messageNode.path("content").asText();
                    
                    if (content != null && !content.trim().isEmpty()) {
                        logger.debug("AI生成议程成功，长度: {}", content.length());
                        return content.trim();
                    }
                }
                throw new DeepSeekApiException("AI服务返回的内容为空");
            } else {
                throw new DeepSeekApiException("AI服务返回异常状态: " + response.getStatusCode());
            }

        } catch (DeepSeekApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("AI服务调用异常", e);
            throw new DeepSeekApiException("AI服务调用异常: " + e.getMessage(), e);
        }
    }
}

