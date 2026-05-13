import React from "react";
import { Camera, Building2, MapPin, Clock, Save, Image as ImageIcon } from "lucide-react";

export const RestaurantTab = ({
  restaurantData,
  logoPreview,
  loading,
  onRestaurantChange,
  onSaveRestaurant,
  onFileUpload,
}) => (
  <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start animate-in fade-in duration-300">
    {/* Logo Upload */}
    <div className="lg:col-span-1">
      <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 p-8 flex flex-col items-center text-center">
        <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider mb-5 flex items-center gap-2">
          <ImageIcon className="w-4 h-4 text-gray-400" /> Restaurant Logo
        </h3>
        <div className="w-full aspect-square max-w-[200px] rounded-2xl bg-gray-50 border-2 border-dashed border-gray-200 flex flex-col items-center justify-center p-6 cursor-pointer hover:bg-indigo-50 hover:border-indigo-200 transition-colors relative group overflow-hidden">
          {logoPreview ? (
            <>
              <img
                src={logoPreview}
                alt="Restaurant logo"
                className="absolute inset-0 w-full h-full object-cover rounded-2xl"
              />
              <div className="absolute inset-0 bg-[#1e1b4b]/50 opacity-0 group-hover:opacity-100 transition-opacity flex flex-col items-center justify-center gap-1">
                <Camera className="w-6 h-6 text-white" />
                <span className="text-xs font-bold text-white">Change</span>
              </div>
            </>
          ) : (
            <>
              <Building2 className="w-12 h-12 text-gray-300 group-hover:text-[#7c83fd] transition-colors mb-3" />
              <span className="text-sm font-bold text-gray-500 group-hover:text-[#7c83fd]">
                Click to Upload
              </span>
              <span className="text-xs text-gray-400 mt-1">PNG, JPG up to 5MB</span>
            </>
          )}
          <input
            type="file"
            className="absolute inset-0 opacity-0 cursor-pointer"
            accept="image/*"
            onChange={(e) => onFileUpload(e, "restaurant")}
          />
        </div>
      </div>
    </div>

    {/* Profile Form */}
    <div className="lg:col-span-2">
      <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 overflow-hidden">
        <div className="px-8 py-5 border-b border-gray-50 flex items-center gap-3">
          <div className="p-1.5 bg-indigo-50 text-[#7c83fd] rounded-lg">
            <Building2 className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">Restaurant Profile</h3>
        </div>

        <div className="p-8 space-y-6">
          <div className="space-y-1.5">
            <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
              Restaurant Name
            </label>
            <input
              type="text"
              name="restaurantName"
              value={restaurantData.restaurantName}
              onChange={onRestaurantChange}
              className="w-full px-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
            />
          </div>

          <div className="space-y-1.5">
            <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
              Physical Address
            </label>
            <div className="relative">
              <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="text"
                name="physicalAddress"
                value={restaurantData.physicalAddress}
                onChange={onRestaurantChange}
                className="w-full pl-9 pr-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {[
              { name: "openingHours", label: "Opening Hours" },
              { name: "closingHours", label: "Closing Hours" },
            ].map(({ name, label }) => (
              <div key={name} className="space-y-1.5">
                <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  {label}
                </label>
                <div className="relative">
                  <Clock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                  <input
                    type="time"
                    name={name}
                    value={restaurantData[name]}
                    onChange={onRestaurantChange}
                    className="w-full pl-9 pr-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
                  />
                </div>
              </div>
            ))}
          </div>

          <div className="pt-4 flex justify-end">
            <button
              onClick={onSaveRestaurant}
              disabled={loading}
              className="flex items-center gap-2 px-6 py-2.5 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:hover:translate-y-0"
            >
              <Save className="w-4 h-4" />
              {loading ? "Saving..." : "Save Restaurant Profile"}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
);
