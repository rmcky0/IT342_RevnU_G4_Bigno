import api from "../../../shared/api/axios";

export const salaryApi = {
  getSalaryHistory: async (page = 0, size = 10) => {
    const response = await api.get(`/salaries?page=${page}&size=${size}`);
    return response.data;
  },

  recordSalary: async (salaryData) => {
    const response = await api.post("/salaries", salaryData);
    return response.data;
  },
  updateSalary: async (id, salaryData) => {
    const response = await api.put(`/salaries/${id}`, salaryData);
    return response.data;
  },

  deleteSalary: async (id) => {
    const response = await api.delete(`/salaries/${id}`);
    return response.data;
  },
};
