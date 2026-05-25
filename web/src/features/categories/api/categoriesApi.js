import api from "../../../shared/api/axios";

export const categoriesAPI = {
  getCategories: async (type) => {
    const params = type ? { type } : {};
    const res = await api.get("/categories", { params });
    return res.data.data ?? [];
  },
};
