import api from "../../../shared/api/axios";

export const authService = {
  login: async ({ email, password }) => {
    const response = await api.post("/auth/login", { email, password });
    return response.data;
  },

  register: async ({ fullname, email, password }) => {
    const response = await api.post("/auth/register", {
      fullname,
      email,
      password,
    });
    return response.data;
  },

  linkGoogle: async ({ email, password, googleId }) => {
    const response = await api.post("/auth/link-google", {
      email,
      password,
      googleId,
    });
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await api.get("/auth/me");
    return response.data;
  },
};
