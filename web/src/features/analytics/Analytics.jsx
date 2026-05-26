import React, { useState } from "react";
import { useAnalytics } from "./hooks/useAnalytics";
import { useAuth } from "../auth/context/AuthContext";
import {
  Calendar,
  RefreshCw,
  ShoppingCart,
  CreditCard,
  Wallet,
  ListOrdered,
  LayoutDashboard,
} from "lucide-react";

import { KPICard, RecordCountCard } from "./components/DashboardCard";
import { CalendarModal } from "./components/CalendarModal";
import { NotificationBell } from "../notifications/components/NotificationBell";
import { SalaryInfoBanner } from "./components/banners/SalaryInfoBanner";
import { ErrorBanner } from "./components/banners/ErrorBanner";
import { LoadingSpinner } from "./components/banners/LoadingSpinner";
import { SalesTrendChart } from "./components/SalesTrendChart";
import { SalesByCategoryChart } from "./components/SalesByCategoryChart";
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
    holidayName,
    openSalesTotal,
    openExpensesTotal,
    openTotalSalaries,
    openNetProfit,
    openCategories,
    openSaleRecordsCount,
    openExpenseRecordsCount,
    handleLockRecords,
    refetch,
  } = useAnalytics();

  const { user } = useAuth();
  const [showCalendar, setShowCalendar] = useState(false);
  const [showLockModal, setShowLockModal] = useState(false);

  const confirmLock = async () => {
    await handleLockRecords();
    setShowLockModal(false);
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="h-full flex flex-col text-[#1e1b4b] overflow-y-auto lg:overflow-hidden custom-scrollbar">
      {showCalendar && <CalendarModal onClose={() => setShowCalendar(false)} />}

      {/* ── Header ── */}
      <div className="shrink-0 flex items-center justify-between gap-3 mb-4 flex-wrap">
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
            <LayoutDashboard className="w-4.5 h-4.5 text-white" />
          </div>
          <div className="min-w-0">
            <div className="flex items-center gap-2 flex-wrap">
              <h1 className="text-lg font-bold tracking-tight truncate">
                Dashboard
              </h1>
              {holidayName && (
                <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-amber-50 text-amber-600 text-[10px] font-bold rounded-full border border-amber-200 uppercase tracking-wider shrink-0">
                  <Calendar className="w-2.5 h-2.5" /> {holidayName}
                </span>
              )}
            </div>
            <p className="text-[11px] font-medium text-gray-400 mt-0.5">
              {getFormattedDate()}
            </p>
          </div>
        </div>

        {/* Action pill */}
        <div className="flex items-center bg-white rounded-xl border border-gray-200 shrink-0">
          <button
            onClick={refetch}
            className="p-2.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] rounded-l-xl transition-colors"
            title="Refresh"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
          <div className="w-px h-5 bg-gray-100" />
          <button
            onClick={() => setShowCalendar(true)}
            className="p-2.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] transition-colors"
            title="Holidays"
          >
            <Calendar className="w-4 h-4" />
          </button>
          <div className="w-px h-5 bg-gray-100" />
          <div className="px-1">
            <NotificationBell
              holidayName={holidayName}
              holidayDate={data?.date}
            />
          </div>
        </div>
      </div>

      <ErrorBanner error={error} onRetry={refetch} />

      {/* ── KPI Row ── */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3 shrink-0 mb-4">
        <KPICard
          title="Total Sales"
          value={openSalesTotal}
          isEmpty={isEmpty}
          emptyMessage="Waiting on first sale."
          icon={ShoppingCart}
          color="indigo"
        />
        <KPICard
          title="Total Expenses"
          value={openExpensesTotal}
          isEmpty={isEmpty}
          emptyMessage="Log expenses to see totals."
          invertDelta
          icon={CreditCard}
          color="red"
          payroll={openTotalSalaries}
        />
        <KPICard
          title="Net Profit"
          value={openNetProfit}
          isEmpty={isEmpty}
          emptyMessage="Requires activity."
          highlight
          icon={Wallet}
          color="emerald"
        />
      </div>

      {/* ── Middle Row: Main Charts ── */}
      <div className="flex-[1.5] min-h-[300px] grid grid-cols-1 lg:grid-cols-3 gap-3 mb-4">
        <SalesTrendChart
          data={data.salesTrend}
          yesterdayLabel={data.yesterdayLabel}
        />
        <SalesByCategoryChart categories={openCategories} />
      </div>

      {/* ── Bottom Row: Mini Charts & Actions ── */}
      <div className="flex-1 min-h-[220px] grid grid-cols-1 lg:grid-cols-4 gap-3 pb-4">
        <div className="flex flex-col gap-3 min-h-0">
          <RecordCountCard
            title="Sale Records"
            count={openSaleRecordsCount}
            icon={ListOrdered}
          />
          <RecordCountCard
            title="Expense Records"
            count={openExpenseRecordsCount}
            icon={ListOrdered}
          />
        </div>
        <WeeklyProfitChart data={profitTrend} />
        <EodLockCard
          isLocked={isLocked}
          lockLoading={lockLoading}
          onLock={() => setShowLockModal(true)}
        />
      </div>

      <EODLockConfirmModal
        isOpen={showLockModal}
        onClose={() => setShowLockModal(false)}
        onConfirm={confirmLock}
        lockLoading={lockLoading}
        openSalesTotal={openSalesTotal}
        openExpensesTotal={openExpensesTotal}
        openTotalSalaries={openTotalSalaries}
        openNetProfit={openNetProfit}
        openSaleRecordsCount={openSaleRecordsCount}
        openExpenseRecordsCount={openExpenseRecordsCount}
      />
    </div>
  );
};
