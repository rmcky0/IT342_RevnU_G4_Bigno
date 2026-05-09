import React from "react";

export const StaffProfileSkeleton = () => {
  return (
    <div className="flex flex-col flex-1 h-full max-h-full space-y-5 overflow-hidden">
      <div className="shrink-0 flex items-center gap-4">
        <div className="w-10 h-10 bg-gray-200 rounded-lg animate-pulse"></div>
        <div className="w-48 h-8 bg-gray-200 rounded-lg animate-pulse"></div>
      </div>
      <div className="shrink-0 bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col md:flex-row items-center gap-6">
        <div className="w-20 h-20 rounded-2xl bg-gray-200 animate-pulse shrink-0"></div>
        <div className="flex-1 space-y-3 w-full">
          <div className="w-48 h-8 bg-gray-200 rounded-lg animate-pulse"></div>
          <div className="flex gap-3">
            <div className="w-32 h-8 bg-gray-100 rounded-lg animate-pulse"></div>
            <div className="w-40 h-8 bg-gray-100 rounded-lg animate-pulse"></div>
          </div>
        </div>
      </div>
      <div className="flex-1 bg-white rounded-xl shadow-sm border border-gray-100 flex flex-col overflow-hidden">
        <div className="h-12 bg-gray-100 animate-pulse"></div>
        <div className="p-5 space-y-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="flex justify-between items-center">
              <div className="w-32 h-4 bg-gray-100 rounded animate-pulse"></div>
              <div className="w-24 h-4 bg-gray-100 rounded animate-pulse"></div>
              <div className="w-40 h-4 bg-gray-100 rounded animate-pulse"></div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
