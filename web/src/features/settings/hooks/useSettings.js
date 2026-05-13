import { useState, useEffect } from "react";
import { settingsApi } from "../api/settingsApi";
import { useAuth } from "../../auth/context/AuthContext";
import { API_BASE_URL } from "../../../shared/api/axios";

const toFileUrl = (fileId) =>
  fileId ? `${API_BASE_URL}/files/${fileId}` : null;

export const useSettings = () => {
  const { user, updateAvatar } = useAuth();
  const [activeTab, setActiveTab] = useState("personal");
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
  const [avatarPreview, setAvatarPreview] = useState(
    toFileUrl(user?.avatarFileId),
  );

  const [appSettings, setAppSettings] = useState({
    emailNotifications: true,
    pushNotifications: false,
    requireReceiptPhoto: false,
    softLockRecords: true,
  });

  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  // ── Load on mount ──────────────────────────────────────────────────────────

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    try {
      const [profile, restaurant, system] = await Promise.all([
        settingsApi.getProfile().catch(() => null),
        settingsApi.getRestaurantProfile().catch(() => null),
        settingsApi.getSystemSettings().catch(() => null),
      ]);

      if (profile) {
        setPersonalData({
          fullname: profile.fullname || "",
          email: profile.email || "",
        });
        if (profile.avatarFileId) {
          const url = toFileUrl(profile.avatarFileId);
          setAvatarPreview(url);
          updateAvatar(profile.avatarFileId);
        }
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

      if (system) {
        setAppSettings({
          emailNotifications: system.emailNotifications ?? true,
          pushNotifications: system.pushNotifications ?? false,
          requireReceiptPhoto: system.requireReceiptPhoto ?? false,
          softLockRecords: system.softLockRecords ?? true,
        });
      }
    } catch (error) {
      console.error("Failed to load settings:", error);
      showMessage(
        "error",
        "Failed to load settings. Some data may not be available.",
      );
    } finally {
      setLoading(false);
    }
  };

  const showMessage = (type, text) => {
    setMessage({ type, text });
    setTimeout(() => setMessage({ type: "", text: "" }), 3500);
  };

  // ── Change handlers ────────────────────────────────────────────────────────

  const handlePersonalChange = (e) => {
    const { name, value } = e.target;
    setPersonalData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRestaurantChange = (e) => {
    const { name, value } = e.target;
    setRestaurantData((prev) => ({ ...prev, [name]: value }));
  };

  const handleAppToggle = (settingName) => {
    setAppSettings((prev) => ({ ...prev, [settingName]: !prev[settingName] }));
  };

  const handleLinkGoogle = () => {
    // Redirect to backend OAuth2 endpoint
    window.location.href = `${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/google`;
  };

  // ── Password handlers ──────────────────────────────────────────────────────

  const handlePasswordInputChange = (e) => {
    const { name, value } = e.target;
    setPasswordData((prev) => ({ ...prev, [name]: value }));
  };

  /**
   * FIX: API call is now wired — was previously commented out.
   * Validates passwords match before sending, then calls the real endpoint.
   */
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
      await settingsApi.changePassword(
        passwordData.currentPassword,
        passwordData.newPassword,
      );
      showMessage("success", "Password updated successfully.");
      setShowPasswordModal(false);
      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Failed to update password. Check your current password and try again.";
      showMessage("error", msg);
    } finally {
      setLoading(false);
    }
  };

  // ── Save functions ─────────────────────────────────────────────────────────

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

  const saveAppSettings = async () => {
    setLoading(true);
    try {
      await settingsApi.updateSystemSettings(appSettings);
      showMessage("success", "App preferences saved successfully.");
    } catch (err) {
      showMessage("error", "Failed to save app settings.");
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (event, type) => {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append(type === "profile" ? "avatar" : "logo", file);

    setLoading(true);
    try {
      if (type === "profile") {
        const updated = await settingsApi.updateProfilePicture(formData);
        if (updated?.avatarFileId) {
          const url = toFileUrl(updated.avatarFileId);
          setAvatarPreview(url);
          updateAvatar(updated.avatarFileId);
        }
      } else {
        await settingsApi.updateRestaurantLogo(formData);
        const updated = await settingsApi.getRestaurantProfile();
        if (updated?.logoFileId) {
          setLogoPreview(`${API_BASE_URL}/files/${updated.logoFileId}`);
        }
      }
      showMessage("success", "Image updated successfully.");
    } catch (err) {
      showMessage("error", "Failed to upload image.");
    } finally {
      setLoading(false);
    }
  };

  const isGoogleLinked = user?.provider === "google";

  return {
    user,
    activeTab,
    setActiveTab,
    loading,
    message,
    personalData,
    handlePersonalChange,
    savePersonal,
    restaurantData,
    handleRestaurantChange,
    saveRestaurant,
    logoPreview,
    avatarPreview,
    appSettings,
    handleAppToggle,
    saveAppSettings,
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
