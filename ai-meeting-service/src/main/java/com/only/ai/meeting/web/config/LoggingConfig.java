package com.only.ai.meeting.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求日志记录配置
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Component
public class LoggingConfig implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        long startTime = System.currentTimeMillis();
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String fullUrl = queryString == null ? uri : uri + "?" + queryString;

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("{} {} - {}ms", method, fullUrl, duration);
        }
    }
}
