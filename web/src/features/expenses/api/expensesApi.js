import api from "../../../shared/api/axios";

export const expensesAPI = {
  createExpense: async (expenseData) => {
    const response = await api.post("/expenses", expenseData);
    return response.data;
  },

  uploadReceipt: async (expenseId, file) => {
    const formData = new FormData();
    formData.append("file", file);
    const response = await api.post(`/expenses/${expenseId}/upload`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  },

  getAllExpenses: async (page = 0, size = 9) => {
    const response = await api.get("/expenses", { params: { page, size } });
    return response.data;
  },

  updateExpense: async (id, expenseData) => {
    const response = await api.put(`/expenses/${id}`, expenseData);
    return response.data;
  },

  deleteExpense: async (id) => {
    const response = await api.delete(`/expenses/${id}`);
    return response.data;
  },
};
