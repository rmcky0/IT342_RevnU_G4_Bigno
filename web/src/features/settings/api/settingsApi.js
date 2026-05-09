import api from "../../../shared/api/axios";

export const settingsApi = {
  getProfile: async () => {
    const response = await api.get("/settings/profile");
    return response.data;
  },

  updateProfile: async (profileData) => {
    const response = await api.put("/settings/profile", profileData);
    return response.data;
  },

  updateProfilePicture: async (formData) => {
    const response = await api.patch("/settings/profile/avatar", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  },

  changePassword: async (currentPassword, newPassword) => {
    const response = await api.put("/settings/change-password", {
      currentPassword,
      newPassword,
    });
    return response.data;
  },

  getRestaurantProfile: async () => {
    const response = await api.get("/settings/restaurant");
    return response.data;
  },

  updateRestaurantProfile: async (restaurantData) => {
    const response = await api.put("/settings/restaurant", restaurantData);
    return response.data;
  },

  updateRestaurantLogo: async (formData) => {
    const response = await api.patch("/settings/restaurant/logo", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  },

  getSystemSettings: async () => {
    const response = await api.get("/settings/system");
    return response.data;
  },

  updateSystemSettings: async (settingsData) => {
    const response = await api.put("/settings/system", settingsData);
    return response.data;
  },
};
