import { useCallback, useEffect, useRef, useState } from "react";
import { notificationsApi } from "../api/notificationsApi";
import { API_BASE_URL } from "../../../shared/api/axios";

const formatRelativeTime = (timestamp) => {
  if (!timestamp) return "";
  const now = new Date();
  const date = new Date(timestamp);
  const diffSeconds = Math.floor((now - date) / 1000);

  if (diffSeconds < 60) return "Just now";
  if (diffSeconds < 3600) return `${Math.floor(diffSeconds / 60)}m ago`;
  if (diffSeconds < 86400) return `${Math.floor(diffSeconds / 3600)}h ago`;

  return date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
  });
};

export const useNotifications = ({ limit = 15 } = {}) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const holidayKeyRef = useRef(null);

  const loadNotifications = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [items, count] = await Promise.all([
        notificationsApi.getNotifications(limit),
        notificationsApi.getUnreadCount(),
      ]);
      setNotifications(items);
      setUnreadCount(Number(count) || 0);
    } catch (err) {
      setError(
        err?.response?.data?.error?.message ?? "Failed to load notifications.",
      );
    } finally {
      setLoading(false);
    }
  }, [limit]);

  useEffect(() => {
    loadNotifications();
  }, [loadNotifications]);

  useEffect(() => {
    const token = sessionStorage.getItem("token");
    if (!token) return undefined;

    const streamUrl = `${API_BASE_URL}/notifications/stream?token=${encodeURIComponent(
      token,
    )}`;

    const source = new EventSource(streamUrl);

    const handleNotification = (event) => {
      try {
        const payload = JSON.parse(event.data);
        if (!payload?.id) return;
        setNotifications((prev) => {
          const exists = prev.some((note) => note.id === payload.id);
          if (exists) return prev;
          return [payload, ...prev].slice(0, limit);
        });
        if (!payload.read) {
          setUnreadCount((count) => count + 1);
        }
      } catch (err) {
        console.error("Failed to parse notification event", err);
      }
    };

    source.addEventListener("notification", handleNotification);

    source.onerror = () => {
      console.warn("Notification stream error");
    };

    return () => {
      source.close();
    };
  }, [limit]);

  const markAllRead = useCallback(async () => {
    await notificationsApi.markAllRead();
    setNotifications((prev) => prev.map((note) => ({ ...note, read: true })));
    setUnreadCount(0);
  }, []);

  const markRead = useCallback(async (id) => {
    const updated = await notificationsApi.markRead(id);
    if (!updated) return;

    setNotifications((prev) =>
      prev.map((note) => (note.id === id ? updated : note)),
    );
    if (!updated.read) return;

    setUnreadCount((count) => Math.max(0, count - 1));
  }, []);

  const ensureHolidayAlert = useCallback(async (date, name) => {
    if (!date || !name) return;

    const key = `${date}:${name}`;
    if (holidayKeyRef.current === key) return;

    holidayKeyRef.current = key;
    try {
      await notificationsApi.createHolidayAlert({ date, name });
    } catch (err) {
      console.error("Failed to create holiday alert", err);
    }
  }, []);

  return {
    notifications: notifications.map((note) => ({
      ...note,
      relativeTime: formatRelativeTime(note.createdAt),
    })),
    unreadCount,
    loading,
    error,
    markAllRead,
    markRead,
    reload: loadNotifications,
    ensureHolidayAlert,
  };
};
