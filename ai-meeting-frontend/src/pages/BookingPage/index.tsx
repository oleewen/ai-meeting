import React, { useState, useEffect } from 'react';
import { Card, message, Spin } from 'antd';
import { useSearchParams, useNavigate } from 'react-router-dom';
import BookingForm from '../../components/BookingForm';
import { meetingRoomService, MeetingRoom } from '../../services/meetingRoomService';
import { errorHandler } from '../../utils/errorHandler';

/**
 * 预约页面
 */
const BookingPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const roomId = searchParams.get('roomId');
  const [room, setRoom] = useState<MeetingRoom | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (roomId) {
      loadRoom();
    } else {
      message.error('缺少会议室信息');
      navigate('/query');
    }
  }, [roomId]);

  const loadRoom = async () => {
    setLoading(true);
    try {
      // 从localStorage获取会议室信息（由QueryPage传递）
      const savedRoom = localStorage.getItem('selectedRoom');
      if (savedRoom) {
        const roomData = JSON.parse(savedRoom);
        setRoom(roomData);
        localStorage.removeItem('selectedRoom'); // 使用后清除
      } else {
        // 如果没有保存的信息，使用默认值
        const defaultRoom: MeetingRoom = {
          id: parseInt(roomId || '1'),
          name: '会议室',
          location: '1楼',
          capacity: 10,
          equipment: [],
          status: 'AVAILABLE'
        };
        setRoom(defaultRoom);
      }
    } catch (error) {
      errorHandler.handleError(error);
      navigate('/query');
    } finally {
      setLoading(false);
    }
  };

  const handleSuccess = () => {
    navigate('/my-bookings');
  };

  const handleCancel = () => {
    navigate('/query');
  };

  if (loading || !room) {
    return (
      <div style={{ padding: '24px', textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
      <Card title="预约会议室">
        <BookingForm room={room} onSuccess={handleSuccess} onCancel={handleCancel} />
      </Card>
    </div>
  );
};

export default BookingPage;
