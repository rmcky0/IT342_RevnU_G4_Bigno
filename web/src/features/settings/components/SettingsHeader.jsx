import React from "react";
import {
  User,
  Building2,
  Sliders,
  CheckCircle2,
  AlertCircle,
  Settings as SettingsIcon,
} from "lucide-react";

const TABS = [
  { id: "personal", icon: User, label: "User Profile" },
  { id: "restaurant", icon: Building2, label: "Restaurant Identity" },
  { id: "system", icon: Sliders, label: "App Preferences" },
];

export const SettingsHeader = ({ activeTab, setActiveTab, message }) => (
  <div className="shrink-0 space-y-5">
    <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div className="flex items-center gap-3">
        <div className="p-2 bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
          <SettingsIcon className="w-5 h-5 text-white" />
        </div>
        <h2 className="text-2xl font-bold tracking-tight">System Settings</h2>
      </div>

      <div className="flex items-center bg-white p-1 rounded-xl shadow-sm border border-gray-100 overflow-x-auto">
        {TABS.map(({ id, icon: Icon, label }) => (
          <button
            key={id}
            onClick={() => setActiveTab(id)}
            className={`px-5 py-1.5 rounded-lg font-semibold text-sm transition-all whitespace-nowrap ${
              activeTab === id
                ? "bg-indigo-50 text-[#7c83fd] shadow-sm"
                : "text-gray-500 hover:text-gray-700"
            }`}
          >
            <div className="flex items-center gap-2">
              <Icon className="w-4 h-4" /> {label}
            </div>
          </button>
        ))}
      </div>
    </div>

    {message.text && (
      <div
        className={`px-4 py-3 rounded-lg text-sm font-medium shadow-sm animate-in fade-in slide-in-from-top-2 flex items-center gap-2 ${
          message.type === "success"
            ? "bg-green-50 text-green-700 border border-green-200"
            : "bg-red-50 text-red-600 border border-red-200"
        }`}
      >
        {message.type === "success" ? (
          <CheckCircle2 className="w-4 h-4 shrink-0" />
        ) : (
          <AlertCircle className="w-4 h-4 shrink-0" />
        )}
        {message.text}
      </div>
    )}
  </div>
);
