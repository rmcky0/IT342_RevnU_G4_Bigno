import api from "../../../shared/api/axios";

export const externalApi = {
  getHolidays: async (year) => {
    const res = await api.get("/external/holidays", { params: { year } });
    return res.data;
  },
};
