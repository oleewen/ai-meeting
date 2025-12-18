import React from 'react';
import { List, Card, Tag, Button, Space } from 'antd';
import { MeetingRoom } from '../../services/meetingRoomService';

interface MeetingRoomListProps {
  rooms: MeetingRoom[];
  onSelectRoom?: (room: MeetingRoom) => void;
  onViewSchedule?: (roomId: number) => void;
}

/**
 * 会议室列表组件
 */
const MeetingRoomList: React.FC<MeetingRoomListProps> = ({ 
  rooms, 
  onSelectRoom, 
  onViewSchedule 
}) => {
  if (rooms.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <p>暂无可用会议室</p>
      </div>
    );
  }

  return (
    <List
      grid={{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 3, xxl: 4 }}
      dataSource={rooms}
      renderItem={(room) => (
        <List.Item>
          <Card
            title={room.name}
            extra={<Tag color={room.status === 'AVAILABLE' ? 'green' : 'red'}>{room.status}</Tag>}
            actions={[
              <Button 
                type="link" 
                onClick={() => onViewSchedule && onViewSchedule(room.id)}
              >
                查看排期
              </Button>,
              <Button 
                type="primary" 
                onClick={() => onSelectRoom && onSelectRoom(room)}
              >
                预约
              </Button>
            ]}
          >
            <Space direction="vertical" style={{ width: '100%' }}>
              <div><strong>地点:</strong> {room.location}</div>
              <div><strong>容量:</strong> {room.capacity}人</div>
              {room.equipment && room.equipment.length > 0 && (
                <div>
                  <strong>设备:</strong>{' '}
                  {room.equipment.map((eq, index) => (
                    <Tag key={index}>{eq}</Tag>
                  ))}
                </div>
              )}
              {room.description && (
                <div><strong>描述:</strong> {room.description}</div>
              )}
            </Space>
          </Card>
        </List.Item>
      )}
    />
  );
};

export default MeetingRoomList;
