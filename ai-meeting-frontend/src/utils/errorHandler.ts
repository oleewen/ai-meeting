import { message } from 'antd';

/**
 * 错误处理工具
 */
export const errorHandler = {
  /**
   * 处理API错误
   */
  handleError(error: any) {
    if (error.response) {
      // 服务器返回错误
      const { status, data } = error.response;
      const errorMessage = data?.message || `请求失败: ${status}`;
      message.error(errorMessage);
    } else if (error.request) {
      // 请求已发出但没有收到响应
      message.error('网络错误，请检查网络连接');
    } else {
      // 其他错误
      message.error(error.message || '发生未知错误');
    }
  }
};
