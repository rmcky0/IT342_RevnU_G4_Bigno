import api from '../api/axios';

export const salesAPI = {
  createSale: async (saleData) => {
    const response = await api.post('/sales', saleData);
    return response.data;
  },

  getAllSales: async () => {
    const response = await api.get('/sales');
    return response.data;
  },

  getSalesById: async (id) => {
    const response = await api.get(`/sales/${id}`);
    return response.data;
  },

  getSalesByDateRange: async (startDate, endDate) => {
    const response = await api.get('/sales/range', {
      params: { startDate, endDate }
    });
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
};

export const expensesAPI = {
  createExpense: async (expenseData) => {
    const response = await api.post('/expenses', expenseData);
    return response.data;
  },

  getAllExpenses: async () => {
    const response = await api.get('/expenses');
    return response.data;
  },

  getExpenseById: async (id) => {
    const response = await api.get(`/expenses/${id}`);
    return response.data;
  },

  getExpensesByDateRange: async (startDate, endDate) => {
    const response = await api.get('/expenses/range', {
      params: { startDate, endDate }
    });
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

export const staffAPI = {
  createStaff: async (staffData) => {
    const response = await api.post('/staff', staffData);
    return response.data;
  },

  getAllStaff: async () => {
    const response = await api.get('/staff');
    return response.data;
  },

  getStaffById: async (id) => {
    const response = await api.get(`/staff/${id}`);
    return response.data;
  },

  updateStaff: async (id, staffData) => {
    const response = await api.put(`/staff/${id}`, staffData);
    return response.data;
  },

  deleteStaff: async (id) => {
    const response = await api.delete(`/staff/${id}`);
    return response.data;
  },
};

export const analyticsAPI = {
  getDailySummary: async (date = new Date().toISOString().split('T')[0]) => {
    const response = await api.get('/analytics/summary', {
      params: { date }
    });
    return response.data;
  },

  getSalesTrend: async (startDate, endDate) => {
    const response = await api.get('/analytics/sales-trend', {
      params: { startDate, endDate }
    });
    return response.data;
  },

  getSalesByCategory: async (date = new Date().toISOString().split('T')[0]) => {
    const response = await api.get('/analytics/sales-by-category', {
      params: { date }
    });
    return response.data;
  },

  getSalesSummary: async (startDate, endDate) => {
    const response = await api.get('/analytics/sales-summary', {
      params: { startDate, endDate }
    });
    return response.data;
  },

  getProfitTrend: async (startDate, endDate) => {
    const response = await api.get('/analytics/profit-trend', {
      params: { startDate, endDate }
    });
    return response.data;
  },
};
