import { useState, useEffect, useCallback } from "react";
import { analyticsApi } from "../api/analyticsApi";
import api from "../../../shared/api/axios";
import * as cache from "../../../shared/cache/dataCache";
import {
  DAILY_TTL,
  TREND_TTL,
  dailyCacheKey,
  trendCacheKey,
} from "../constants/analyticsCacheKeys";
import {
  normalizeAnalytics,
  getEmptyAnalytics,
  calcDelta,
} from "../utils/analyticsUtils";
import { fetchPhHoliday } from "../services/holidayService";

export const useAnalytics = (date = null) => {
  const [data, setData] = useState(null);
  const [profitTrend, setProfitTrend] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLocked, setIsLocked] = useState(false);
  const [lockLoading, setLockLoading] = useState(false);
  const [lockError, setLockError] = useState(null);
  const [holidayName, setHolidayName] = useState(null);

  const fetchAnalytics = useCallback(
    async (forceRefresh = false) => {
      setLoading(true);
      setError(null);

      const dKey = dailyCacheKey(date);
      const tKey = trendCacheKey();

      try {
        let dailyPayload;
        if (!forceRefresh) {
          dailyPayload = cache.get(dKey);
        }
        if (!dailyPayload) {
          dailyPayload = await analyticsApi.getDailyStats(date);
          cache.set(dKey, dailyPayload, DAILY_TTL);
        }

        let trendPayload;
        if (!forceRefresh) {
          trendPayload = cache.get(tKey);
        }
        if (!trendPayload) {
          trendPayload = await analyticsApi.getProfitTrends("daily");
          cache.set(tKey, trendPayload, TREND_TTL);
        }

        const normalized = normalizeAnalytics(dailyPayload);
        setData(normalized);
        setIsLocked(Boolean(normalized.isClosed));
        setProfitTrend(trendPayload?.points ?? []);

        fetchPhHoliday(normalized.date).then(setHolidayName);
      } catch (err) {
        console.error("Failed to load analytics:", err);
        setError(
          err?.response?.data?.error?.message ?? "Failed to load analytics.",
        );
        setData(getEmptyAnalytics());
      } finally {
        setLoading(false);
      }
    },
    [date],
  );

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  const handleLockRecords = useCallback(async () => {
    setLockLoading(true);
    setLockError(null);
    try {
      const today = new Date().toISOString().split("T")[0];
      await api.post("/day/close", { date: today });
      setIsLocked(true);

      cache.invalidate(dailyCacheKey(date), trendCacheKey());
      cache.invalidate("archive:summaries");
      cache.invalidatePrefix("sales_page_");
      cache.invalidatePrefix("expense_page_");

      await fetchAnalytics(true);
    } catch (err) {
      const msg =
        err?.response?.data?.error?.message ??
        "Failed to close the day. Please try again.";
      setLockError(msg);
    } finally {
      setLockLoading(false);
    }
  }, [date, fetchAnalytics]);

  const normalized = data ?? getEmptyAnalytics();

  const isEmpty =
    normalized.totalSales === 0 &&
    normalized.totalExpenses === 0 &&
    normalized.saleRecordsCount === 0 &&
    normalized.expenseRecordsCount === 0 &&
    normalized.salesTrend.length === 0 &&
    normalized.tags.length === 0;

  const salesDelta = calcDelta(
    normalized.totalSales,
    normalized.yesterdaySales,
  );
  const expensesDelta = calcDelta(
    normalized.totalExpenses,
    normalized.yesterdayExpenses,
  );
  const profitDelta = calcDelta(
    normalized.netProfit,
    normalized.yesterdayProfit,
  );

  return {
    data: normalized,
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
    refetch: () => fetchAnalytics(true),
  };
};
