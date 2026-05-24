import api from "../../../shared/api/axios";

export const categoriesAPI = {
  getCategories: async (type) => {
    const params = type ? { type } : {};
    const res = await api.get("/categories", { params });
    return res.data.data ?? [];
  },

  createCategory: async (data) => {
    const res = await api.post("/categories", data);
    return res.data.data;
  },

  updateCategory: async (id, data) => {
    const res = await api.put(`/categories/${id}`, data);
    return res.data.data;
  },

  deleteCategory: async (id) => {
    await api.delete(`/categories/${id}`);
  },
};
