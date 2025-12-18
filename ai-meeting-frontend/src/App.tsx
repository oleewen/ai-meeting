import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import 'antd/dist/reset.css';
import './App.css';
import Navigation from './components/Navigation';
import QueryPage from './pages/QueryPage';
import BookingPage from './pages/BookingPage';
import MyBookingsPage from './pages/MyBookingsPage';

/**
 * 主应用组件
 */
function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <div className="App">
          <Navigation />
          <Routes>
            <Route path="/" element={<QueryPage />} />
            <Route path="/query" element={<QueryPage />} />
            <Route path="/booking" element={<BookingPage />} />
            <Route path="/my-bookings" element={<MyBookingsPage />} />
          </Routes>
        </div>
      </Router>
    </ConfigProvider>
  );
}

export default App;
