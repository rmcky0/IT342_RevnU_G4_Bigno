import React from "react";
import { SettingsHeader } from "./SettingsHeader";
import { PersonalTab } from "./PersonalTab";
import { RestaurantTab } from "./RestaurantTab";
import { AppPreferencesTab } from "./AppPreferencesTab";
import { PasswordModal } from "./PasswordModal";

export const SettingsContent = ({
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
}) => (
  <div className="flex flex-col flex-1 h-full max-h-full space-y-5 overflow-y-auto text-[#1e1b4b] pb-10">
    <SettingsHeader
      activeTab={activeTab}
      setActiveTab={setActiveTab}
      message={message}
    />

    {activeTab === "personal" && (
      <PersonalTab
        user={user}
        personalData={personalData}
        avatarPreview={avatarPreview}
        loading={loading}
        isGoogleLinked={isGoogleLinked}
        onPersonalChange={handlePersonalChange}
        onSavePersonal={savePersonal}
        onFileUpload={handleFileUpload}
        onOpenPasswordModal={() => setShowPasswordModal(true)}
        onLinkGoogle={handleLinkGoogle}
      />
    )}

    {activeTab === "restaurant" && (
      <RestaurantTab
        restaurantData={restaurantData}
        logoPreview={logoPreview}
        loading={loading}
        onRestaurantChange={handleRestaurantChange}
        onSaveRestaurant={saveRestaurant}
        onFileUpload={handleFileUpload}
      />
    )}

    {activeTab === "system" && (
      <AppPreferencesTab
        appSettings={appSettings}
        loading={loading}
        onAppToggle={handleAppToggle}
        onSaveAppSettings={saveAppSettings}
      />
    )}

    {showPasswordModal && (
      <PasswordModal
        passwordData={passwordData}
        loading={loading}
        onInputChange={handlePasswordInputChange}
        onSubmit={submitPasswordChange}
        onClose={() => setShowPasswordModal(false)}
      />
    )}
  </div>
);
