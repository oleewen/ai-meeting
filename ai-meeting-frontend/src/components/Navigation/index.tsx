import React from 'react';
import { Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';

/**
 * 导航组件
 */
const Navigation: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/query',
      label: '查询会议室'
    },
    {
      key: '/my-bookings',
      label: '我的预定'
    }
  ];

  const handleMenuClick = (e: any) => {
    navigate(e.key);
  };

  return (
    <Menu
      mode="horizontal"
      selectedKeys={[location.pathname]}
      items={menuItems}
      onClick={handleMenuClick}
      style={{ marginBottom: '24px' }}
    />
  );
};

export default Navigation;
