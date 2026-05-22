import React from "react";
import {
  Camera,
  Building2,
  MapPin,
  Clock,
  Image as ImageIcon,
  User,
  ShieldCheck,
  Lock,
  Save,
  Check,
} from "lucide-react";

const GoogleIcon = () => (
  <svg className="w-5 h-5" viewBox="0 0 24 24">
    <path
      fill="#4285F4"
      d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
    />
    <path
      fill="#34A853"
      d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
    />
    <path
      fill="#FBBC05"
      d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
    />
    <path
      fill="#EA4335"
      d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
    />
  </svg>
);

const SideCard = ({
  icon,
  title,
  description,
  iconBg = "bg-indigo-50",
  iconColor = "text-[#7c83fd]",
}) => (
  <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 p-8 flex flex-col items-center text-center gap-4">
    <div className={`p-3 ${iconBg} ${iconColor} rounded-xl`}>{icon}</div>
    <div>
      <h3 className="font-bold text-[#1e1b4b] text-sm">{title}</h3>
      <p className="text-xs text-gray-400 mt-1 leading-relaxed">
        {description}
      </p>
    </div>
  </div>
);

export const SettingsBody = ({
  restaurantData,
  logoPreview,
  loading,
  onRestaurantChange,
  onSaveRestaurant,
  onFileUpload,
  personalData,
  isGoogleLinked,
  onPersonalChange,
  onSavePersonal,
  onOpenPasswordModal,
  onLinkGoogle,
}) => (
  <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start animate-in fade-in duration-300">
    {/* ── Restaurant: Logo ── */}
    <div className="lg:col-span-1">
      <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 p-8 flex flex-col items-center text-center">
        <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider mb-5 flex items-center gap-2">
          <ImageIcon className="w-4 h-4 text-gray-400" /> Restaurant Logo
        </h3>
        <div className="w-full aspect-square max-w-50 rounded-2xl bg-gray-50 border-2 border-dashed border-gray-200 flex flex-col items-center justify-center p-6 cursor-pointer hover:bg-indigo-50 hover:border-indigo-200 transition-colors relative group overflow-hidden">
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
              <span className="text-xs text-gray-400 mt-1">
                PNG, JPG up to 5MB
              </span>
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

    <div className="lg:col-span-2">
      {/* ── Restaurant: Profile Form ── */}
      <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 overflow-hidden mb-5">
        <div className="px-8 py-5 border-b border-gray-50 flex items-center gap-3">
          <div className="p-1.5 bg-indigo-50 text-[#7c83fd] rounded-lg">
            <Building2 className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">
            Restaurant Profile
          </h3>
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

      {/* ── Personal: Form ── */}
      <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 overflow-hidden mb-5">
        <div className="px-8 py-5 border-b border-gray-50 flex items-center gap-3">
          <div className="p-1.5 bg-indigo-50 text-[#7c83fd] rounded-lg">
            <User className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">Personal Details</h3>
        </div>
        <div className="p-8 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-1.5">
              <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                Full Name
              </label>
              <input
                type="text"
                name="fullname"
                value={personalData.fullname}
                onChange={onPersonalChange}
                placeholder="e.g. John Doe"
                className="w-full px-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
              />
            </div>
            <div className="space-y-1.5">
              <label className="flex text-[11px] font-bold text-gray-500 uppercase tracking-wider justify-between">
                Email Address{" "}
                <span className="text-gray-400 font-medium normal-case">
                  (Read-only)
                </span>
              </label>
              <input
                type="email"
                name="email"
                value={personalData.email}
                readOnly
                className="w-full px-4 py-2.5 bg-gray-50/50 rounded-lg border border-gray-200 font-semibold text-gray-500 transition-all text-sm outline-none shadow-sm cursor-not-allowed select-none"
              />
            </div>
          </div>
          <div className="flex justify-end">
            <button
              onClick={onSavePersonal}
              disabled={loading}
              className="flex items-center gap-2 px-6 py-2.5 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:hover:translate-y-0"
            >
              <Save className="w-4 h-4" />
              {loading ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </div>
      </div>

      {/* ── Security: Options ── */}
      <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 overflow-hidden mb-5">
        <div className="px-8 py-5 border-b border-gray-50 flex items-center gap-3">
          <div className="p-1.5 bg-gray-100 text-gray-600 rounded-lg">
            <ShieldCheck className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">Security</h3>
        </div>
        <div className="p-8 space-y-4">
          {!isGoogleLinked && (
            <div className="flex items-center justify-between p-4 bg-gray-50/50 rounded-xl border border-gray-100">
              <div className="flex items-center gap-3">
                <div className="p-2.5 bg-white rounded-lg shadow-sm border border-gray-100">
                  <Lock className="w-5 h-5 text-gray-400" />
                </div>
                <div>
                  <p className="font-bold text-[#1e1b4b] text-sm uppercase tracking-wider">
                    Password
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    Last updated recently
                  </p>
                </div>
              </div>
              <button
                onClick={onOpenPasswordModal}
                className="px-5 py-2 bg-white text-[#1e1b4b] border border-gray-200 rounded-lg font-semibold text-xs shadow-sm hover:bg-gray-50 transition-all"
              >
                Change
              </button>
            </div>
          )}
          <div className="flex items-center justify-between p-4 bg-gray-50/50 rounded-xl border border-gray-100">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-white rounded-lg shadow-sm border border-gray-100">
                <GoogleIcon />
              </div>
              <div>
                <p className="font-bold text-[#1e1b4b] text-sm uppercase tracking-wider">
                  Google SSO
                </p>
                <p className="text-xs text-gray-500 mt-0.5 truncate max-w-50">
                  {isGoogleLinked
                    ? personalData.email
                    : "Link your Gmail to sign in faster"}
                </p>
              </div>
            </div>
            {isGoogleLinked ? (
              <span className="px-3 py-1.5 bg-emerald-50 text-emerald-600 rounded-lg text-xs font-bold border border-emerald-100 flex items-center gap-1.5">
                <Check className="w-3.5 h-3.5" /> Connected
              </span>
            ) : (
              <button
                onClick={onLinkGoogle}
                className="px-5 py-2 bg-white text-[#1e1b4b] border border-gray-200 rounded-lg font-semibold text-xs shadow-sm hover:bg-gray-50 transition-all flex items-center gap-2"
              >
                <GoogleIcon />
                Link Account
              </button>
            )}
          </div>
          {!isGoogleLinked && (
            <p className="text-[11px] text-gray-400 px-1">
              Linking Google lets you sign in with either your password or
              Google on the same account. Only works if your Gmail matches your
              registered email.
            </p>
          )}
        </div>
      </div>
    </div>
  </div>
);
