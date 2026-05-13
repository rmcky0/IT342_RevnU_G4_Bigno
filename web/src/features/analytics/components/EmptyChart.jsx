import React from "react";
import { BarChart2 } from "lucide-react";

export const EmptyChart = ({ message }) => (
  <div className="w-full h-full flex flex-col items-center justify-center bg-gray-50/50 rounded-xl border border-dashed border-gray-200 p-6">
    <div className="w-12 h-12 bg-white rounded-xl shadow-sm border border-gray-100 flex items-center justify-center mb-3">
      <BarChart2 className="w-6 h-6 text-gray-300" />
    </div>
    <p className="text-xs text-gray-400 font-bold text-center uppercase tracking-wider max-w-[200px] leading-relaxed">
      {message}
    </p>
  </div>
);
