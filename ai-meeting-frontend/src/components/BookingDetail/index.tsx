import React from 'react';
import { Card, Descriptions, Tag } from 'antd';
import { Booking } from '../../services/bookingService';
import dayjs from 'dayjs';

interface BookingDetailProps {
  booking: Booking;
}

/**
 * 预约详情组件
 */
const BookingDetail: React.FC<BookingDetailProps> = ({ booking }) => {
  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      PENDING: { color: 'blue', text: '待开始' },
      SIGNED_IN: { color: 'green', text: '已签到' },
      COMPLETED: { color: 'default', text: '已结束' },
      CANCELLED: { color: 'red', text: '已取消' },
      NOT_SIGNED_IN: { color: 'orange', text: '未签到' }
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  return (
    <Card title="预约详情">
      <Descriptions column={1} bordered>
        <Descriptions.Item label="会议主题">{booking.subject}</Descriptions.Item>
        <Descriptions.Item label="会议室">{booking.roomName}</Descriptions.Item>
        <Descriptions.Item label="预约日期">{booking.date}</Descriptions.Item>
        <Descriptions.Item label="时间段">
          {booking.startTime} - {booking.endTime}
        </Descriptions.Item>
        <Descriptions.Item label="参会人数">{booking.attendeeCount}人</Descriptions.Item>
        <Descriptions.Item label="状态">{getStatusTag(booking.status)}</Descriptions.Item>
        {booking.remark && (
          <Descriptions.Item label="备注">{booking.remark}</Descriptions.Item>
        )}
        {booking.signInTime && (
          <Descriptions.Item label="签到时间">
            {dayjs(booking.signInTime).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
        )}
        <Descriptions.Item label="创建时间">
          {dayjs(booking.createTime).format('YYYY-MM-DD HH:mm:ss')}
        </Descriptions.Item>
      </Descriptions>
    </Card>
  );
};

export default BookingDetail;
