import { useState, useCallback } from 'react';

/**
 * 加载状态管理Hook
 */
export function useLoadingState() {
  const [loading, setLoading] = useState(false);

  const withLoading = useCallback(async <T,>(asyncFn: () => Promise<T>): Promise<T | null> => {
    setLoading(true);
    try {
      return await asyncFn();
    } catch (error) {
      throw error;
    } finally {
      setLoading(false);
    }
  }, []);

  return { loading, setLoading, withLoading };
}
