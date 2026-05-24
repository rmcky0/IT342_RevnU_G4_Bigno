import React, { useEffect, useState, useCallback } from "react";
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
  Download,
  Eye,
  X,
  AlertTriangle,
} from "lucide-react";
import { useAuth } from "../auth/context/AuthContext";
import { useRestaurantProfile } from "../restaurant/hooks/useRestaurantProfile";
import {
  downloadExcel,
  downloadPdf,
  previewPdfUrl,
} from "./utils/downloadUtils";

/* ── Shared sub-components ───────────────────────────────── */

const KPICard = ({ icon: Icon, iconBg, iconColor, label, value, accent }) => (
  <div
    className={`bg-white rounded-xl px-5 py-4 border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex items-center gap-4 ${accent ? `border-l-2 ${accent}` : ""}`}
  >
    <div
      className={`w-10 h-10 rounded-xl ${iconBg} flex items-center justify-center shrink-0`}
    >
      <Icon className={`w-5 h-5 ${iconColor}`} />
    </div>
    <div className="min-w-0">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate">
        {label}
      </p>
      <p className="text-xl font-bold text-[#1e1b4b] leading-tight mt-0.5 tabular-nums">
        {value}
      </p>
    </div>
  </div>
);

const TableSection = ({ icon: Icon, iconColor, title, count, children }) => (
  <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.03)] overflow-hidden">
    <div className="px-5 py-3.5 border-b border-gray-100 flex items-center gap-2">
      <Icon className={`w-4 h-4 ${iconColor}`} />
      <h3 className="font-bold text-sm text-[#1e1b4b]">{title}</h3>
      <span className="ml-auto text-[10px] font-bold text-gray-400 uppercase tracking-wider">
        {count} {count === 1 ? "entry" : "entries"}
      </span>
    </div>
    {children}
  </div>
);

const EmptyTable = ({ message }) => (
  <div className="flex flex-col items-center justify-center py-12 animate-in fade-in zoom-in-95 duration-300">
    <div className="w-12 h-12 bg-gray-50 rounded-2xl flex items-center justify-center mb-3 shadow-inner">
      <Inbox className="w-6 h-6 text-gray-300" />
    </div>
    <p className="text-sm text-gray-400 font-medium">{message}</p>
  </div>
);

const TH = ({ children, align = "left" }) => (
  <th
    className={`px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 ${align === "right" ? "text-right" : "text-left"}`}
  >
    {children}
  </th>
);

/* ── Main component ──────────────────────────────────────── */

