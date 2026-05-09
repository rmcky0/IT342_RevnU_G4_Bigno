import api from "../../../shared/api/axios";

export const salesAPI = {
  getAllSales: async (page = 0, size = 9) => {
    const response = await api.get("/sales", { params: { page, size } });
    return response.data;
  },

  createSale: async (saleData) => {
    const response = await api.post("/sales", saleData);
    return response.data;
  },

  updateSale: async (id, saleData) => {
    const response = await api.put(`/sales/${id}`, saleData);
    return response.data;
  },

  deleteSale: async (id) => {
    const response = await api.delete(`/sales/${id}`);
    return response.data;
  },

  getSalesByDate: async (date) => {
    const response = await api.get("/sales/date", { params: { date } });
    return response.data;
  },
};
