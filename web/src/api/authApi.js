import api from './axios';

/**
 * Register a new user.
 * @param {{ fullName: string, email: string, password: string }} data
 * @returns {Promise<{ message: string, email: string, fullName: string, role: string }>}
 */
export const registerUser = async (data) => {
  const response = await api.post('/auth/register', {
    fullName: data.fullName,
    email: data.email,
    password: data.password,
  });
  return response.data;
};

/**
 * Login an existing user.
 * @param {{ email: string, password: string }} data
 * @returns {Promise<{ message: string, email: string, fullName: string, role: string }>}
 */
export const loginUser = async (data) => {
  const response = await api.post('/auth/login', {
    email: data.email,
    password: data.password,
  });
  return response.data;
};
