import React from "react";
import { Lock, CheckCircle } from "lucide-react";

export const EodLockCard = ({ isLocked, lockLoading, onLock }) => (
  <div className="bg-[#7c83fd] rounded-xl flex flex-col items-center justify-center text-center relative overflow-hidden shadow-lg shadow-[#7c83fd]/20 min-h-0 group">
    {/* Subtle grid */}
    <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.07)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.07)_1px,transparent_1px)] bg-[size:24px_24px]" />

    {/* Blobs */}
    <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full blur-2xl -mr-10 -mt-10 pointer-events-none group-hover:scale-110 transition-transform duration-700" />
    <div className="absolute bottom-0 left-0 w-24 h-24 bg-indigo-900/10 rounded-full blur-xl -ml-8 -mb-8 pointer-events-none" />

    <div className="relative z-10 flex flex-col items-center px-6 py-6 w-full">
      <div className="bg-white/20 p-3 rounded-xl mb-4 backdrop-blur-md border border-white/20 shadow-sm shrink-0">
        {isLocked ? (
          <CheckCircle className="w-6 h-6 text-white" />
        ) : (
          <Lock className="w-6 h-6 text-white" />
        )}
      </div>

      <h3 className="text-white font-bold text-base mb-1 tracking-tight shrink-0">
        {isLocked ? "EOD Finalized" : "End of Day"}
      </h3>
      <p className="text-indigo-100 text-[10px] font-bold mb-5 uppercase tracking-widest shrink-0">
        {isLocked ? "Records permanently locked" : "Ready to close shift"}
      </p>

      {isLocked ? (
        <div className="w-full py-2.5 bg-white/20 text-white font-bold text-sm rounded-xl border border-white/20 flex items-center justify-center gap-2 mt-auto shrink-0 backdrop-blur-sm">
          <CheckCircle className="w-4 h-4" /> Finalized
        </div>
      ) : (
        <button
          onClick={onLock}
          disabled={lockLoading}
          className="w-full py-2.5 bg-white text-[#7c83fd] font-bold text-sm rounded-xl shadow-md hover:bg-gray-50 active:scale-[0.98] transition-all disabled:opacity-70 disabled:cursor-not-allowed mt-auto shrink-0"
        >
          {lockLoading ? "Processing…" : "Lock Records"}
        </button>
      )}
    </div>
  </div>
);
