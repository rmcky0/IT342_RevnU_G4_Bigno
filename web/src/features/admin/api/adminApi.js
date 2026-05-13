import api from "../../../shared/api/axios";

export const adminApi = {
  normalizeUser: (user) => ({
    ...user,
    suspended: user?.status === "SUSPENDED",
  }),
  normalizeRestaurant: (restaurant) => ({
    ...restaurant,
    physicalLocation:
      restaurant?.physicalLocation ?? restaurant?.location ?? "",
    ownerSuspended: restaurant?.ownerStatus === "SUSPENDED",
  }),

  getStats: async () => {
    const res = await api.get("/admin/stats");
    return res.data.data;
  },

  getAllUsers: async () => {
    const res = await api.get("/admin/users");
    const payload = res.data.data;
    const list = Array.isArray(payload) ? payload : (payload?.content ?? []);
    return list.map((user) => adminApi.normalizeUser(user));
  },
  suspendUser: async (id) => {
    const res = await api.put(`/admin/users/${id}/status`, {
      status: "SUSPENDED",
    });
    return adminApi.normalizeUser(res.data.data);
  },
  activateUser: async (id) => {
    const res = await api.put(`/admin/users/${id}/status`, {
      status: "ACTIVE",
    });
    return adminApi.normalizeUser(res.data.data);
  },
  promoteToAdmin: async (id) => {
    const res = await api.put(`/admin/users/${id}/role`, { role: "ADMIN" });
    return adminApi.normalizeUser(res.data.data);
  },
  demoteToTenant: async (id) => {
    const res = await api.put(`/admin/users/${id}/role`, { role: "TENANT" });
    return adminApi.normalizeUser(res.data.data);
  },
  deleteUser: async (id) => {
    await api.delete(`/admin/users/${id}`);
  },

  getAllRestaurants: async () => {
    const res = await api.get("/admin/restaurants");
    const payload = res.data.data;
    const list = Array.isArray(payload) ? payload : (payload?.content ?? []);
    return list.map((restaurant) => adminApi.normalizeRestaurant(restaurant));
  },
};
