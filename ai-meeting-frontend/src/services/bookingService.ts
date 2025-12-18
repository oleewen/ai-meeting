import axios from 'axios';

const API_BASE_URL = '/api';

export interface Booking {
  id: number;
  userId: string;
  userName: string;
  roomId: number;
  roomName: string;
  date: string;
  startTime: string;
  endTime: string;
  subject: string;
  attendeeCount: number;
  remark?: string;
  status: string;
  signInTime?: string;
  createTime: string;
}

export interface CreateBookingRequest {
  roomId: number;
  date: string;
  startTime: string;
  endTime: string;
  subject: string;
  attendeeCount: number;
  remark?: string;
}

export interface QueryMyBookingsParams {
  startDate?: string;
  endDate?: string;
  status?: string;
}

/**
 * 预约服务
 */
export const bookingService = {
  /**
   * 创建预约
   */
  async createBooking(request: CreateBookingRequest): Promise<Booking> {
    const response = await axios.post(`${API_BASE_URL}/bookings`, request, {
      headers: {
        'X-User-Id': 'user001' // TODO: 从认证信息获取
      }
    });
    return response.data.data;
  },

  /**
   * 查询我的预约
   */
  async getMyBookings(params?: QueryMyBookingsParams): Promise<Booking[]> {
    const queryParams = new URLSearchParams();
    if (params?.startDate) {
      queryParams.append('startDate', params.startDate);
    }
    if (params?.endDate) {
      queryParams.append('endDate', params.endDate);
    }
    if (params?.status) {
      queryParams.append('status', params.status);
    }

    const response = await axios.get(`${API_BASE_URL}/bookings?${queryParams.toString()}`, {
      headers: {
        'X-User-Id': 'user001' // TODO: 从认证信息获取
      }
    });
    return response.data.data || [];
  },

  /**
   * 查看预约详情
   */
  async getBookingDetail(bookingId: number): Promise<Booking> {
    const response = await axios.get(`${API_BASE_URL}/bookings/${bookingId}`, {
      headers: {
        'X-User-Id': 'user001' // TODO: 从认证信息获取
      }
    });
    return response.data.data;
  },

  /**
   * 取消预约
   */
  async cancelBooking(bookingId: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/bookings/${bookingId}`, {
      headers: {
        'X-User-Id': 'user001' // TODO: 从认证信息获取
      }
    });
  },

  /**
   * 会议签到
   */
  async signIn(bookingId: number): Promise<Booking> {
    const response = await axios.post(`${API_BASE_URL}/bookings/${bookingId}/sign-in`, {}, {
      headers: {
        'X-User-Id': 'user001' // TODO: 从认证信息获取
      }
    });
    return response.data.data;
  }
};
