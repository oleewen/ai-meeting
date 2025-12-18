package com.only.ai.meeting.job;

import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.BookingStatus;
import com.only.ai.meeting.domain.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约状态更新定时任务
 * 每分钟检查并更新已结束的预约状态
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Component
public class BookingStatusUpdateJob {

    private static final Logger logger = LoggerFactory.getLogger(BookingStatusUpdateJob.class);

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * 每分钟执行一次，更新已结束的预约状态
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updateCompletedBookings() {
        try {
            LocalDateTime now = LocalDateTime.now();
            // 查找所有状态为PENDING或SIGNED_IN且结束时间已过的预约
            List<Booking> completedBookings = bookingRepository.findCompletedBookings(now);
            
            for (Booking booking : completedBookings) {
                if (booking.getStatus() == BookingStatus.PENDING) {
                    // 如果会议结束但未签到，标记为未签到
                    booking.setStatus(BookingStatus.NOT_SIGNED_IN);
                } else if (booking.getStatus() == BookingStatus.SIGNED_IN) {
                    // 如果已签到，标记为已结束
                    booking.setStatus(BookingStatus.COMPLETED);
                }
                bookingRepository.updateStatus(booking.getId(), booking.getStatus());
            }
            
            if (!completedBookings.isEmpty()) {
                logger.info("定时任务更新了{}条预约状态", completedBookings.size());
            }
        } catch (Exception e) {
            logger.error("更新预约状态失败", e);
        }
    }
}
