import api from "../../../shared/api/axios";

export const analyticsApi = {
  getDailyStats: async (date = null) => {
    const params = date ? { date } : {};
    const response = await api.get("/analytics/daily", { params });
    return response.data.data;
  },

  getProfitTrends: async (period = "daily") => {
    const response = await api.get("/analytics/trends", { params: { period } });
    return response.data.data;
  },
};
