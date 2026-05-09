import api from "../../../shared/api/axios";

export const notificationsApi = {
  async getNotifications(limit = 15) {
    const res = await api.get("/notifications", { params: { limit } });
    return res.data?.data ?? [];
  },

  async getUnreadCount() {
    const res = await api.get("/notifications/unread-count");
    return res.data?.data ?? 0;
  },

  async markRead(id) {
    const res = await api.patch(`/notifications/${id}/read`);
    return res.data?.data ?? null;
  },

  async markAllRead() {
    const res = await api.patch("/notifications/read-all");
    return res.data?.data ?? 0;
  },

  async createHolidayAlert(payload) {
    const res = await api.post("/notifications/holiday", payload);
    return res.data?.data ?? null;
  },

  async createSystemAlert(payload) {
    const res = await api.post("/notifications/system", payload);
    return res.data?.data ?? null;
  },
};
