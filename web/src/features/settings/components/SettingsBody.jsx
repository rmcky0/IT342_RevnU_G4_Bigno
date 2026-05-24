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
  <svg className="w-4.5 h-4.5" viewBox="0 0 24 24">
    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
  </svg>
);

const inputCls =
  "w-full px-4 py-2.5 bg-gray-50 rounded-xl border border-gray-200 focus:bg-white focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] text-sm outline-none transition-all";

const readonlyCls =
  "w-full px-4 py-2.5 bg-gray-50 rounded-xl border border-gray-200 font-semibold text-gray-400 text-sm outline-none cursor-not-allowed select-none";

const Field = ({ label, hint, children }) => (
  <div className="flex flex-col gap-1.5">
    <div className="flex items-center justify-between">
      <label className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">{label}</label>
      {hint && <span className="text-[10px] text-gray-400">{hint}</span>}
    </div>
    {children}
  </div>
);

const CardSection = ({ icon: Icon, iconBg = "bg-[#7c83fd]/10", iconColor = "text-[#7c83fd]", title, children, footer }) => (
  <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] overflow-hidden">
    <div className="px-5 py-3.5 border-b border-gray-100 flex items-center gap-3">
      <div className={`w-8 h-8 rounded-xl ${iconBg} flex items-center justify-center shrink-0`}>
        <Icon className={`w-4 h-4 ${iconColor}`} />
      </div>
      <p className="text-sm font-bold text-[#1e1b4b]">{title}</p>
    </div>
    <div className="p-5 flex flex-col gap-4">{children}</div>
    {footer && <div className="px-5 pb-5">{footer}</div>}
  </div>
);

