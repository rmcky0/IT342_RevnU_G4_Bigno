import axios from "axios";

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1/revnu";

const api = axios.create({
  baseURL: `${API_BASE_URL}/`,
});

const refreshApi = axios.create({
  baseURL: `${API_BASE_URL}/`,
});

api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("token");
    console.log(
      "Interceptor sending token:",
      token ? "Token exists!" : "NO TOKEN FOUND",
    );
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const isUnauthorized = error.response?.status === 401;
    const isAuthEndpoint = originalRequest?.url?.includes("/auth/login") ||
      originalRequest?.url?.includes("/auth/register") ||
      originalRequest?.url?.includes("/auth/refresh");

    if (isUnauthorized && !originalRequest?._retry && !isAuthEndpoint) {
      originalRequest._retry = true;
      const refreshToken = sessionStorage.getItem("refreshToken");

      if (refreshToken) {
        try {
          const refreshResponse = await refreshApi.post("/auth/refresh", {
            refreshToken,
          });
          const payload = refreshResponse?.data?.data;
          const newAccessToken = payload?.accessToken;
          const newRefreshToken = payload?.refreshToken;

          if (newAccessToken) {
            sessionStorage.setItem("token", newAccessToken);
            window.dispatchEvent(new Event("revnu-auth-token-changed"));
            if (newRefreshToken) {
              sessionStorage.setItem("refreshToken", newRefreshToken);
            }
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return api(originalRequest);
          }
        } catch (refreshError) {
          console.warn("Refresh token failed. Logging out...");
        }
      }

      sessionStorage.removeItem("token");
      sessionStorage.removeItem("refreshToken");
      sessionStorage.removeItem("user");
      window.dispatchEvent(new Event("revnu-auth-token-changed"));
      window.location.href = "/login";
    }

    return Promise.reject(error);
  },
);

export default api;
