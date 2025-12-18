package com.only.ai.meeting.web.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * API响应时间监控配置
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Component
public class MetricsConfig implements Filter {

    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            requestCount.incrementAndGet();
            totalResponseTime.addAndGet(duration);
            
            // 可以在这里记录P95延迟等指标
            // 简化实现：仅记录总请求数和总响应时间
        }
    }

    /**
     * 获取平均响应时间
     */
    public double getAverageResponseTime() {
        long count = requestCount.get();
        if (count == 0) {
            return 0;
        }
        return (double) totalResponseTime.get() / count;
    }

    /**
     * 获取总请求数
     */
    public long getRequestCount() {
        return requestCount.get();
    }
}
