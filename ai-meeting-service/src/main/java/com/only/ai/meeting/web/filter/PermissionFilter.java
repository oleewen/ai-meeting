package com.only.ai.meeting.web.filter;

import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限验证过滤器
 * 确保用户只能访问和操作自己的预约
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Component
public class PermissionFilter implements Filter {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 检查是否是预约相关的操作（需要权限验证）
        if (path.startsWith("/api/bookings/") && 
            (method.equals("GET") || method.equals("DELETE") || method.equals("POST"))) {
            
            String userId = (String) httpRequest.getAttribute("currentUserId");
            if (userId == null) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 从路径中提取bookingId
            String[] pathParts = path.split("/");
            if (pathParts.length >= 4) {
                try {
                    Long bookingId = Long.parseLong(pathParts[3]);
                    Booking booking = bookingRepository.findById(bookingId);
                    
                    if (booking != null && !booking.getUserId().equals(userId)) {
                        // 权限验证失败
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // bookingId格式错误，继续处理
                }
            }
        }

        chain.doFilter(request, response);
    }
}