export const ArchivedDetail = () => {
  const { date } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { profile: restaurantProfile } = useRestaurantProfile(user?.email);
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [previewUrl, setPreviewUrl] = useState(null);

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

  const restaurantName = restaurantProfile?.restaurantName || "RevnU";
  const ownerName = user?.fullname || "";

  const openPreview = useCallback(() => {
    if (!detail) return;
    const url = previewPdfUrl(detail, date, restaurantName, ownerName);
    setPreviewUrl(url);
  }, [detail, date, restaurantName, ownerName]);

  const closePreview = useCallback(() => {
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(null);
  }, [previewUrl]);

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

  /* ── Loading skeleton ── */
  if (loading) {
    return (
      <div className="flex flex-col gap-4 animate-pulse">
        <div className="h-9 bg-gray-100 rounded-xl w-72" />
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-20 bg-gray-100 rounded-xl" />
          ))}
        </div>
        <div className="h-56 bg-gray-100 rounded-xl" />
        <div className="h-56 bg-gray-100 rounded-xl" />
      </div>
    );
  }

  /* ── Error state ── */
  if (error) {
    return (
      <div className="flex flex-col gap-3 text-[#1e1b4b]">
        <button
          onClick={() => navigate("/archived")}
          className="inline-flex items-center gap-2 text-sm font-semibold text-gray-400 hover:text-[#7c83fd] transition-colors w-fit"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Archived
        </button>
        <div className="flex items-center gap-2 px-4 py-3 bg-red-50 border border-red-100 rounded-xl text-red-600 text-sm font-semibold">
          <AlertTriangle className="w-4 h-4 shrink-0" />
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
    <div className="flex flex-col gap-4 text-[#1e1b4b]">
      {/* ── Header ── */}
      <div className="flex items-center gap-2.5 flex-wrap">
        <button
          onClick={() => navigate(-1)}
          className="p-2 bg-white rounded-xl border border-gray-200 text-gray-400 hover:text-[#7c83fd] hover:border-indigo-100 transition-colors shrink-0"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>

        <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
          <Lock className="w-4.5 h-4.5 text-white" />
        </div>

        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <h2 className="text-lg font-bold tracking-tight truncate">
              {formatDate(date)}
            </h2>
            <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-[#f0f1ff] text-[#7c83fd] text-[10px] font-bold rounded-full border border-[#d6d9ff] shrink-0">
              <Lock className="w-2.5 h-2.5" /> EOD Locked
            </span>
          </div>
          <p className="text-[11px] text-gray-400 font-medium mt-0.5">
            {sales.length} sales · {expenses.length} expenses
            {summary?.reportSent && " · Email report sent ✓"}
          </p>
        </div>

        {/* Download / Preview buttons */}
        <div className="flex items-center gap-2 shrink-0">
          <button
            onClick={() =>
              downloadExcel(detail, date, restaurantName, ownerName)
            }
            className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-emerald-500 text-white rounded-xl font-bold text-[13px] hover:bg-emerald-600 transition-colors"
            title="Download Excel"
          >
            <Download className="w-3.5 h-3.5" /> Excel
          </button>
          <button
            onClick={openPreview}
            className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-[#7c83fd] text-white rounded-xl font-bold text-[13px] hover:bg-[#6b72f5] transition-colors"
            title="Preview PDF"
          >
            <Eye className="w-3.5 h-3.5" /> Preview PDF
          </button>
          <button
            onClick={() => downloadPdf(detail, date, restaurantName, ownerName)}
            className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-red-500 text-white rounded-xl font-bold text-[13px] hover:bg-red-600 transition-colors"
            title="Download PDF"
          >
            <Download className="w-3.5 h-3.5" /> PDF
          </button>
        </div>
      </div>

      {/* ── KPI Cards ── */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        <KPICard
          icon={ShoppingCart}
          iconBg="bg-[#7c83fd]/10"
          iconColor="text-[#7c83fd]"
          label="Total Sales"
          value={`₱${fmt(summary?.totalSales)}`}
          accent="border-l-[#7c83fd]"
        />
        <KPICard
          icon={CreditCard}
          iconBg="bg-amber-50"
          iconColor="text-amber-500"
          label="Total Expenses"
          value={`₱${fmt(summary?.totalExpenses)}`}
          accent="border-l-amber-400"
        />
        <KPICard
          icon={Wallet}
          iconBg="bg-violet-50"
          iconColor="text-violet-500"
          label="Salaries Paid"
          value={`₱${fmt(summary?.totalSalaries)}`}
          accent="border-l-violet-400"
        />
        <KPICard
          icon={isProfit ? TrendingUp : TrendingDown}
          iconBg={isProfit ? "bg-emerald-50" : "bg-red-50"}
          iconColor={isProfit ? "text-emerald-500" : "text-red-500"}
          label="Net Profit"
          value={`₱${fmt(summary?.netProfit)}`}
          accent={isProfit ? "border-l-emerald-400" : "border-l-red-400"}
        />
      </div>

      {/* ── Net Profit Breakdown ── */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.03)] p-5">
        <div className="flex items-center gap-2 mb-4">
          {isProfit ? (
            <TrendingUp className="w-4 h-4 text-emerald-500" />
          ) : (
            <TrendingDown className="w-4 h-4 text-red-500" />
          )}
          <h3 className="font-bold text-sm text-[#1e1b4b]">
            Net Profit Breakdown
          </h3>
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          {/* Sales */}
          <div className="flex flex-col items-center px-4 py-3 bg-[#7c83fd]/5 rounded-xl border border-[#7c83fd]/20 min-w-28">
            <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">
              Sales
            </span>
            <span className="text-base font-black text-[#7c83fd] tabular-nums">
              ₱{fmt(summary?.totalSales)}
            </span>
          </div>

          <span className="text-lg font-black text-gray-300 select-none">
            −
          </span>

          {/* Expenses */}
          <div className="flex flex-col items-center px-4 py-3 bg-amber-50 rounded-xl border border-amber-100 min-w-28">
            <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">
              Expenses
            </span>
            <span className="text-base font-black text-amber-600 tabular-nums">
              ₱{fmt(summary?.totalExpenses)}
            </span>
          </div>

          <span className="text-lg font-black text-gray-300 select-none">
            −
          </span>

          {/* Salaries */}
          <div className="flex flex-col items-center px-4 py-3 bg-violet-50 rounded-xl border border-violet-100 min-w-28">
            <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">
              Salaries
            </span>
            <span className="text-base font-black text-violet-600 tabular-nums">
              ₱{fmt(summary?.totalSalaries)}
            </span>
          </div>

          <span className="text-lg font-black text-gray-300 select-none">
            =
          </span>

          {/* Result */}
          <div
            className={`flex flex-col items-center px-4 py-3 rounded-xl border min-w-28 ${isProfit ? "bg-emerald-50 border-emerald-200" : "bg-red-50 border-red-200"}`}
          >
            <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">
              Net Profit
            </span>
            <span
              className={`text-base font-black tabular-nums ${isProfit ? "text-emerald-600" : "text-red-500"}`}
            >
              {!isProfit && "−"}₱{fmt(Math.abs(profit))}
            </span>
          </div>
        </div>

        {/* Formula label */}
        <p className="mt-3 text-[11px] text-gray-400 font-medium">
          Net Profit = Total Sales − Total Expenses − Salaries Paid
        </p>
      </div>

      {/* ── Tag Breakdown ── */}
      {topTags.length > 0 && (
        <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.03)] p-5">
          <div className="flex items-center gap-2 mb-4">
            <TagIcon className="w-4 h-4 text-[#7c83fd]" />
            <h3 className="font-bold text-sm text-[#1e1b4b]">Sales by Tag</h3>
          </div>
          <div className="flex flex-wrap gap-2">
            {topTags.map(([tag, amount], i) => (
              <div
                key={tag}
                className="flex items-center gap-2 px-3 py-2 bg-gray-50 rounded-xl border border-gray-100"
              >
                <div
                  className="w-2 h-2 rounded-full shrink-0"
                  style={{ backgroundColor: COLORS[i % COLORS.length] }}
                />
                <span className="text-[11px] font-bold text-gray-600 uppercase tracking-wide">
                  {tag}
                </span>
                <span
                  className="text-[11px] font-black tabular-nums"
                  style={{ color: COLORS[i % COLORS.length] }}
                >
                  ₱{fmt(amount)}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── Sales Table ── */}
      <TableSection
        icon={ShoppingCart}
        iconColor="text-[#7c83fd]"
        title="Sales Records"
        count={sales.length}
      >
        {sales.length === 0 ? (
          <EmptyTable message="No sale records for this day." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse whitespace-nowrap">
              <thead className="bg-gray-50 sticky top-0 z-10">
                <tr>
                  <TH>Time</TH>
                  <TH align="right">Amount</TH>
                  <TH>Category / Tags</TH>
                  <TH>Notes</TH>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {sales.map((s) => (
                  <tr
                    key={s.id}
                    className="hover:bg-[#f8f9ff] transition-colors duration-100 group"
                  >
                    <td className="px-5 py-3 text-xs font-medium text-gray-500">
                      {formatTime(s.createdAt)}
                    </td>
                    <td className="px-5 py-3 text-sm font-bold text-[#7c83fd] text-right tabular-nums">
                      ₱{fmt(s.amount)}
                    </td>
                    <td className="px-5 py-3">
                      <div className="flex flex-wrap gap-1">
                        {(s.tags || []).map((t, i) => (
                          <span
                            key={i}
                            className="px-2 py-0.5 bg-[#eef0ff] text-[#6b72f5] text-[10px] font-bold uppercase rounded-full border border-[#d6d9ff]"
                          >
                            {t}
                          </span>
                        ))}
                        {(!s.tags || s.tags.length === 0) && (
                          <span className="text-xs text-gray-300">—</span>
                        )}
                      </div>
                    </td>
                    <td className="px-5 py-3 text-xs text-gray-400 max-w-48 truncate group-hover:text-gray-600 transition-colors">
                      {s.notes || "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot>
                <tr className="border-t-2 border-[#7c83fd]/20 bg-[#f8f9ff]">
                  <td className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                    Total
                  </td>
                  <td className="px-5 py-3 text-sm font-bold text-[#7c83fd] text-right tabular-nums">
                    ₱
                    {fmt(
                      sales.reduce(
                        (acc, r) => acc + parseFloat(r.amount || 0),
                        0,
                      ),
                    )}
                  </td>
                  <td colSpan={2} />
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </TableSection>

      {/* ── Expenses Table ── */}
      <TableSection
        icon={CreditCard}
        iconColor="text-amber-500"
        title="Expense Records"
        count={expenses.length}
      >
        {expenses.length === 0 ? (
          <EmptyTable message="No expense records for this day." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse whitespace-nowrap">
              <thead className="bg-gray-50 sticky top-0 z-10">
                <tr>
                  <TH>Time</TH>
                  <TH align="right">Amount</TH>
                  <TH>Category / Tags</TH>
                  <TH>Notes</TH>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {expenses.map((e) => (
                  <tr
                    key={e.id}
                    className="hover:bg-[#f8f9ff] transition-colors duration-100 group"
                  >
                    <td className="px-5 py-3 text-xs font-medium text-gray-500">
                      {formatTime(e.createdAt)}
                    </td>
                    <td className="px-5 py-3 text-sm font-bold text-amber-600 text-right tabular-nums">
                      ₱{fmt(e.amount)}
                    </td>
                    <td className="px-5 py-3">
                      <div className="flex flex-wrap gap-1">
                        {(e.tags || []).map((t, i) => (
                          <span
                            key={i}
                            className="px-2 py-0.5 bg-amber-50 text-amber-700 text-[10px] font-bold uppercase rounded-full border border-amber-200"
                          >
                            {t}
                          </span>
                        ))}
                        {(!e.tags || e.tags.length === 0) && (
                          <span className="text-xs text-gray-300">—</span>
                        )}
                      </div>
                    </td>
                    <td className="px-5 py-3 text-xs text-gray-400 max-w-48 truncate group-hover:text-gray-600 transition-colors">
                      {e.notes || "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot>
                <tr className="border-t-2 border-amber-100 bg-amber-50/40">
                  <td className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                    Total
                  </td>
                  <td className="px-5 py-3 text-sm font-bold text-amber-600 text-right tabular-nums">
                    ₱
                    {fmt(
                      expenses.reduce(
                        (acc, r) => acc + parseFloat(r.amount || 0),
                        0,
                      ),
                    )}
                  </td>
                  <td colSpan={2} />
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </TableSection>

      {/* ── PDF Preview Modal ── */}
      {previewUrl && (
        <div
          className="fixed inset-0 z-50 bg-[#1e1b4b]/40 backdrop-blur-[3px] flex items-center justify-center p-4 animate-in fade-in duration-200"
          onClick={closePreview}
        >
          <div
            className="bg-white rounded-2xl shadow-2xl flex flex-col w-full max-w-4xl h-[90vh] overflow-hidden border border-gray-100"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between px-6 py-5 border-b border-gray-100 shrink-0">
              <div>
                <p className="font-bold text-[#1e1b4b] text-base">
                  PDF Preview
                </p>
                <p className="text-[11px] text-gray-400 mt-0.5">
                  {restaurantName} · {date}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <button
                  onClick={() =>
                    downloadPdf(detail, date, restaurantName, ownerName)
                  }
                  className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-red-500 text-white rounded-xl font-bold text-[13px] hover:bg-red-600 transition-colors"
                >
                  <Download className="w-3.5 h-3.5" /> Download PDF
                </button>
                <button
                  onClick={closePreview}
                  className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-xl transition-colors"
                >
                  <X className="w-4.5 h-4.5" />
                </button>
              </div>
            </div>
            <iframe
              src={previewUrl}
              className="flex-1 w-full border-0"
              title="PDF Preview"
            />
          </div>
        </div>
      )}
    </div>
  );
};
