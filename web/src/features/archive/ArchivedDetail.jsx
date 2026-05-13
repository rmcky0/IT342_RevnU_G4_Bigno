import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { archiveApi } from "./api/archiveApi";
import * as cache from "../../shared/cache/dataCache";
import {
  ArrowLeft,
  Lock,
  TrendingUp,
  TrendingDown,
  CreditCard,
  Wallet,
  Tag as TagIcon,
  Inbox,
  FileText,
  ShoppingCart,
} from "lucide-react";

export const ArchivedDetail = () => {
  const { date } = useParams();
  const navigate = useNavigate();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const CACHE_KEY = `archive:detail:${date}`;

  useEffect(() => {
    if (!date) return;
    const load = async () => {
      const cached = cache.get(CACHE_KEY);
      if (cached) {
        setDetail(cached);
        setLoading(false);
        return;
      }

      try {
        const res = await archiveApi.getDayDetail(date);
        const data = res?.data;
        setDetail(data);
        cache.set(CACHE_KEY, data, 10 * 60 * 1000);
      } catch {
        setError(
          "Failed to load day detail. This date may not have been closed yet.",
        );
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [date]);

  const fmt = (n) =>
    parseFloat(n || 0).toLocaleString("en-PH", { minimumFractionDigits: 2 });

  const formatDate = (dateStr) =>
    new Date(dateStr + "T00:00:00").toLocaleDateString("en-PH", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });

  const formatTime = (dateStr) =>
    new Date(dateStr).toLocaleString("en-PH", {
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });

  if (loading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div className="h-8 bg-gray-100 rounded-lg w-64" />
        <div className="grid grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="h-28 bg-gray-100 rounded-xl" />
          ))}
        </div>
        <div className="h-64 bg-gray-100 rounded-xl" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-4 text-[#1e1b4b]">
        <button
          onClick={() => navigate("/archived")}
          className="flex items-center gap-2 text-sm font-semibold text-gray-400 hover:text-[#7c83fd] transition-colors"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Archived
        </button>
        <div className="px-5 py-4 bg-red-50 border border-red-100 rounded-xl text-red-600 text-sm font-medium">
          {error}
        </div>
      </div>
    );
  }

  if (!detail) return null;

  const { summary, sales = [], expenses = [] } = detail;
  const profit = parseFloat(summary?.netProfit || 0);
  const isProfit = profit >= 0;

  const tagTotals = {};
  sales.forEach((s) => {
    (s.tags || []).forEach((tag) => {
      tagTotals[tag] = (tagTotals[tag] || 0) + parseFloat(s.amount || 0);
    });
  });
  const topTags = Object.entries(tagTotals)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5);

  const COLORS = ["#7c83fd", "#ff5a8d", "#ffb800", "#4ade80", "#a78bfa"];

  return (
    <div className="space-y-6 text-[#1e1b4b]">
      {/* Header */}
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate("/archived")}
          className="p-2 bg-white rounded-lg border border-gray-200 shadow-sm text-gray-500 hover:text-[#7c83fd] hover:border-indigo-100 transition-all"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <h2 className="text-2xl font-bold tracking-tight">
              {formatDate(date)}
            </h2>
            <span className="flex items-center gap-1 px-2.5 py-1 bg-indigo-50 text-[#7c83fd] text-xs font-bold rounded-lg border border-indigo-100">
              <Lock className="w-3 h-3" /> EOD Locked
            </span>
          </div>
          <p className="text-xs text-gray-400 font-medium mt-0.5">
            {sales.length} sales · {expenses.length} expenses
            {summary?.reportSent && " · Email report sent ✓"}
          </p>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          {
            label: "Total Sales",
            value: summary?.totalSales,
            icon: ShoppingCart,
            color: "text-[#7c83fd]",
            bg: "from-indigo-50 to-[#7c83fd]/10",
            border: "border-indigo-100",
          },
          {
            label: "Total Expenses",
            value: summary?.totalExpenses,
            icon: CreditCard,
            color: "text-amber-600",
            bg: "from-amber-50 to-amber-500/10",
            border: "border-amber-100",
          },
          {
            label: "Salaries Paid",
            value: summary?.totalSalaries,
            icon: Wallet,
            color: "text-violet-600",
            bg: "from-violet-50 to-violet-500/10",
            border: "border-violet-100",
          },
          {
            label: "Net Profit",
            value: summary?.netProfit,
            icon: isProfit ? TrendingUp : TrendingDown,
            color: isProfit ? "text-emerald-600" : "text-red-500",
            bg: isProfit
              ? "from-emerald-50 to-emerald-500/10"
              : "from-red-50 to-red-500/10",
            border: isProfit ? "border-emerald-100" : "border-red-100",
          },
        ].map(({ label, value, icon: Icon, color, bg, border }) => (
          <div
            key={label}
            className={`bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 relative overflow-hidden group`}
          >
            <div
              className={`absolute -right-3 -top-3 opacity-30 group-hover:scale-110 transition-transform duration-500 pointer-events-none ${color}`}
            >
              <Icon className="w-20 h-20" />
            </div>
            <div className="relative z-10">
              <div
                className={`inline-flex p-2 rounded-lg bg-gradient-to-br ${bg} border ${border} mb-3`}
              >
                <Icon className={`w-4 h-4 ${color}`} />
              </div>
              <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-1">
                {label}
              </p>
              <p className={`text-xl font-black ${color}`}>₱{fmt(value)}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Tag Breakdown */}
      {topTags.length > 0 && (
        <div className="bg-white rounded-xl border border-gray-100 shadow-[0_4px_20px_rgb(0,0,0,0.03)] p-5">
          <div className="flex items-center gap-2 mb-4">
            <TagIcon className="w-4 h-4 text-[#7c83fd]" />
            <h3 className="font-bold text-sm text-[#1e1b4b] uppercase tracking-wider">
              Sales by Tag
            </h3>
          </div>
          <div className="flex flex-wrap gap-3">
            {topTags.map(([tag, amount], i) => (
              <div
                key={tag}
                className="flex items-center gap-2 px-3 py-2 bg-gray-50 rounded-lg border border-gray-100"
              >
                <div
                  className="w-2.5 h-2.5 rounded-full"
                  style={{ backgroundColor: COLORS[i % COLORS.length] }}
                />
                <span className="text-xs font-bold text-gray-600 uppercase">
                  {tag}
                </span>
                <span
                  className="text-xs font-black"
                  style={{ color: COLORS[i % COLORS.length] }}
                >
                  ₱{fmt(amount)}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Sales Table */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-[0_4px_20px_rgb(0,0,0,0.03)] overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-100 flex items-center gap-2">
          <ShoppingCart className="w-4 h-4 text-[#7c83fd]" />
          <h3 className="font-bold text-sm text-[#1e1b4b]">Sales Records</h3>
          <span className="ml-auto text-xs font-bold text-gray-400">
            {sales.length} entries
          </span>
        </div>
        {sales.length === 0 ? (
          <EmptyTable message="No sale records for this day." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse whitespace-nowrap">
              <thead className="bg-gradient-to-r from-[#8f9df7] to-[#9faaf5] text-white">
                <tr>
                  {["Time", "Amount", "Tags", "Description"].map((h) => (
                    <th
                      key={h}
                      className="px-5 py-3 font-semibold text-xs tracking-wider uppercase"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {sales.map((s, idx) => (
                  <tr
                    key={s.id}
                    className={idx % 2 === 0 ? "bg-white" : "bg-[#f8f9ff]/40"}
                  >
                    <td className="px-5 py-3.5 text-xs font-medium text-gray-500">
                      {formatTime(s.createdAt)}
                    </td>
                    <td className="px-5 py-3.5 text-sm font-black text-[#7c83fd]">
                      ₱{fmt(s.amount)}
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="flex flex-wrap gap-1">
                        {(s.tags || []).map((t, i) => (
                          <span
                            key={i}
                            className="px-2 py-0.5 bg-indigo-50 text-[#7c83fd] text-[10px] font-bold uppercase rounded-md border border-indigo-100/50"
                          >
                            {t}
                          </span>
                        ))}
                        {(!s.tags || s.tags.length === 0) && (
                          <span className="text-xs text-gray-400 italic">
                            None
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-5 py-3.5 text-xs text-gray-500 max-w-[200px] truncate">
                      {s.description || "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot>
                <tr className="bg-indigo-50/50 border-t-2 border-indigo-100">
                  <td className="px-5 py-3 text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Total
                  </td>
                  <td className="px-5 py-3 text-sm font-black text-[#7c83fd]">
                    ₱
                    {fmt(
                      sales.reduce((s, r) => s + parseFloat(r.amount || 0), 0),
                    )}
                  </td>
                  <td colSpan="2" />
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </div>

      {/* Expenses Table */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-[0_4px_20px_rgb(0,0,0,0.03)] overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-100 flex items-center gap-2">
          <CreditCard className="w-4 h-4 text-amber-500" />
          <h3 className="font-bold text-sm text-[#1e1b4b]">Expense Records</h3>
          <span className="ml-auto text-xs font-bold text-gray-400">
            {expenses.length} entries
          </span>
        </div>
        {expenses.length === 0 ? (
          <EmptyTable message="No expense records for this day." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse whitespace-nowrap">
              <thead className="bg-gradient-to-r from-amber-400 to-amber-500 text-white">
                <tr>
                  {["Time", "Amount", "Tags", "Notes"].map((h) => (
                    <th
                      key={h}
                      className="px-5 py-3 font-semibold text-xs tracking-wider uppercase"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {expenses.map((e, idx) => (
                  <tr
                    key={e.id}
                    className={idx % 2 === 0 ? "bg-white" : "bg-amber-50/20"}
                  >
                    <td className="px-5 py-3.5 text-xs font-medium text-gray-500">
                      {formatTime(e.createdAt)}
                    </td>
                    <td className="px-5 py-3.5 text-sm font-black text-amber-600">
                      ₱{fmt(e.amount)}
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="flex flex-wrap gap-1">
                        {(e.tags || []).map((t, i) => (
                          <span
                            key={i}
                            className="px-2 py-0.5 bg-amber-50 text-amber-700 text-[10px] font-bold uppercase rounded-md border border-amber-100"
                          >
                            {t}
                          </span>
                        ))}
                        {(!e.tags || e.tags.length === 0) && (
                          <span className="text-xs text-gray-400 italic">
                            None
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-5 py-3.5 text-xs text-gray-500 max-w-[200px] truncate">
                      {e.description || "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot>
                <tr className="bg-amber-50/50 border-t-2 border-amber-100">
                  <td className="px-5 py-3 text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Total
                  </td>
                  <td className="px-5 py-3 text-sm font-black text-amber-600">
                    ₱
                    {fmt(
                      expenses.reduce(
                        (s, r) => s + parseFloat(r.amount || 0),
                        0,
                      ),
                    )}
                  </td>
                  <td colSpan="2" />
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

const EmptyTable = ({ message }) => (
  <div className="flex flex-col items-center justify-center py-10">
    <Inbox className="w-8 h-8 text-gray-300 mb-2" />
    <p className="text-sm text-gray-400 font-medium">{message}</p>
  </div>
);
