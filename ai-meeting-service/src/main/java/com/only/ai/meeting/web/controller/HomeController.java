package com.only.ai.meeting.web.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 首页控制器
 * 
 * @author AI Meeting Team
 * @since 2025-12-18
 */
@RestController
public class HomeController {

    /**
     * 首页 - 返回会议室查询预约页面
     * 直接返回HTML内容，确保样式正确加载
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        try {
            Resource resource = new ClassPathResource("static/index.html");
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(html);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}


