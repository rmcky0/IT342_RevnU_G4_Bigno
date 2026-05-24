import React from "react";

export const StaffProfileSkeleton = () => (
  <div className="flex flex-col flex-1 h-full max-h-full gap-3 overflow-hidden animate-pulse">
    {/* Header */}
    <div className="shrink-0 flex items-center gap-2.5">
      <div className="w-9 h-9 bg-gray-100 rounded-xl" />
      <div className="w-9 h-9 bg-gray-100 rounded-xl" />
      <div className="flex flex-col gap-1.5">
        <div className="w-28 h-4 bg-gray-100 rounded-full" />
        <div className="w-40 h-3 bg-gray-100 rounded-full" />
      </div>
    </div>

    {/* Profile card */}
    <div className="shrink-0 bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] p-5 flex flex-col sm:flex-row items-start sm:items-center gap-5">
      <div className="w-16 h-16 rounded-xl bg-gray-100 shrink-0" />
      <div className="flex-1 min-w-0 flex flex-col gap-2.5">
        <div className="w-44 h-5 bg-gray-100 rounded-full" />
        <div className="flex gap-2">
          <div className="w-28 h-6 bg-gray-100 rounded-full" />
          <div className="w-36 h-6 bg-gray-100 rounded-full" />
        </div>
      </div>
      <div className="sm:pl-5 sm:border-l border-gray-100 pt-4 sm:pt-0 border-t sm:border-t-0 w-full sm:w-auto shrink-0 flex flex-col gap-2">
        <div className="w-24 h-3 bg-gray-100 rounded-full" />
        <div className="w-36 h-7 bg-gray-100 rounded-full" />
        <div className="w-20 h-3 bg-gray-100 rounded-full" />
      </div>
    </div>

    {/* Section header */}
    <div className="shrink-0 flex items-center justify-between">
      <div className="flex items-center gap-2">
        <div className="w-7 h-7 bg-gray-100 rounded-xl" />
        <div className="w-28 h-4 bg-gray-100 rounded-full" />
      </div>
      <div className="w-16 h-5 bg-gray-100 rounded-full" />
    </div>

    {/* Table skeleton */}
    <div className="flex-1 bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex flex-col min-h-0 overflow-hidden">
      <div className="h-10 bg-gray-50 border-b border-gray-100 shrink-0" />
      <div className="flex flex-col divide-y divide-gray-50 p-0">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="flex items-center justify-between px-5 py-3.5 gap-4">
            <div className="w-40 h-3.5 bg-gray-100 rounded-full" />
            <div className="w-24 h-3.5 bg-gray-100 rounded-full ml-auto" />
            <div className="w-32 h-3 bg-gray-100 rounded-full" />
          </div>
        ))}
      </div>
    </div>
  </div>
);
