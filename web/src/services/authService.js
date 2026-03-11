import api from '../api/axios';

export const authService = {
  login: async ({ email, password }) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },

  register: async ({ fullName, email, password }) => {
    const response = await api.post('/auth/register', { fullName, email, password });
    return response.data;
  },

  linkGoogle: async ({ email, password, googleId }) => {
    const response = await api.post('/auth/link-google', { email, password, googleId });
    return response.data;
  },

  getSession: () => {
    const raw = sessionStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  },

  saveSession: (user) => {
    sessionStorage.setItem('user', JSON.stringify(user));
  },

  clearSession: () => {
    sessionStorage.removeItem('user');
  },
};
