import React from "react";
import { Settings as SettingsIcon } from "lucide-react";

export const SettingsHeader = () => (
  <div className="shrink-0 flex items-center gap-2.5">
    <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
      <SettingsIcon className="w-4.5 h-4.5 text-white" />
    </div>
    <h2 className="text-lg font-bold tracking-tight text-[#1e1b4b]">Settings</h2>
  </div>
);
