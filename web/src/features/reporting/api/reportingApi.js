import api from "../../../shared/api/axios";

export const reportingApi = {
  closeDay: async (date) => {
    const response = await api.post("/day/close", { date });
    return response.data;
  },

  getSummary: async (date) => {
    const response = await api.get(`/day/summary/${date}`);
    return response.data;
  },

  getAllSummaries: async () => {
    const response = await api.get("/day/summaries");
    return response.data;
  },

  getDayDetail: async (date) => {
    const response = await api.get(`/day/detail/${date}`);
    return response.data;
  },
};
