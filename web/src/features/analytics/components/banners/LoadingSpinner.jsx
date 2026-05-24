import React from "react";

export const LoadingSpinner = () => (
  <div className="h-full flex flex-col items-center justify-center space-y-4 animate-in fade-in duration-300">
    <div className="w-10 h-10 border-4 border-gray-100 border-t-[#7C6FF7] rounded-full animate-spin" />
    <p className="text-xs font-bold tracking-widest text-gray-400 uppercase">
      Updating Dashboard
    </p>
  </div>
);
