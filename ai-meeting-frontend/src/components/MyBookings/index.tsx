import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, DatePicker, Select, Modal, message } from 'antd';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { Booking, bookingService } from '../../services/bookingService';
import { errorHandler } from '../../utils/errorHandler';
import { useLoadingState } from '../../utils/loadingState';

const { RangePicker } = DatePicker;
const { Option } = Select;

interface MyBookingsProps {
  onViewDetail?: (booking: Booking) => void;
}

/**
 * 我的预约列表组件
 */
const MyBookings: React.FC<MyBookingsProps> = ({ onViewDetail }) => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [startDate, setStartDate] = useState<Dayjs | null>(null);
  const [endDate, setEndDate] = useState<Dayjs | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const { loading, withLoading } = useLoadingState();

  useEffect(() => {
    loadBookings();
  }, [startDate, endDate, statusFilter]);

  const loadBookings = async () => {
    await withLoading(async () => {
      try {
        const params: any = {};
        if (startDate && endDate) {
          params.startDate = startDate.format('YYYY-MM-DD');
          params.endDate = endDate.format('YYYY-MM-DD');
        }
        if (statusFilter) {
          params.status = statusFilter;
        }
        const data = await bookingService.getMyBookings(params);
        setBookings(data);
      } catch (error) {
        errorHandler.handleError(error);
      }
    });
  };

  const handleCancel = async (booking: Booking) => {
    Modal.confirm({
      title: '确认取消预约',
      content: `确定要取消预约"${booking.subject}"吗？`,
      onOk: async () => {
        try {
          await bookingService.cancelBooking(booking.id);
          message.success('取消成功');
          loadBookings();
        } catch (error) {
          errorHandler.handleError(error);
        }
      }
    });
  };

  const handleSignIn = async (booking: Booking) => {
    try {
      await bookingService.signIn(booking.id);
      message.success('签到成功');
      loadBookings();
    } catch (error) {
      errorHandler.handleError(error);
    }
  };

  const canSignIn = (booking: Booking): boolean => {
    if (booking.status !== 'PENDING') {
      return false;
    }
    const now = dayjs();
    const meetingStart = dayjs(`${booking.date} ${booking.startTime}`);
    const signInStart = meetingStart.subtract(10, 'minute');
    const signInEnd = meetingStart.add(15, 'minute');
    return now.isAfter(signInStart) && now.isBefore(signInEnd);
  };

  const canCancel = (booking: Booking): boolean => {
    if (booking.status === 'CANCELLED' || booking.status === 'COMPLETED') {
      return false;
    }
    const now = dayjs();
    const meetingStart = dayjs(`${booking.date} ${booking.startTime}`);
    const minutesBeforeStart = meetingStart.diff(now, 'minute');
    return minutesBeforeStart >= 5;
  };

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

  const columns = [
    {
      title: '会议主题',
      dataIndex: 'subject',
      key: 'subject'
    },
    {
      title: '会议室',
      dataIndex: 'roomName',
      key: 'roomName'
    },
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date'
    },
    {
      title: '时间段',
      key: 'time',
      render: (_: any, record: Booking) => `${record.startTime} - ${record.endTime}`
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Booking) => (
        <Space>
          <Button type="link" onClick={() => onViewDetail && onViewDetail(record)}>
            查看详情
          </Button>
          {canSignIn(record) && (
            <Button type="link" onClick={() => handleSignIn(record)}>
              签到
            </Button>
          )}
          {canCancel(record) && (
            <Button type="link" danger onClick={() => handleCancel(record)}>
              取消
            </Button>
          )}
        </Space>
      )
    }
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <RangePicker
          onChange={(dates) => {
            if (dates) {
              setStartDate(dates[0]);
              setEndDate(dates[1]);
            } else {
              setStartDate(null);
              setEndDate(null);
            }
          }}
        />
        <Select
          placeholder="状态筛选"
          style={{ width: 150 }}
          allowClear
          onChange={(value) => setStatusFilter(value || '')}
        >
          <Option value="PENDING">待开始</Option>
          <Option value="SIGNED_IN">已签到</Option>
          <Option value="COMPLETED">已结束</Option>
          <Option value="CANCELLED">已取消</Option>
          <Option value="NOT_SIGNED_IN">未签到</Option>
        </Select>
        <Button onClick={loadBookings}>刷新</Button>
      </Space>

      <Table
        columns={columns}
        dataSource={bookings}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  );
};

export default MyBookings;
