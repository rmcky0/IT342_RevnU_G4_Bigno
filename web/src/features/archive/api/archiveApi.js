import api from "../../../shared/api/axios";

export const archiveApi = {
  getAllSummaries: async () => {
    const res = await api.get("/day/summaries");
    return res.data;
  },

  getDayDetail: async (date) => {
    const res = await api.get(`/day/detail/${date}`);
    return res.data;
  },
};
