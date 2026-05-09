import React from "react";
import { AlertCircle } from "lucide-react";

export const ErrorBanner = ({ error, onRetry }) => {
  if (!error) return null;
  return (
    <div className="shrink-0 flex items-center justify-between bg-red-50 border border-red-100 rounded-xl px-5 py-3 shadow-sm animate-in fade-in">
      <div className="flex items-center gap-3 text-sm font-medium text-red-600">
        <AlertCircle className="w-4 h-4 shrink-0" />
        {error}
      </div>
      <button
        onClick={onRetry}
        className="text-xs font-bold text-red-700 bg-red-100 hover:bg-red-200 px-3 py-1.5 rounded-lg transition-colors"
      >
        Retry Connection
      </button>
    </div>
  );
};
