import { useCallback, useEffect, useRef } from 'react';

export function useInactivityTimer(timeoutMs: number, onTimeout: () => void) {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const resetTimer = useCallback(() => {
    if (timerRef.current) {
      clearTimeout(timerRef.current);
    }

    timerRef.current = setTimeout(() => {
      onTimeout();
    }, timeoutMs);
  }, [onTimeout, timeoutMs]);

  useEffect(() => {
    resetTimer();

    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
    };
  }, [resetTimer]);

  return { resetTimer };
}
