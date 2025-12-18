import React, { useState } from 'react';
import { Card, Modal } from 'antd';
import MyBookings from '../../components/MyBookings';
import BookingDetail from '../../components/BookingDetail';
import { Booking } from '../../services/bookingService';

/**
 * 我的预约页面
 */
const MyBookingsPage: React.FC = () => {
  const [selectedBooking, setSelectedBooking] = useState<Booking | null>(null);
  const [detailVisible, setDetailVisible] = useState(false);

  const handleViewDetail = (booking: Booking) => {
    setSelectedBooking(booking);
    setDetailVisible(true);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card title="我的预定">
        <MyBookings onViewDetail={handleViewDetail} />
      </Card>

      <Modal
        title="预约详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={600}
      >
        {selectedBooking && <BookingDetail booking={selectedBooking} />}
      </Modal>
    </div>
  );
};

export default MyBookingsPage;
