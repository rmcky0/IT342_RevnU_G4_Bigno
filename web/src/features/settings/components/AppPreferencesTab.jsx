import React from "react";
import { Bell, ShieldCheck, Save } from "lucide-react";

const Toggle = ({
  checked,
  onChange,
  color = "bg-[#7c83fd]",
  focusRing = "focus:ring-indigo-500",
}) => (
  <button
    onClick={onChange}
    className={`relative w-11 h-6 rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 ${focusRing} ${
      checked ? color : "bg-gray-200"
    }`}
  >
    <div
      className={`absolute top-1 left-1 w-4 h-4 rounded-full bg-white transition-transform shadow-sm ${
        checked ? "translate-x-5" : "translate-x-0"
      }`}
    />
  </button>
);

export const AppPreferencesTab = ({
  appSettings,
  loading,
  onAppToggle,
  onSaveAppSettings,
}) => (
  <div className="space-y-6 animate-in fade-in duration-300">
    <div className=" gap-6 items-start">
      {/* Notifications */}
      <div className="mb-5 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 overflow-hidden flex flex-col">
        <div className="px-6 py-5 border-b border-gray-50 flex items-center gap-3">
          <div className="p-1.5 bg-blue-50 text-blue-500 rounded-lg">
            <Bell className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">Notifications</h3>
        </div>
        <div className="p-6 space-y-4 flex-1">
          {[
            {
              key: "emailNotifications",
              label: "Email Summaries",
              desc: "Receive daily end-of-day sales reports",
            },
            {
              key: "pushNotifications",
              label: "Push Alerts",
              desc: "Real-time alerts for system events",
            },
          ].map(({ key, label, desc }) => (
            <div
              key={key}
              className="flex items-center justify-between p-4 border border-gray-100 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors"
            >
              <div>
                <p className="font-bold text-[#1e1b4b] text-sm">{label}</p>
                <p className="text-[11px] text-gray-500 mt-0.5">{desc}</p>
              </div>
              <Toggle
                checked={appSettings[key]}
                onChange={() => onAppToggle(key)}
              />
            </div>
          ))}
        </div>
      </div>

      {/* Workflow Rules */}
      <div className="md:col-span-2 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-50 flex items-center gap-3">
          <div className="p-1.5 bg-orange-50 text-orange-500 rounded-lg">
            <ShieldCheck className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">Workflow Rules</h3>
        </div>
        <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="flex items-center justify-between p-4 border border-gray-100 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
            <div>
              <p className="font-bold text-[#1e1b4b] text-sm">
                Require Receipts
              </p>
              <p className="text-[11px] text-gray-500 mt-0.5">
                Make photo uploads mandatory for expenses
              </p>
            </div>
            <Toggle
              checked={appSettings.requireReceiptPhoto}
              onChange={() => onAppToggle("requireReceiptPhoto")}
              color="bg-orange-500"
              focusRing="focus:ring-orange-500"
            />
          </div>
          <div className="flex items-center justify-between p-4 border border-gray-100 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-colors">
            <div>
              <p className="font-bold text-[#1e1b4b] text-sm">
                Soft Lock Records
              </p>
              <p className="text-[11px] text-gray-500 mt-0.5">
                Allow editing locked EOD records with a warning
              </p>
            </div>
            <Toggle
              checked={appSettings.softLockRecords}
              onChange={() => onAppToggle("softLockRecords")}
            />
          </div>
        </div>
      </div>
    </div>

    <div className="flex justify-end pt-2">
      <button
        onClick={onSaveAppSettings}
        disabled={loading}
        className="flex items-center gap-2 px-8 py-2.5 bg-[#1e1b4b] text-white rounded-lg font-semibold text-sm shadow-lg shadow-indigo-900/20 hover:bg-indigo-900 hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:hover:translate-y-0"
      >
        <Save className="w-4 h-4" />
        {loading ? "Applying..." : "Save Preferences"}
      </button>
    </div>
  </div>
);
