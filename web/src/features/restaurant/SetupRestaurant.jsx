import { useSetupRestaurant } from "./hooks/useSetupRestaurant";
import { useAuth } from "../auth/context/AuthContext";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

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
    <div className="min-h-screen relative overflow-hidden bg-[#5a7cff] flex items-center justify-center font-sans">
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[180vw] h-[180vw] md:w-[150vw] md:h-[150vw] lg:w-[120vw] lg:h-[120vw] bg-white rounded-full z-0"></div>

      {/* Main Content Container */}
      <div className="relative z-10 w-full max-w-3xl px-6 py-12 flex flex-col items-center">
        {/* Header Text */}
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">
            Welcome, {user?.fullname || "Workspace Owner"}!
          </h1>
          <p className="text-gray-700 text-lg">
            Let's get your restaurant ready for its first shift.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="w-full">
          {/* Card Container */}
          <div className="bg-[#f4f5fa] rounded-2xl p-8 mb-6 shadow-sm">
            <h2 className="text-xl font-semibold text-gray-800 mb-6">
              Restaurant Identity
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              {/* Left Column: Text Inputs */}
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Restaurant Name
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={form.name}
                    onChange={handleChange}
                    placeholder="e.g. The Rustic Spoon"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm bg-transparent focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#5a7cff]"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Physical Address
                  </label>
                  <input
                    type="text"
                    name="physicalLocation"
                    value={form.physicalLocation}
                    onChange={handleChange}
                    placeholder="123 Main Street, City"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm bg-transparent focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#5a7cff]"
                    required
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Opening Hours
                    </label>
                    <input
                      type="time"
                      name="openingTime"
                      value={form.openingTime}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm bg-transparent focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#5a7cff]"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Closing Hours
                    </label>
                    <input
                      type="time"
                      name="closingTime"
                      value={form.closingTime}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm bg-transparent focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#5a7cff]"
                      required
                    />
                  </div>
                </div>
              </div>

              {/* Right Column: Image Upload */}
              <div className="flex flex-col items-start md:pl-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Restaurant Logo
                </label>

                {/* Image Preview Box */}
                <div
                  className="w-48 h-48 rounded-2xl border-2 border-dashed border-gray-300 overflow-hidden bg-white mb-4 flex items-center justify-center cursor-pointer hover:border-[#5a7cff] transition-colors"
                  onClick={handleImageClick}
                >
                  {logoPreview ? (
                    <img
                      src={logoPreview}
                      alt="Logo Preview"
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <span className="text-gray-400 text-sm">
                      Click to upload
                    </span>
                  )}
                </div>

                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleImageChange}
                  accept="image/*"
                  className="hidden"
                />

                <button
                  type="button"
                  onClick={handleImageClick}
                  className="bg-[#9faaf5] hover:bg-[#8694ed] text-white px-6 py-2.5 rounded-lg text-sm font-medium transition-colors w-48"
                >
                  Upload Restaurant Logo
                </button>
              </div>
            </div>
          </div>

          {/* Submit Button */}
          <div className="flex justify-center">
            <button
              type="submit"
              disabled={loading}
              className="w-full md:w-[60%] bg-[#4f46e5] hover:bg-[#4338ca] text-white py-3.5 rounded-xl font-semibold text-sm transition-all shadow-md disabled:opacity-70 disabled:cursor-not-allowed"
            >
              {loading ? "Setting up workspace..." : "Launch Live Dashboard"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
