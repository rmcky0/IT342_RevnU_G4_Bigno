import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/context/AuthContext";
import { restaurantApi } from "../api/restaurantApi";

export const useSetupRestaurant = () => {
  const [form, setForm] = useState({
    name: "",
    physicalLocation: "",
    openingTime: "08:00",
    closingTime: "22:00",
  });

  const [logoPreview, setLogoPreview] = useState(null);
  const [logoFile, setLogoFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const fileInputRef = useRef(null);

  const navigate = useNavigate();
  const { completeRestaurantSetup } = useAuth();

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleImageClick = () => {
    fileInputRef.current.click();
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setLogoFile(file);
      setLogoPreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await restaurantApi.setupRestaurant(form, logoFile);

      completeRestaurantSetup();

      navigate("/dashboard", { replace: true });
    } catch (error) {
      console.error("Failed to setup restaurant:", error);
    } finally {
      setLoading(false);
    }
  };

  return {
    form,
    loading,
    logoPreview,
    fileInputRef,
    handleChange,
    handleImageClick,
    handleImageChange,
    handleSubmit,
  };
};
