import React from "react";
import { useSettings } from "./hooks/useSettings";
import { SettingsContent } from "./components/SettingsContent";

export const Settings = () => {
  const {
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
  } = useSettings();

  return (
    <SettingsContent
      loading={loading}
      message={message}
      personalData={personalData}
      handlePersonalChange={handlePersonalChange}
      savePersonal={savePersonal}
      restaurantData={restaurantData}
      handleRestaurantChange={handleRestaurantChange}
      saveRestaurant={saveRestaurant}
      logoPreview={logoPreview}
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
