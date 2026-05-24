import React from "react";
import { SettingsHeader } from "./SettingsHeader";
import { SettingsBody } from "./SettingsBody";
import { PasswordModal } from "./PasswordModal";

export const SettingsContent = ({
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
  loading,
  showPasswordModal,
  setShowPasswordModal,
  passwordData,
  handlePasswordInputChange,
  submitPasswordChange,
}) => (
  <div className="flex flex-col flex-1 gap-4 text-[#1e1b4b] h-full overflow-y-auto custom-scrollbar pb-6">
    <SettingsHeader />

    <SettingsBody
      restaurantData={restaurantData}
      logoPreview={logoPreview}
      loading={loading}
      onRestaurantChange={handleRestaurantChange}
      onSaveRestaurant={saveRestaurant}
      onFileUpload={handleFileUpload}
      personalData={personalData}
      isGoogleLinked={isGoogleLinked}
      onPersonalChange={handlePersonalChange}
      onSavePersonal={savePersonal}
      onOpenPasswordModal={() => setShowPasswordModal(true)}
      onLinkGoogle={handleLinkGoogle}
    />

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
