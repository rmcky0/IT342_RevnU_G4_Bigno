import React from "react";
import { CheckCircle2, AlertCircle, Settings as SettingsIcon } from "lucide-react";

export const SettingsHeader = ({ message }) => (
  <div className="shrink-0 space-y-4">
    <div className="flex items-center gap-3">
      <div className="p-2 bg-linear-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
        <SettingsIcon className="w-5 h-5 text-white" />
      </div>
      <h2 className="text-2xl font-bold tracking-tight">Settings</h2>
    </div>

    {message?.text && (
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
