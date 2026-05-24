import { useSetupRestaurant } from "./hooks/useSetupRestaurant";
import { useAuth } from "../auth/context/AuthContext";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Store, ImagePlus, Camera } from "lucide-react";

export const SetupRestaurant = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) navigate("/login");
  }, [user, navigate]);

  const {
    form,
    loading,
    logoPreview,
    fileInputRef,
    handleChange,
    handleImageClick,
    handleImageChange,
    handleSubmit,
  } = useSetupRestaurant();

  return (
    <div className="h-screen bg-gray-50/50 flex flex-col justify-center items-center px-4 sm:px-6 font-sans overflow-y-auto">
      {/* ── Header Section ── */}
      <div className="w-full max-w-2xl text-center mb-6 mt-auto sm:mt-0 pt-8 sm:pt-0">
        <div className="w-12 h-12 flex items-center justify-center bg-white rounded-2xl shadow-sm border border-gray-100 p-2.5 mx-auto mb-4">
          <Store className="w-6 h-6 text-[#7C6FF7]" />
        </div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 mb-1.5 tracking-tight">
          Welcome, {user?.fullname?.split(" ")[0] || "Owner"}!
        </h1>
        <p className="text-sm text-gray-500 max-w-sm mx-auto">
          Let's get your workspace set up to track your revenue.
        </p>
      </div>

      {/* ── Main Form Card (Shrunk to max-w-2xl for better density) ── */}
      <div className="bg-white p-6 sm:px-8 sm:py-8 shadow-[0_4px_24px_rgb(0,0,0,0.03)] border border-gray-100/80 rounded-[1.5rem] w-full max-w-2xl transition-all mb-auto sm:mb-0 pb-8 sm:pb-8">
        <form onSubmit={handleSubmit} className="w-full flex flex-col">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 md:gap-8">
            {/* Left Column: Form Inputs */}
            <div className="md:col-span-2 space-y-4">
              <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Restaurant Name
                </label>
                <input
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="e.g. The Rustic Spoon"
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none"
                  required
                />
              </div>

              <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Physical Address
                </label>
                <input
                  type="text"
                  name="physicalLocation"
                  value={form.physicalLocation}
                  onChange={handleChange}
                  placeholder="123 Main Street, City, State"
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Opening Time
                  </label>
                  <input
                    type="time"
                    name="openingTime"
                    value={form.openingTime}
                    onChange={handleChange}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-700 focus:bg-white focus:outline-none cursor-pointer"
                    required
                  />
                </div>
                <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Closing Time
                  </label>
                  <input
                    type="time"
                    name="closingTime"
                    value={form.closingTime}
                    onChange={handleChange}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-700 focus:bg-white focus:outline-none cursor-pointer"
                    required
                  />
                </div>
              </div>
            </div>

            {/* Right Column: Logo Upload */}
            <div className="md:col-span-1 flex flex-col items-center justify-start order-first md:order-last mb-6 md:mb-0">
              <div className="w-full flex flex-col items-center">
                <label className="block text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-2.5 text-center">
                  Restaurant Logo
                </label>
                <div
                  onClick={handleImageClick}
                  className="relative group w-32 h-32 sm:w-36 sm:h-36 rounded-[1.5rem] border-2 border-dashed border-gray-200 bg-gray-50 flex flex-col items-center justify-center cursor-pointer overflow-hidden transition-all duration-300 hover:border-[#7C6FF7]/50 hover:bg-indigo-50/30"
                >
                  {logoPreview ? (
                    <>
                      <img
                        src={logoPreview}
                        alt="Logo Preview"
                        className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                      />
                      <div className="absolute inset-0 bg-gray-900/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center backdrop-blur-sm">
                        <Camera className="w-8 h-8 text-white" />
                      </div>
                    </>
                  ) : (
                    <div className="flex flex-col items-center text-gray-400 group-hover:text-[#7C6FF7] transition-colors">
                      <ImagePlus className="w-7 h-7 mb-2" />
                      <span className="text-[10px] font-bold uppercase tracking-wider">
                        Upload
                      </span>
                    </div>
                  )}
                </div>
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleImageChange}
                  accept="image/*"
                  className="hidden"
                />
              </div>
            </div>
          </div>

          <hr className="border-gray-100 my-6 md:my-8" />

          {/* ── Submit Button (Now 100% width to anchor the form) ── */}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-[#7C6FF7] hover:bg-[#6a5ee6] text-white font-semibold rounded-xl text-sm transition-all shadow-md shadow-[#7C6FF7]/20 active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {loading ? (
              <>
                <svg
                  className="animate-spin h-4 w-4 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  ></circle>
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  ></path>
                </svg>
                Setting up...
              </>
            ) : (
              "Launch Live Dashboard"
            )}
          </button>
        </form>
      </div>
    </div>
  );
};
