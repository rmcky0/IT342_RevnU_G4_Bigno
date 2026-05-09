import api from "../../../shared/api/axios";

export const restaurantApi = {
  setupRestaurant: async (restaurantData, logoFile) => {
    const formData = new FormData();

    const dataBlob = new Blob(
      [
        JSON.stringify({
          name: restaurantData.name,
          physicalLocation: restaurantData.physicalLocation,
          openingHrs: restaurantData.openingTime,
          closingHrs: restaurantData.closingTime,
        }),
      ],
      { type: "application/json" },
    );

    formData.append("data", dataBlob, "data.json");

    if (logoFile) {
      formData.append("logo", logoFile);
    }

    const response = await api.post("/settings/restaurant", formData);

    return response.data;
  },

  getRestaurantProfile: async () => {
    const response = await api.get("/settings/restaurant");
    return response.data;
  },
};
