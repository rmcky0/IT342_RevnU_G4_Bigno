import React from "react";
import { useSettings } from "./hooks/useSettings";
import { SettingsContent } from "./components/SettingsContent";

export const Settings = () => {
  const {
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
    handleAppSelect,
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
  } = useSettings();

  return (
    <SettingsContent
      user={user}
      activeTab={activeTab}
      setActiveTab={setActiveTab}
      loading={loading}
      message={message}
      personalData={personalData}
      handlePersonalChange={handlePersonalChange}
      savePersonal={savePersonal}
      restaurantData={restaurantData}
      handleRestaurantChange={handleRestaurantChange}
      saveRestaurant={saveRestaurant}
      logoPreview={logoPreview}
      avatarPreview={avatarPreview}
      appSettings={appSettings}
      handleAppToggle={handleAppToggle}
      saveAppSettings={saveAppSettings}
      handleFileUpload={handleFileUpload}
      isGoogleLinked={isGoogleLinked}
      handleLinkGoogle={handleLinkGoogle}
      showPasswordModal={showPasswordModal}
      setShowPasswordModal={setShowPasswordModal}
      passwordData={passwordData}
      handlePasswordInputChange={handlePasswordInputChange}
      submitPasswordChange={submitPasswordChange}
    />
  );
};
