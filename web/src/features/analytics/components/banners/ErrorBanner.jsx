import React from "react";
import { AlertTriangle } from "lucide-react";

export const ErrorBanner = ({ error, onRetry }) => {
  if (!error) return null;
  return (
    <div className="shrink-0 flex items-center justify-between gap-3 px-4 py-2.5 bg-red-50 border border-red-100 rounded-xl text-red-600 text-[13px] font-semibold animate-in fade-in slide-in-from-top-1 duration-200 mb-3">
      <div className="flex items-center gap-2">
        <AlertTriangle className="w-4 h-4 shrink-0" />
        {error}
      </div>
      <button
        onClick={onRetry}
        className="text-[11px] font-bold text-red-600 bg-red-100 hover:bg-red-200 px-3 py-1.5 rounded-xl transition-colors active:scale-95 shrink-0"
      >
        Retry
      </button>
    </div>
  );
};
