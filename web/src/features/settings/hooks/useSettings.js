import { useState, useEffect } from "react";
import { settingsApi } from "../api/settingsApi";
import { useAuth } from "../../auth/context/AuthContext";
import { API_BASE_URL } from "../../../shared/api/axios";

export const useSettings = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: "", text: "" });

  const [personalData, setPersonalData] = useState({
    fullname: user?.fullname || "",
    email: user?.email || "",
  });

  const [restaurantData, setRestaurantData] = useState({
    restaurantName: "",
    physicalAddress: "",
    openingHours: "",
    closingHours: "",
  });

  const [logoPreview, setLogoPreview] = useState(null);

  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    try {
      const [profileRes, restaurantRes] = await Promise.all([
        settingsApi.getProfile().catch(() => null),
        settingsApi.getRestaurantProfile().catch(() => null),
      ]);

      const profile = profileRes?.data;
      const restaurant = restaurantRes?.data;

      if (profile) {
        setPersonalData({
          fullname: profile.fullname || "",
          email: profile.email || "",
        });
      }

      if (restaurant) {
        setRestaurantData({
          restaurantName: restaurant.restaurantName || "",
          physicalAddress: restaurant.physicalAddress || "",
          openingHours: restaurant.openingHours || "",
          closingHours: restaurant.closingHours || "",
        });
        if (restaurant.logoFileId) {
          setLogoPreview(`${API_BASE_URL}/files/${restaurant.logoFileId}`);
        }
      }

    } catch (error) {
      console.error("Failed to load settings:", error);
      showMessage("error", "Failed to load settings. Some data may not be available.");
    } finally {
      setLoading(false);
    }
  };

  const showMessage = (type, text) => {
    setMessage({ type, text });
    setTimeout(() => setMessage({ type: "", text: "" }), 3500);
  };

  const handlePersonalChange = (e) => {
    const { name, value } = e.target;
    setPersonalData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRestaurantChange = (e) => {
    const { name, value } = e.target;
    setRestaurantData((prev) => ({ ...prev, [name]: value }));
  };

  const handleLinkGoogle = () => {
    window.location.href = `${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/google`;
  };

  const handlePasswordInputChange = (e) => {
    const { name, value } = e.target;
    setPasswordData((prev) => ({ ...prev, [name]: value }));
  };

  const submitPasswordChange = async (e) => {
    e.preventDefault();

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      showMessage("error", "New passwords do not match.");
      return;
    }

    if (passwordData.newPassword.length < 8) {
      showMessage("error", "New password must be at least 8 characters.");
      return;
    }

    setLoading(true);
    try {
      await settingsApi.changePassword(passwordData.currentPassword, passwordData.newPassword);
      showMessage("success", "Password updated successfully.");
      setShowPasswordModal(false);
      setPasswordData({ currentPassword: "", newPassword: "", confirmPassword: "" });
    } catch (err) {
      const msg =
        err?.response?.data?.error?.details ||
        err?.response?.data?.error?.message ||
        "Failed to update password. Check your current password and try again.";
      showMessage("error", msg);
    } finally {
      setLoading(false);
    }
  };

  const savePersonal = async () => {
    setLoading(true);
    try {
      await settingsApi.updateProfile(personalData);
      showMessage("success", "Personal profile updated successfully.");
    } catch (err) {
      showMessage("error", "Failed to update personal profile.");
    } finally {
      setLoading(false);
    }
  };

  const saveRestaurant = async () => {
    setLoading(true);
    try {
      await settingsApi.updateRestaurantProfile(restaurantData);
      showMessage("success", "Restaurant profile updated successfully.");
    } catch (err) {
      showMessage("error", "Failed to update restaurant profile.");
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (event, type) => {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("logo", file);

    setLoading(true);
    try {
      await settingsApi.updateRestaurantLogo(formData);
      const res = await settingsApi.getRestaurantProfile();
      const updated = res?.data;
      if (updated?.logoFileId) {
        setLogoPreview(`${API_BASE_URL}/files/${updated.logoFileId}`);
      }
      showMessage("success", "Logo updated successfully.");
    } catch (err) {
      showMessage("error", "Failed to upload logo.");
    } finally {
      setLoading(false);
    }
  };

  const isGoogleLinked = user?.provider === "google";

  return {
    loading,
    message,
    personalData,
    handlePersonalChange,
    savePersonal,
    restaurantData,
    handleRestaurantChange,
    saveRestaurant,
    logoPreview,
    handleFileUpload,
    isGoogleLinked,
    handleLinkGoogle,
    showPasswordModal,
    setShowPasswordModal,
    passwordData,
    handlePasswordInputChange,
    submitPasswordChange,
  };
};
