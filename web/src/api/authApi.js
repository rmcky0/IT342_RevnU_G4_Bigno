import api from "./axios";

export const registerUser = async (data) => {
  const response = await api.post("/auth/register", {
    fullName: data.fullName,
    email: data.email,
    password: data.password,
  });
  return response.data;
};

export const loginUser = async (data) => {
  const response = await api.post("/auth/login", {
    email: data.email,
    password: data.password,
  });
  return response.data;
};
