import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { archiveApi } from "./api/archiveApi";
import * as cache from "../../shared/cache/dataCache";
import { useToast } from "../../shared/components/Toast";
import {
  Lock,
  ChevronRight,
  TrendingUp,
  TrendingDown,
  ArrowLeft,
  Inbox,
  Calendar,
} from "lucide-react";

const CACHE_KEY = "archive:summaries";

export const ArchivedList = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [summaries, setSummaries] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      const cached = cache.get(CACHE_KEY);
      if (cached) {
        setSummaries(cached);
        setLoading(false);
        return;
      }
      try {
        const res = await archiveApi.getAllSummaries();
        const data = Array.isArray(res?.data) ? res.data : [];
        setSummaries(data);
        cache.set(CACHE_KEY, data, 2 * 60 * 1000);
      } catch {
        showToast("error", "Failed to load archived records.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const formatDate = (dateStr) =>
    new Date(dateStr + "T00:00:00").toLocaleDateString("en-PH", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });

  const fmt = (n) =>
    parseFloat(n || 0).toLocaleString("en-PH", { minimumFractionDigits: 2 });

  return (
    <div className="flex flex-col gap-3 text-[#1e1b4b]">
      {/* ── Header ── */}
      <div className="shrink-0 flex items-center gap-2.5">
        <button
          onClick={() => navigate(-1)}
          className="p-2 bg-white rounded-xl border border-gray-200 text-gray-400 hover:text-[#7c83fd] hover:border-indigo-100 transition-colors shrink-0"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>
        <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
          <Lock className="w-4.5 h-4.5 text-white" />
        </div>
        <h2 className="text-lg font-bold tracking-tight truncate">Archived Records</h2>
        {summaries.length > 0 && (
          <span className="shrink-0 text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] border border-[#d6d9ff] rounded-full px-2.5 py-0.5 uppercase tracking-wider">
            {summaries.length} sessions
          </span>
        )}
      </div>

      {/* ── Loading skeletons ── */}
      {loading && (
        <div className="flex flex-col gap-3">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="h-24 bg-white rounded-xl border border-gray-100 animate-pulse" />
          ))}
        </div>
      )}

      {/* ── Empty state ── */}
      {!loading && summaries.length === 0 && (
        <div className="flex flex-col items-center justify-center py-20 animate-in fade-in zoom-in-95 duration-300">
          <div className="w-14 h-14 bg-indigo-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner">
            <Inbox className="w-7 h-7 text-[#7c83fd]" />
          </div>
          <p className="text-base font-bold text-[#1e1b4b] mb-1">No archived records yet</p>
          <p className="text-sm text-gray-400 text-center max-w-xs">
            Use the "Lock EOD" button on the Sales or Expenses page to close and archive a day's records.
          </p>
        </div>
      )}

      {/* ── Session cards ── */}
      {!loading && summaries.length > 0 && (
        <div className="flex flex-col gap-3">
          {summaries.map((s) => {
            const profit = parseFloat(s.netProfit || 0);
            const isProfit = profit >= 0;
            return (
              <button
                key={s.date}
                onClick={() => navigate(`/archived/${s.date}`)}
                className="w-full bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.03)] hover:shadow-[0_4px_20px_rgb(124,131,253,0.1)] hover:border-indigo-100 hover:bg-[#f8f9ff] transition-all duration-200 p-5 flex items-center gap-5 text-left group"
              >
                {/* Date badge */}
                <div className="flex flex-col items-center justify-center w-14 h-14 bg-[#7c83fd]/10 rounded-xl border border-[#7c83fd]/20 shrink-0">
                  <Calendar className="w-4 h-4 text-[#7c83fd] mb-0.5" />
                  <span className="text-[9px] font-black text-[#7c83fd] uppercase tracking-wide">
                    {new Date(s.date + "T00:00:00").toLocaleDateString("en-PH", { month: "short" })}
                  </span>
                  <span className="text-base font-black text-[#1e1b4b] leading-none">
                    {new Date(s.date + "T00:00:00").getDate()}
                  </span>
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1.5">
                    <p className="font-bold text-sm text-[#1e1b4b] truncate">
                      {formatDate(s.date)}
                    </p>
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-[#f0f1ff] text-[#7c83fd] text-[10px] font-bold rounded-full border border-[#d6d9ff] shrink-0">
                      <Lock className="w-2.5 h-2.5" /> Locked
                    </span>
                  </div>
                  <div className="flex flex-wrap gap-4 text-xs">
                    <span className="text-gray-400 font-medium">
                      Sales: <span className="text-[#7c83fd] font-bold">₱{fmt(s.totalSales)}</span>
                    </span>
                    <span className="text-gray-400 font-medium">
                      Expenses: <span className="text-gray-600 font-bold">₱{fmt(s.totalExpenses)}</span>
                    </span>
                    {parseFloat(s.totalSalaries || 0) > 0 && (
                      <span className="text-gray-400 font-medium">
                        Salaries: <span className="text-violet-600 font-bold">₱{fmt(s.totalSalaries)}</span>
                      </span>
                    )}
                  </div>
                </div>

                {/* Net profit */}
                <div className={`flex flex-col items-end shrink-0 ${isProfit ? "text-emerald-600" : "text-red-500"}`}>
                  <div className="flex items-center gap-1 mb-0.5">
                    {isProfit ? <TrendingUp className="w-3.5 h-3.5" /> : <TrendingDown className="w-3.5 h-3.5" />}
                    <span className="text-base font-black tabular-nums">₱{fmt(Math.abs(s.netProfit))}</span>
                  </div>
                  <span className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-full border ${isProfit ? "bg-emerald-50 text-emerald-600 border-emerald-200" : "bg-red-50 text-red-500 border-red-200"}`}>
                    {isProfit ? "Profit" : "Loss"}
                  </span>
                </div>

                <ChevronRight className="w-4 h-4 text-gray-300 group-hover:text-[#7c83fd] transition-colors shrink-0" />
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
};
