import React, { useState } from 'react';
import { Form, Input, DatePicker, TimePicker, InputNumber, Button, message, Space } from 'antd';
import dayjs from 'dayjs';
import { MeetingRoom } from '../../services/meetingRoomService';
import { bookingService, CreateBookingRequest } from '../../services/bookingService';
import { errorHandler } from '../../utils/errorHandler';

interface BookingFormProps {
  room: MeetingRoom;
  onSuccess?: () => void;
  onCancel?: () => void;
}

/**
 * 预约表单组件
 */
const BookingForm: React.FC<BookingFormProps> = ({ room, onSuccess, onCancel }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const request: CreateBookingRequest = {
        roomId: room.id,
        date: values.date.format('YYYY-MM-DD'),
        startTime: values.startTime.format('HH:mm'),
        endTime: values.endTime.format('HH:mm'),
        subject: values.subject,
        attendeeCount: values.attendeeCount,
        remark: values.remark
      };

      await bookingService.createBooking(request);
      message.success('预约成功！');
      form.resetFields();
      onSuccess && onSuccess();
    } catch (error) {
      errorHandler.handleError(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      initialValues={{
        date: dayjs(),
        attendeeCount: 1
      }}
    >
      <Form.Item label="会议室">
        <Input value={room.name} disabled />
      </Form.Item>

      <Form.Item
        name="date"
        label="预约日期"
        rules={[{ required: true, message: '请选择预约日期' }]}
      >
        <DatePicker 
          style={{ width: '100%' }}
          disabledDate={(current) => current && current < dayjs().startOf('day')}
        />
      </Form.Item>

      <Form.Item
        name="startTime"
        label="开始时间"
        rules={[{ required: true, message: '请选择开始时间' }]}
      >
        <TimePicker style={{ width: '100%' }} format="HH:mm" />
      </Form.Item>

      <Form.Item
        name="endTime"
        label="结束时间"
        rules={[{ required: true, message: '请选择结束时间' }]}
      >
        <TimePicker style={{ width: '100%' }} format="HH:mm" />
      </Form.Item>

      <Form.Item
        name="subject"
        label="会议主题"
        rules={[
          { required: true, message: '请输入会议主题' },
          { max: 200, message: '会议主题长度不能超过200字符' }
        ]}
      >
        <Input placeholder="请输入会议主题" />
      </Form.Item>

      <Form.Item
        name="attendeeCount"
        label="参会人数"
        rules={[
          { required: true, message: '请输入参会人数' },
          { type: 'number', min: 1, max: room.capacity, message: `参会人数必须在1-${room.capacity}之间` }
        ]}
      >
        <InputNumber min={1} max={room.capacity} style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item
        name="remark"
        label="备注"
        rules={[{ max: 500, message: '备注长度不能超过500字符' }]}
      >
        <Input.TextArea rows={4} placeholder="请输入备注（可选）" />
      </Form.Item>

      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit" loading={loading}>
            提交预约
          </Button>
          {onCancel && (
            <Button onClick={onCancel}>取消</Button>
          )}
        </Space>
      </Form.Item>
    </Form>
  );
};

export default BookingForm;
