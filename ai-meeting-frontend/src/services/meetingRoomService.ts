import axios from 'axios';

const API_BASE_URL = '/api';

export interface MeetingRoom {
  id: number;
  name: string;
  location: string;
  capacity: number;
  equipment: string[];
  status: string;
  description?: string;
}

export interface TimeSlot {
  startTime: string;
  endTime: string;
  status: string;
  bookingId?: number;
  subject?: string;
}

export interface RoomSchedule {
  roomId: number;
  roomName: string;
  date: string;
  timeSlots: TimeSlot[];
}

export interface QueryRoomsParams {
  date: string;
  startTime: string;
  endTime: string;
  location?: string;
  minCapacity?: number;
  equipment?: string[];
}

/**
 * 会议室服务
 */
export const meetingRoomService = {
  /**
   * 查询可用会议室
   */
  async queryAvailableRooms(params: QueryRoomsParams): Promise<MeetingRoom[]> {
    const queryParams = new URLSearchParams();
    queryParams.append('date', params.date);
    queryParams.append('startTime', params.startTime);
    queryParams.append('endTime', params.endTime);
    if (params.location) {
      queryParams.append('location', params.location);
    }
    if (params.minCapacity) {
      queryParams.append('minCapacity', params.minCapacity.toString());
    }
    if (params.equipment && params.equipment.length > 0) {
      params.equipment.forEach(eq => queryParams.append('equipment', eq));
    }

    const response = await axios.get(`${API_BASE_URL}/meeting-rooms?${queryParams.toString()}`);
    return response.data.data || [];
  },

  /**
   * 查看会议室当日排期
   */
  async getRoomSchedule(roomId: number, date: string): Promise<RoomSchedule> {
    const response = await axios.get(`${API_BASE_URL}/meeting-rooms/${roomId}/schedule`, {
      params: { date }
    });
    return response.data.data;
  }
};