const SaveButton = ({ onClick, loading, label = "Save Changes" }) => (
  <div className="flex justify-end">
    <button
      onClick={onClick}
      disabled={loading}
      className="inline-flex items-center gap-1.5 px-4 py-2 bg-[#7c83fd] text-white rounded-xl text-[13px] font-bold hover:bg-[#6b72f5] transition-colors disabled:opacity-50"
    >
      <Save className="w-3.5 h-3.5" />
      {loading ? "Saving…" : label}
    </button>
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
  <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 items-start animate-in fade-in duration-300">

    {/* ── Left column ── */}
    <div className="flex flex-col gap-4">

      {/* Logo upload */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] overflow-hidden">
        <div className="px-5 py-3.5 border-b border-gray-100 flex items-center gap-3">
          <div className="w-8 h-8 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center shrink-0">
            <ImageIcon className="w-4 h-4 text-[#7c83fd]" />
          </div>
          <p className="text-sm font-bold text-[#1e1b4b]">Restaurant Logo</p>
        </div>
        <div className="p-5 flex flex-col items-center">
          <div className="relative w-full aspect-square max-w-44 rounded-2xl bg-gray-50 border-2 border-dashed border-gray-200 flex flex-col items-center justify-center cursor-pointer hover:bg-[#f0f1ff] hover:border-[#7c83fd]/40 transition-colors group overflow-hidden">
            {logoPreview ? (
              <>
                <img
                  src={logoPreview}
                  alt="Restaurant logo"
                  className="absolute inset-0 w-full h-full object-cover rounded-2xl"
                />
                <div className="absolute inset-0 bg-[#1e1b4b]/50 opacity-0 group-hover:opacity-100 transition-opacity flex flex-col items-center justify-center gap-1.5 rounded-2xl">
                  <Camera className="w-5 h-5 text-white" />
                  <span className="text-[11px] font-bold text-white">Change Logo</span>
                </div>
              </>
            ) : (
              <div className="flex flex-col items-center gap-2 p-4 text-center">
                <div className="w-12 h-12 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center mb-1">
                  <Building2 className="w-6 h-6 text-[#7c83fd]/60 group-hover:text-[#7c83fd] transition-colors" />
                </div>
                <span className="text-[12px] font-bold text-gray-400 group-hover:text-[#7c83fd] transition-colors">Click to Upload</span>
                <span className="text-[10px] text-gray-300">PNG, JPG · max 5 MB</span>
              </div>
            )}
            <input
              type="file"
              className="absolute inset-0 opacity-0 cursor-pointer"
              accept="image/*"
              onChange={(e) => onFileUpload(e, "restaurant")}
            />
          </div>
          <p className="text-[10px] text-gray-400 mt-3 text-center">
            Shown on receipts and reports
          </p>
        </div>
      </div>

      {/* Security */}
      <CardSection
        icon={ShieldCheck}
        iconBg="bg-emerald-50"
        iconColor="text-emerald-500"
        title="Security"
      >
        {!isGoogleLinked && (
          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl border border-gray-100">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-white border border-gray-200 flex items-center justify-center shrink-0 shadow-sm">
                <Lock className="w-4 h-4 text-gray-400" />
              </div>
              <div>
                <p className="text-[12px] font-bold text-[#1e1b4b]">Password</p>
                <p className="text-[10px] text-gray-400 mt-0.5">Update your login password</p>
              </div>
            </div>
            <button
              onClick={onOpenPasswordModal}
              className="px-3 py-1.5 bg-white border border-gray-200 rounded-xl text-[11px] font-bold text-gray-600 hover:text-[#7c83fd] hover:border-[#7c83fd]/30 hover:bg-[#f0f1ff] transition-colors shadow-sm"
            >
              Change
            </button>
          </div>
        )}

        {isGoogleLinked && (
          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl border border-gray-100">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-white border border-gray-200 flex items-center justify-center shrink-0 shadow-sm">
                <GoogleIcon />
              </div>
              <div>
                <p className="text-[12px] font-bold text-[#1e1b4b]">Google SSO</p>
                <p className="text-[10px] text-gray-400 mt-0.5 truncate max-w-36">{personalData.email}</p>
              </div>
            </div>
            <span className="inline-flex items-center gap-1 px-2.5 py-1 bg-emerald-50 text-emerald-600 border border-emerald-200 rounded-full text-[10px] font-bold">
              <Check className="w-3 h-3" /> Connected
            </span>
          </div>
        )}
      </CardSection>
    </div>

    {/* ── Right column ── */}
    <div className="lg:col-span-2 flex flex-col gap-4">

      {/* Restaurant Profile */}
      <CardSection icon={Building2} title="Restaurant Profile">
        <Field label="Restaurant Name">
          <input
            type="text"
            name="restaurantName"
            value={restaurantData.restaurantName}
            onChange={onRestaurantChange}
            placeholder="e.g. Jollibee Makati"
            className={inputCls}
          />
        </Field>

        <Field label="Physical Address">
          <div className="relative">
            <MapPin className="absolute left-3.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400 pointer-events-none" />
            <input
              type="text"
              name="physicalAddress"
              value={restaurantData.physicalAddress}
              onChange={onRestaurantChange}
              placeholder="Street, City, Province"
              className={`${inputCls} pl-9`}
            />
          </div>
        </Field>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {[
            { name: "openingHours", label: "Opening Hours" },
            { name: "closingHours", label: "Closing Hours" },
          ].map(({ name, label }) => (
            <Field key={name} label={label}>
              <div className="relative">
                <Clock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400 pointer-events-none" />
                <input
                  type="time"
                  name={name}
                  value={restaurantData[name]}
                  onChange={onRestaurantChange}
                  className={`${inputCls} pl-9`}
                />
              </div>
            </Field>
          ))}
        </div>

        <SaveButton onClick={onSaveRestaurant} loading={loading} label="Save Restaurant Profile" />
      </CardSection>

      {/* Personal Details */}
      <CardSection icon={User} title="Personal Details">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Field label="Full Name">
            <input
              type="text"
              name="fullname"
              value={personalData.fullname}
              onChange={onPersonalChange}
              placeholder="e.g. Juan Dela Cruz"
              className={inputCls}
            />
          </Field>

          <Field label="Email Address" hint="Read-only">
            <input
              type="email"
              name="email"
              value={personalData.email}
              readOnly
              className={readonlyCls}
            />
          </Field>
        </div>

        <SaveButton onClick={onSavePersonal} loading={loading} label="Save Personal Details" />
      </CardSection>
    </div>
  </div>
);
