import React from "react";
import { User, Building2, Sliders } from "lucide-react";

const TABS = [
  { id: "personal", label: "User Profile", icon: User },
  { id: "restaurant", label: "Restaurant Identity", icon: Building2 },
  { id: "system", label: "App Preferences", icon: Sliders },
];

export const SettingsTabBar = ({ activeTab, setActiveTab }) => (
  <div className="flex items-center bg-white p-1 rounded-xl shadow-sm border border-gray-100 overflow-x-auto">
    {TABS.map(({ id, label, icon: Icon }) => (
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
);
