import React, { useState, useEffect } from "react";
import {
  X,
  AlertTriangle,
  ShieldCheck,
  ShoppingCart,
  CreditCard,
  Wallet,
  Clock,
} from "lucide-react";
import api from "../api/axios";

export const EODLockConfirmModal = ({
  isOpen,
  onClose,
  onConfirm,
  lockLoading,
}) => {
  const [stats, setStats] = useState(null);
  const [fetching, setFetching] = useState(true);
  const [confirmText, setConfirmText] = useState("");
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    if (isOpen) {
      setFetching(true);
      setConfirmText("");
      api
        .get("/analytics/daily")
        .then((res) => setStats(res.data.data))
        .catch((err) => console.error("Failed to fetch EOD stats:", err))
        .finally(() => setFetching(false));
    }
  }, [isOpen]);

  useEffect(() => {
    if (isOpen) {
      const timer = setInterval(() => setCurrentTime(new Date()), 1000);
      return () => clearInterval(timer);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const isConfirmed = confirmText === "LOCK EOD";

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/40 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl relative border border-gray-100 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-6 py-5 border-b border-gray-100 bg-red-50/50 flex items-center justify-between shrink-0">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-red-100 text-red-600 rounded-lg">
              <AlertTriangle className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-[#1e1b4b]">
                Confirm End of Day
              </h3>
              <p className="text-red-500 text-xs font-bold uppercase tracking-wider mt-0.5">
                This action is permanent
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-white rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content Body */}
        <div className="p-6 overflow-y-auto custom-scrollbar flex-1">
          {fetching ? (
            <div className="flex flex-col items-center justify-center py-10 space-y-3">
              <div className="w-8 h-8 border-4 border-indigo-100 border-t-[#7c83fd] rounded-full animate-spin"></div>
              <p className="text-sm font-bold text-gray-400 uppercase tracking-wider">
                Calculating Day...
              </p>
            </div>
          ) : (
            <div className="space-y-6">
              {/* Date & Time */}
              <div className="flex flex-col items-center text-center bg-gray-50 rounded-xl p-4 border border-gray-100">
                <Clock className="w-6 h-6 text-[#7c83fd] mb-2" />
                <p className="text-[#1e1b4b] font-bold text-lg">
                  {currentTime.toLocaleDateString("en-US", {
                    weekday: "long",
                    month: "long",
                    day: "numeric",
                    year: "numeric",
                  })}
                </p>
                <p className="text-gray-500 font-medium text-sm">
                  {currentTime.toLocaleTimeString("en-US", {
                    hour: "2-digit",
                    minute: "2-digit",
                    second: "2-digit",
                  })}
                </p>
              </div>

              {/* Stats Grid */}
              <div className="space-y-3">
                <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                  Daily Summary Preview
                </p>

                <div className="flex items-center justify-between bg-indigo-50/50 p-3 rounded-lg border border-indigo-100/50">
                  <div className="flex items-center gap-2 text-[#1e1b4b] font-semibold text-sm">
                    <ShoppingCart className="w-4 h-4 text-[#7c83fd]" /> Sales (
                    {stats?.saleRecordsCount || 0})
                  </div>
                  <span className="font-bold text-[#7c83fd]">
                    ₱
                    {Number(stats?.totalSales || 0).toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                    })}
                  </span>
                </div>

                <div className="flex items-center justify-between bg-red-50/50 p-3 rounded-lg border border-red-100/50">
                  <div className="flex items-center gap-2 text-[#1e1b4b] font-semibold text-sm">
                    <CreditCard className="w-4 h-4 text-red-500" /> Expenses (
                    {stats?.expenseRecordsCount || 0})
                  </div>
                  <span className="font-bold text-red-500">
                    ₱
                    {Number(stats?.totalExpenses || 0).toLocaleString(
                      undefined,
                      { minimumFractionDigits: 2 },
                    )}
                  </span>
                </div>

                <div className="flex items-center justify-between bg-emerald-50/50 p-3 rounded-lg border border-emerald-100/50">
                  <div className="flex items-center gap-2 text-[#1e1b4b] font-semibold text-sm">
                    <Wallet className="w-4 h-4 text-emerald-500" /> Net Profit
                  </div>
                  <span className="font-black text-emerald-600">
                    ₱
                    {Number(stats?.netProfit || 0).toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                    })}
                  </span>
                </div>
              </div>

              {/* Strict Confirmation Input */}
              <div className="bg-white border-2 border-red-100 rounded-xl p-4">
                <label className="block text-sm font-bold text-[#1e1b4b] mb-2 text-center">
                  Type <span className="text-red-500 select-all">LOCK EOD</span>{" "}
                  to confirm
                </label>
                <input
                  type="text"
                  value={confirmText}
                  onChange={(e) => setConfirmText(e.target.value)}
                  placeholder="LOCK EOD"
                  className="w-full text-center px-4 py-3 bg-gray-50 rounded-lg border border-gray-200 focus:border-red-400 focus:ring-4 focus:ring-red-50 font-bold text-[#1e1b4b] transition-all tracking-widest uppercase outline-none"
                  autoComplete="off"
                />
              </div>
            </div>
          )}
        </div>

        {/* Footer Actions */}
        <div className="p-4 border-t border-gray-100 bg-gray-50 flex gap-3 shrink-0">
          <button
            onClick={onClose}
            className="flex-1 py-2.5 bg-white text-gray-600 rounded-lg font-semibold text-sm hover:bg-gray-100 transition-colors border border-gray-200"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={!isConfirmed || lockLoading || fetching}
            className="flex-1 py-2.5 bg-red-500 text-white rounded-lg font-semibold text-sm shadow-md hover:bg-red-600 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {lockLoading ? (
              "Locking..."
            ) : (
              <>
                <ShieldCheck className="w-4 h-4" /> Close Day
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
