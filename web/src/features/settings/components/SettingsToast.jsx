import React from "react";
import { CheckCircle2, AlertCircle } from "lucide-react";

export const SettingsToast = ({ message }) => {
  if (!message?.text) return null;
  return (
    <div
      className={`shrink-0 px-4 py-3 rounded-lg text-sm font-medium shadow-sm animate-in fade-in slide-in-from-top-2 flex items-center gap-2 ${
        message.type === "success"
          ? "bg-green-50 text-green-700 border border-green-200"
          : "bg-red-50 text-red-600 border border-red-200"
      }`}
    >
      {message.type === "success" ? (
        <CheckCircle2 className="w-4 h-4" />
      ) : (
        <AlertCircle className="w-4 h-4" />
      )}
      {message.text}
    </div>
  );
};
