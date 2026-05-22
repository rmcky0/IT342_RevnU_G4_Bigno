import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAnalytics } from "./hooks/useAnalytics";
import { useAuth } from "../auth/context/AuthContext";
import {
  Calendar,
  RefreshCw,
  ShoppingCart,
  CreditCard,
  Wallet,
  ListOrdered,
} from "lucide-react";

import { KPICard, RecordCountCard } from "./components/DashboardCard";
import { CalendarModal } from "./components/CalendarModal";
import { NotificationBell } from "../notifications/components/NotificationBell";
import { LoadingSpinner } from "./components/LoadingSpinner";
import { ErrorBanner } from "./components/ErrorBanner";
import { SalaryInfoBanner } from "./components/SalaryInfoBanner";
import { SalesTrendChart } from "./components/SalesTrendChart";
import { SalesByTagChart } from "./components/SalesByTagChart";
import { WeeklyProfitChart } from "./components/WeeklyProfitChart";
import { EodLockCard } from "./components/EodLockCard";
import { getFormattedDate } from "./utils/dateUtils";
import { EODLockConfirmModal } from "../../shared/components/EODLockConfirmModal";

export const Analytics = () => {
  const {
    data,
    profitTrend,
    loading,
    error,
    isEmpty,
    isLocked,
    lockLoading,
    lockError,
    holidayName,
    salesDelta,
    expensesDelta,
    profitDelta,
    handleLockRecords,
    refetch,
  } = useAnalytics();

  const { user } = useAuth();
  const navigate = useNavigate();
  const [showCalendar, setShowCalendar] = useState(false);
  const [showLockModal, setShowLockModal] = useState(false);

  const confirmLock = async () => {
    await handleLockRecords();
    setShowLockModal(false);
  };
  if (loading) return <LoadingSpinner />;

  return (
    <div className="flex flex-col flex-1 h-full min-h-0 overflow-y-auto overflow-x-hidden text-[#1e1b4b] custom-scrollbar">
      <div className="flex flex-col flex-1 min-h-[700px] gap-5 pb-4 pr-1">
        {showCalendar && (
          <CalendarModal onClose={() => setShowCalendar(false)} />
        )}

        {/* ── Header ─────────────────────────────────────────────────────────── */}
        <div className="shrink-0 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3 mb-1">
              <h1 className="text-2xl font-bold tracking-tight">
                Dashboard Overview
              </h1>
              {holidayName && (
                <span className="flex items-center gap-1.5 text-[10px] font-bold text-amber-700 bg-amber-50 border border-amber-200/60 px-2 py-0.5 rounded-md uppercase tracking-wider shadow-sm">
                  <Calendar className="w-3 h-3" /> {holidayName}
                </span>
              )}
            </div>
            <p className="text-sm font-medium text-gray-500">
              {getFormattedDate()}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex items-center bg-white p-1 rounded-xl shadow-sm border border-gray-100">
              <button
                onClick={refetch}
                className="p-2 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-lg transition-colors"
                title="Refresh Analytics"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
              <div className="w-px h-4 bg-gray-200 mx-1" />
              <button
                onClick={() => setShowCalendar(true)}
                className="p-2 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-lg transition-colors"
                title="View Calendar"
              >
                <Calendar className="w-4 h-4" />
              </button>
              <div className="w-px h-4 bg-gray-200 mx-1" />
              <NotificationBell
                holidayName={holidayName}
                holidayDate={data?.date}
              />
            </div>
          </div>
        </div>

        {/* ── Error Banner ───────────────────────────────────────────────────── */}
        <ErrorBanner error={error} onRetry={refetch} />

        {/* ── Top KPIs ───────────────────────────────────────────────────────── */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5 shrink-0">
          <KPICard
            title="Total Sales (Today)"
            value={data.totalSales}
            delta={salesDelta}
            isEmpty={isEmpty}
            emptyMessage="Waiting on your first sale."
            icon={ShoppingCart}
            color="indigo"
          />
          <KPICard
            title="Total Expenses (Today)"
            value={data.totalExpenses}
            delta={expensesDelta}
            isEmpty={isEmpty}
            emptyMessage="Log expenses to see totals."
            invertDelta
            icon={CreditCard}
            color="red"
          />
          <KPICard
            title="Net Profit"
            value={data.netProfit}
            delta={profitDelta}
            isEmpty={isEmpty}
            emptyMessage="Profit appears once activity starts."
            highlight
            icon={Wallet}
            color="emerald"
          />
        </div>

        {/* ── Salary Info Banner ─────────────────────────────────────────────── */}
        <SalaryInfoBanner amount={data.totalSalaries} />

        {/* ── Middle Row: Charts ─────────────────────────────────────────────── */}
        <div className="flex-[1.3] grid grid-cols-1 lg:grid-cols-3 gap-5 min-h-[300px]">
          <SalesTrendChart
            data={data.salesTrend}
            yesterdayLabel={data.yesterdayLabel}
          />
          {/* Bug fix: was data.categories — normalized shape uses data.tags */}
          <SalesByTagChart tags={data.tags} />
        </div>

        {/* ── Bottom Row: Records + Profit Area + Lock ───────────────────────── */}
        <div className="flex-1 grid grid-cols-1 lg:grid-cols-4 gap-5 min-h-[220px]">
          <div className="flex flex-col gap-4 min-h-0">
            <RecordCountCard
              title="Sale Records"
              count={data.saleRecordsCount}
              icon={ListOrdered}
            />
            <RecordCountCard
              title="Expense Records"
              count={data.expenseRecordsCount}
              icon={ListOrdered}
            />
          </div>

          <WeeklyProfitChart data={profitTrend} />

          <EodLockCard
            isLocked={isLocked}
            lockLoading={lockLoading}
            lockError={lockError}
            onLock={() => setShowLockModal(true)}
          />
        </div>
      </div>

      {/* Insert the Strict Confirmation Modal */}
      <EODLockConfirmModal
        isOpen={showLockModal}
        onClose={() => setShowLockModal(false)}
        onConfirm={confirmLock}
        lockLoading={lockLoading}
      />
    </div>
  );
};
