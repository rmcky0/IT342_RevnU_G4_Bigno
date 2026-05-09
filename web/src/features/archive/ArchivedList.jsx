import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { archiveApi } from "./api/archiveApi";
import * as cache from "../../shared/cache/dataCache";
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
  const [summaries, setSummaries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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
        cache.set(CACHE_KEY, data, 2 * 60 * 1000); // 2 min TTL for archive list
      } catch {
        setError("Failed to load archived records.");
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

  const formatShortDate = (dateStr) =>
    new Date(dateStr + "T00:00:00").toLocaleDateString("en-PH", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });

  const fmt = (n) =>
    parseFloat(n || 0).toLocaleString("en-PH", { minimumFractionDigits: 2 });

  return (
    <div className="space-y-6 text-[#1e1b4b]">
      {/* Header */}
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate(-1)}
            className="p-2 bg-white rounded-lg border border-gray-200 shadow-sm text-gray-500 hover:text-[#7c83fd] hover:border-indigo-100 transition-all"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div className="p-2 bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
            <Lock className="w-5 h-5 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-bold tracking-tight">
              Archived Records
            </h2>
            <p className="text-xs text-gray-400 font-medium mt-0.5">
              End-of-day closed sessions
            </p>
          </div>
          {summaries.length > 0 && (
            <span className="px-2.5 py-0.5 bg-white text-[#7c83fd] text-xs font-bold rounded-md border border-gray-100 shadow-sm">
              {summaries.length} sessions
            </span>
          )}
        </div>
      </div>

      {/* Error */}
      {error && (
        <div className="px-4 py-3 bg-red-50 text-red-600 text-sm font-medium rounded-xl border border-red-100">
          {error}
        </div>
      )}

      {/* Loading skeletons */}
      {loading && (
        <div className="space-y-3">
          {Array.from({ length: 5 }).map((_, i) => (
            <div
              key={i}
              className="h-24 bg-white rounded-xl border border-gray-100 shadow-sm animate-pulse"
            />
          ))}
        </div>
      )}

      {/* Empty state */}
      {!loading && summaries.length === 0 && !error && (
        <div className="flex flex-col items-center justify-center py-20">
          <div className="w-20 h-20 bg-gradient-to-br from-indigo-50 to-gray-50 rounded-3xl flex items-center justify-center mb-5 shadow-inner border border-white">
            <Inbox className="w-10 h-10 text-[#7c83fd]" />
          </div>
          <h3 className="text-xl font-bold text-[#1e1b4b] mb-2">
            No archived records yet
          </h3>
          <p className="text-sm text-gray-500 text-center max-w-xs">
            Use the "Lock EOD" button on the Sales or Expenses page to close and
            archive a day's records.
          </p>
        </div>
      )}

      {/* Records list */}
      {!loading && summaries.length > 0 && (
        <div className="space-y-3">
          {summaries.map((s) => {
            const profit = parseFloat(s.netProfit || 0);
            const isProfit = profit >= 0;
            return (
              <button
                key={s.date}
                onClick={() => navigate(`/archived/${s.date}`)}
                className="w-full bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.03)] hover:shadow-[0_4px_20px_rgb(124,131,253,0.12)] hover:border-indigo-100 hover:bg-[#f8f9ff] transition-all duration-200 p-5 flex items-center gap-5 text-left group"
              >
                {/* Date badge */}
                <div className="flex flex-col items-center justify-center w-16 h-16 bg-gradient-to-br from-indigo-50 to-[#7c83fd]/10 rounded-xl border border-indigo-100/50 shrink-0">
                  <Calendar className="w-5 h-5 text-[#7c83fd] mb-0.5" />
                  <span className="text-[10px] font-black text-[#7c83fd] uppercase tracking-wide">
                    {new Date(s.date + "T00:00:00").toLocaleDateString(
                      "en-PH",
                      { month: "short" },
                    )}
                  </span>
                  <span className="text-lg font-black text-[#1e1b4b] leading-none">
                    {new Date(s.date + "T00:00:00").getDate()}
                  </span>
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-2">
                    <p className="font-bold text-sm text-[#1e1b4b] truncate">
                      {formatDate(s.date)}
                    </p>
                    <span className="flex items-center gap-1 px-2 py-0.5 bg-indigo-50 text-[#7c83fd] text-[10px] font-bold rounded-md border border-indigo-100/50 shrink-0">
                      <Lock className="w-2.5 h-2.5" /> Locked
                    </span>
                  </div>
                  <div className="flex flex-wrap gap-4 text-xs">
                    <span className="text-gray-500 font-medium">
                      Sales:{" "}
                      <span className="text-[#7c83fd] font-bold">
                        ₱{fmt(s.totalSales)}
                      </span>
                    </span>
                    <span className="text-gray-500 font-medium">
                      Expenses:{" "}
                      <span className="text-gray-700 font-bold">
                        ₱{fmt(s.totalExpenses)}
                      </span>
                    </span>
                    {parseFloat(s.totalSalaries || 0) > 0 && (
                      <span className="text-gray-500 font-medium">
                        Salaries:{" "}
                        <span className="text-violet-600 font-bold">
                          ₱{fmt(s.totalSalaries)}
                        </span>
                      </span>
                    )}
                  </div>
                </div>

                {/* Net profit */}
                <div
                  className={`flex flex-col items-end shrink-0 ${isProfit ? "text-emerald-600" : "text-red-500"}`}
                >
                  <div className="flex items-center gap-1 mb-0.5">
                    {isProfit ? (
                      <TrendingUp className="w-4 h-4" />
                    ) : (
                      <TrendingDown className="w-4 h-4" />
                    )}
                    <span className="text-lg font-black">
                      ₱{fmt(Math.abs(s.netProfit))}
                    </span>
                  </div>
                  <span
                    className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-md ${isProfit ? "bg-emerald-50 text-emerald-600 border border-emerald-100" : "bg-red-50 text-red-500 border border-red-100"}`}
                  >
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
