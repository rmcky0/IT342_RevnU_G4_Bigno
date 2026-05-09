import React from "react";
import { Lock, CheckCircle } from "lucide-react";

export const EodLockCard = ({ isLocked, lockLoading, lockError, onLock }) => (
  <div className="bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] p-5 rounded-xl flex flex-col items-center justify-center text-center relative overflow-hidden shadow-lg shadow-indigo-200 min-h-0">
    {/* Decorative blobs */}
    <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full blur-2xl -mr-10 -mt-10 pointer-events-none" />
    <div className="absolute bottom-0 left-0 w-24 h-24 bg-[#1e1b4b]/10 rounded-full blur-xl -ml-8 -mb-8 pointer-events-none" />

    <div className="bg-white/20 p-3 rounded-2xl mb-3 backdrop-blur-sm border border-white/30 shadow-sm z-10 shrink-0">
      {isLocked ? (
        <CheckCircle className="w-5 h-5 text-emerald-300" />
      ) : (
        <Lock className="w-5 h-5 text-white" />
      )}
    </div>

    <h3 className="text-white font-bold text-base mb-1 z-10 tracking-tight shrink-0">
      {isLocked ? "EOD Finalized" : "End of Day"}
    </h3>
    <p className="text-white/80 text-[10px] font-bold mb-4 z-10 uppercase tracking-wider shrink-0">
      {isLocked ? "Records are permanently locked" : "Ready to close"}
    </p>

    {lockError && (
      <p className="text-red-100 text-[10px] font-bold uppercase tracking-wider bg-red-500/30 rounded-lg px-2 py-1.5 mb-3 z-10 w-full text-center border border-red-400/30 backdrop-blur-sm shrink-0">
        {lockError}
      </p>
    )}

    {/*Conditional Button/Badge rendering */}
    {isLocked ? (
      <div className="w-full py-2.5 bg-emerald-500/20 text-emerald-50 font-bold text-sm rounded-xl border border-emerald-400/30 flex items-center justify-center gap-2 mt-auto z-10 shrink-0 backdrop-blur-sm">
        <CheckCircle className="w-4 h-4" /> Finalized
      </div>
    ) : (
      <button
        onClick={onLock}
        disabled={lockLoading}
        className="w-full py-2.5 bg-white text-[#1e1b4b] font-bold text-sm rounded-xl shadow-md hover:bg-gray-50 transition-all active:scale-95 z-10 disabled:opacity-80 disabled:cursor-not-allowed disabled:active:scale-100 mt-auto shrink-0"
      >
        {lockLoading ? "Processing…" : "Lock Records"}
      </button>
    )}
  </div>
);
