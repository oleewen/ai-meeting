import React from 'react';
import { Timeline, Tag, Card, Space } from 'antd';
import { RoomSchedule, TimeSlot } from '../../services/meetingRoomService';

interface RoomScheduleProps {
  schedule: RoomSchedule;
}

/**
 * 会议室排期组件
 */
const RoomScheduleComponent: React.FC<RoomScheduleProps> = ({ schedule }) => {
  if (!schedule.timeSlots || schedule.timeSlots.length === 0) {
    return (
      <Card>
        <p>该会议室当日暂无预约</p>
      </Card>
    );
  }

  return (
    <Card title={`${schedule.roomName} - ${schedule.date} 排期`}>
      <Timeline>
        {schedule.timeSlots.map((slot: TimeSlot, index: number) => (
          <Timeline.Item
            key={index}
            color={slot.status === 'OCCUPIED' ? 'red' : 'green'}
          >
            <div>
              <Space>
                <span>
                  {slot.startTime} - {slot.endTime}
                </span>
                <Tag color={slot.status === 'OCCUPIED' ? 'red' : 'green'}>
                  {slot.status === 'OCCUPIED' ? '已占用' : '可用'}
                </Tag>
              </Space>
              {slot.status === 'OCCUPIED' && slot.subject && (
                <div style={{ marginTop: '4px', color: '#666' }}>
                  会议主题: {slot.subject}
                </div>
              )}
            </div>
          </Timeline.Item>
        ))}
      </Timeline>
    </Card>
  );
};

export default RoomScheduleComponent;
