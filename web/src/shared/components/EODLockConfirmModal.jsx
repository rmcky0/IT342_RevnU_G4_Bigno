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
    if (!isOpen) return;
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, [isOpen]);

  if (!isOpen) return null;

  const isConfirmed = confirmText === "LOCK EOD";

  const fmt = (n) =>
    Number(n || 0).toLocaleString(undefined, { minimumFractionDigits: 2 });

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/20 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
      <div className="bg-white rounded-2xl max-w-md w-full border border-gray-100 overflow-hidden flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-150">
        {/* ── Header ─────────────────────────────────────────── */}
        <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between gap-3 shrink-0">
          <div className="flex items-center gap-2.5">
            {/* Square icon — matches DeleteConfirmModal pattern */}
            <div className="w-8 h-8 bg-red-50 border border-red-100 rounded-xl flex items-center justify-center shrink-0">
              <AlertTriangle className="w-4 h-4 text-red-500" />
            </div>
            <div>
              <h3 className="text-[15px] font-semibold text-[#1e1b4b] leading-tight">
                Confirm end of day
              </h3>
              <p className="text-[11px] text-red-500 font-medium mt-0.5 tracking-wide">
                This action is permanent
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors shrink-0"
            aria-label="Close"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* ── Body ───────────────────────────────────────────── */}
        <div className="p-5 flex-1 overflow-y-auto flex flex-col gap-4">
          {fetching ? (
            /* Loading state */
            <div className="flex flex-col items-center justify-center py-12 gap-3">
              <div className="w-7 h-7 border-2 border-gray-200 border-t-[#7c6ff7] rounded-full animate-spin" />
              <p className="text-[12px] font-medium text-gray-400 uppercase tracking-widest">
                Calculating…
              </p>
            </div>
          ) : (
            <>
              {/* Timestamp row */}
              <div className="flex items-center gap-3 bg-gray-50 border border-gray-100 rounded-xl px-4 py-3">
                <Clock className="w-[18px] h-[18px] text-[#7c6ff7] shrink-0" />
                <div>
                  <p className="text-[13px] font-semibold text-[#1e1b4b] leading-tight">
                    {currentTime.toLocaleDateString("en-US", {
                      weekday: "long",
                      month: "long",
                      day: "numeric",
                      year: "numeric",
                    })}
                  </p>
                  <p className="text-[11px] text-gray-400 tabular-nums mt-0.5">
                    {currentTime.toLocaleTimeString("en-US", {
                      hour: "2-digit",
                      minute: "2-digit",
                      second: "2-digit",
                    })}
                  </p>
                </div>
              </div>

              {/* Daily summary */}
              <div>
                <p className="text-[10px] font-semibold text-gray-400 uppercase tracking-widest mb-2">
                  Daily summary
                </p>
                <div className="flex flex-col gap-1.5">
                  {/* Sales row */}
                  <div className="flex items-center justify-between bg-gray-50 border border-gray-100 rounded-lg px-3 py-2.5">
                    <div className="flex items-center gap-2 text-[12px] font-medium text-[#1e1b4b]">
                      <ShoppingCart className="w-3.5 h-3.5 text-[#7c6ff7]" />
                      Sales
                      <span className="text-[11px] font-normal text-gray-400">
                        ({stats?.saleRecordsCount || 0} records)
                      </span>
                    </div>
                    <span className="text-[13px] font-semibold text-[#7c6ff7] tabular-nums">
                      ₱{fmt(stats?.totalSales)}
                    </span>
                  </div>

                  {/* Expenses row */}
                  <div className="flex items-center justify-between bg-gray-50 border border-gray-100 rounded-lg px-3 py-2.5">
                    <div className="flex items-center gap-2 text-[12px] font-medium text-[#1e1b4b]">
                      <CreditCard className="w-3.5 h-3.5 text-red-500" />
                      Expenses
                      <span className="text-[11px] font-normal text-gray-400">
                        ({stats?.expenseRecordsCount || 0} records)
                      </span>
                    </div>
                    <span className="text-[13px] font-semibold text-red-500 tabular-nums">
                      ₱{fmt(stats?.totalExpenses)}
                    </span>
                  </div>

                  {/* Net profit row — visually separated as a subtotal */}
                  <div className="flex items-center justify-between bg-gray-50 border border-gray-200 rounded-lg px-3 py-2.5 mt-0.5">
                    <div className="flex items-center gap-2 text-[12px] font-medium text-[#1e1b4b]">
                      <Wallet className="w-3.5 h-3.5 text-emerald-500" />
                      Net profit
                    </div>
                    <span className="text-[13px] font-bold text-emerald-600 tabular-nums">
                      ₱{fmt(stats?.netProfit)}
                    </span>
                  </div>
                </div>
              </div>

              {/* Confirmation input */}
              <div className="bg-red-50 border border-red-100 rounded-xl px-4 py-3">
                <p className="text-[11px] text-red-700 text-center leading-relaxed mb-2.5">
                  Type{" "}
                  <span className="font-mono font-semibold tracking-widest bg-red-100 text-red-800 px-1.5 py-0.5 rounded">
                    LOCK EOD
                  </span>{" "}
                  to confirm
                </p>
                <input
                  type="text"
                  value={confirmText}
                  onChange={(e) => setConfirmText(e.target.value)}
                  placeholder="LOCK EOD"
                  autoComplete="off"
                  className="w-full text-center px-4 py-2.5 bg-white border border-red-200 focus:border-red-400 focus:ring-2 focus:ring-red-100 rounded-lg text-[13px] font-mono font-semibold tracking-widest uppercase text-[#1e1b4b] outline-none transition-all placeholder:text-red-200 placeholder:font-sans placeholder:tracking-normal placeholder:font-normal"
                />
              </div>
            </>
          )}
        </div>

        {/* ── Footer ─────────────────────────────────────────── */}
        <div className="px-5 py-3.5 border-t border-gray-100 flex gap-2.5 shrink-0">
          <button
            onClick={onClose}
            className="flex-1 py-2.5 bg-gray-50 hover:bg-gray-100 text-gray-600 border border-gray-200 rounded-lg text-[13px] font-semibold transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={!isConfirmed || lockLoading || fetching}
            className="flex-1 py-2.5 bg-red-500 hover:bg-red-600 text-white rounded-lg text-[13px] font-semibold transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1.5"
          >
            <ShieldCheck className="w-3.5 h-3.5" />
            {lockLoading ? "Locking…" : "Close day"}
          </button>
        </div>
      </div>
    </div>
  );
};
