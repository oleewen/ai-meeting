import React, { useState } from 'react';
import { Card, Form, DatePicker, TimePicker, Input, Select, Button, Space, Row, Col } from 'antd';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';
import MeetingRoomList from '../../components/MeetingRoomList';
import RoomSchedule from '../../components/RoomSchedule';
import { meetingRoomService, MeetingRoom } from '../../services/meetingRoomService';
import { errorHandler } from '../../utils/errorHandler';
import { useLoadingState } from '../../utils/loadingState';

const { Option } = Select;

/**
 * 会议室查询页面
 */
const QueryPage: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [rooms, setRooms] = useState<MeetingRoom[]>([]);
  const [selectedRoom, setSelectedRoom] = useState<MeetingRoom | null>(null);
  const [schedule, setSchedule] = useState<any>(null);
  const { loading, withLoading } = useLoadingState();

  const handleQuery = async (values: any) => {
    await withLoading(async () => {
      try {
        const params = {
          date: values.date.format('YYYY-MM-DD'),
          startTime: values.startTime.format('HH:mm'),
          endTime: values.endTime.format('HH:mm'),
          location: values.location,
          minCapacity: values.minCapacity,
          equipment: values.equipment
        };
        const data = await meetingRoomService.queryAvailableRooms(params);
        setRooms(data);
      } catch (error) {
        errorHandler.handleError(error);
      }
    });
  };

  const handleViewSchedule = async (roomId: number) => {
    const formValues = form.getFieldsValue();
    if (!formValues.date) {
      errorHandler.handleError({ message: '请先选择日期' });
      return;
    }
    try {
      const data = await meetingRoomService.getRoomSchedule(
        roomId,
        formValues.date.format('YYYY-MM-DD')
      );
      setSchedule(data);
      setSelectedRoom(rooms.find(r => r.id === roomId) || null);
    } catch (error) {
      errorHandler.handleError(error);
    }
  };

  const handleSelectRoom = (room: MeetingRoom) => {
    // 将会议室信息存储到localStorage，供BookingPage使用
    localStorage.setItem('selectedRoom', JSON.stringify(room));
    navigate(`/booking?roomId=${room.id}`);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card title="查询可用会议室" style={{ marginBottom: '24px' }}>
        <Form
          form={form}
          layout="inline"
          onFinish={handleQuery}
          initialValues={{
            date: dayjs(),
            startTime: dayjs('09:00', 'HH:mm'),
            endTime: dayjs('10:00', 'HH:mm')
          }}
        >
          <Form.Item
            name="date"
            label="日期"
            rules={[{ required: true, message: '请选择日期' }]}
          >
            <DatePicker />
          </Form.Item>

          <Form.Item
            name="startTime"
            label="开始时间"
            rules={[{ required: true, message: '请选择开始时间' }]}
          >
            <TimePicker format="HH:mm" />
          </Form.Item>

          <Form.Item
            name="endTime"
            label="结束时间"
            rules={[{ required: true, message: '请选择结束时间' }]}
          >
            <TimePicker format="HH:mm" />
          </Form.Item>

          <Form.Item name="location" label="地点">
            <Select placeholder="选择地点" style={{ width: 120 }} allowClear>
              <Option value="1楼">1楼</Option>
              <Option value="2楼">2楼</Option>
              <Option value="3楼">3楼</Option>
            </Select>
          </Form.Item>

          <Form.Item name="minCapacity" label="最小容量">
            <Input type="number" placeholder="人数" style={{ width: 100 }} />
          </Form.Item>

          <Form.Item name="equipment" label="设备">
            <Select
              mode="multiple"
              placeholder="选择设备"
              style={{ width: 200 }}
              allowClear
            >
              <Option value="投影仪">投影仪</Option>
              <Option value="视频会议">视频会议</Option>
              <Option value="白板">白板</Option>
              <Option value="音响">音响</Option>
            </Select>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              查询
            </Button>
          </Form.Item>
        </Form>
      </Card>

      <Row gutter={16}>
        <Col span={schedule ? 16 : 24}>
          <Card title="可用会议室列表">
            <MeetingRoomList
              rooms={rooms}
              onSelectRoom={handleSelectRoom}
              onViewSchedule={handleViewSchedule}
            />
          </Card>
        </Col>
        {schedule && (
          <Col span={8}>
            <RoomSchedule schedule={schedule} />
          </Col>
        )}
      </Row>
    </div>
  );
};

export default QueryPage;
