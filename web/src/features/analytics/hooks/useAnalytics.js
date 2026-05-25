import { useState, useEffect, useCallback } from "react";
import { analyticsApi } from "../api/analyticsApi";
import { salesAPI } from "../../sales/api/salesApi";
import { expensesAPI } from "../../expenses/api/expensesApi";
import { categoriesAPI } from "../../categories/api/categoriesApi";
import api from "../../../shared/api/axios";
import * as cache from "../../../shared/cache/dataCache";
import { useToast } from "../../../shared/components/Toast";
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

const OPEN_TOTALS_TTL = 2 * 60 * 1000;
const OPEN_TOTALS_KEY = "analytics:open-totals";
const CATEGORY_COLORS = [
  "#7c83fd",
  "#f87171",
  "#34d399",
  "#fbbf24",
  "#60a5fa",
  "#a78bfa",
  "#fb923c",
  "#e879f9",
];

export const useAnalytics = (date = null) => {
  const { showToast } = useToast();
  const [data, setData] = useState(null);
  const [profitTrend, setProfitTrend] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLocked, setIsLocked] = useState(false);
  const [lockLoading, setLockLoading] = useState(false);
  const [holidayName, setHolidayName] = useState(null);
  const [openSalesTotal, setOpenSalesTotal] = useState(0);
  const [openExpensesTotal, setOpenExpensesTotal] = useState(0);
  const [openSaleRecordsCount, setOpenSaleRecordsCount] = useState(0);
  const [openExpenseRecordsCount, setOpenExpenseRecordsCount] = useState(0);
  const [openCategories, setOpenCategories] = useState([]);

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

        let openTotals = !forceRefresh ? cache.get(OPEN_TOTALS_KEY) : null;
        if (!openTotals) {
          const [salesRes, expensesRes, saleCategories] = await Promise.all([
            salesAPI.getAllSales(0, 9999),
            expensesAPI.getAllExpenses(0, 9999),
            categoriesAPI.getCategories("SALE"),
          ]);

          const openSales = (salesRes?.data?.content ?? []).filter(
            (r) => r.status !== "CLOSED",
          );
          const openExpenses = (expensesRes?.data?.content ?? []).filter(
            (r) => r.status !== "CLOSED",
          );

          const colorMap = {};
          if (Array.isArray(saleCategories)) {
            saleCategories.forEach((c) => {
              colorMap[c.id] = c.color;
            });
          }

          const categoryMap = {};
          openSales.forEach((r) => {
            const name = r.categoryName || "Uncategorized";
            if (!categoryMap[name])
              categoryMap[name] = { name, categoryId: r.categoryId, value: 0 };
            categoryMap[name].value += parseFloat(r.amount) || 0;
          });
          const categories = Object.values(categoryMap).map((t, i) => ({
            name: t.name,
            value: t.value,
            color:
              colorMap[t.categoryId] ??
              CATEGORY_COLORS[i % CATEGORY_COLORS.length],
          }));

          openTotals = {
            sales: openSales.reduce(
              (sum, r) => sum + (parseFloat(r.amount) || 0),
              0,
            ),
            expenses: openExpenses.reduce(
              (sum, r) => sum + (parseFloat(r.amount) || 0),
              0,
            ),
            saleRecordsCount: openSales.length,
            expenseRecordsCount: openExpenses.length,
            categories,
          };
          cache.set(OPEN_TOTALS_KEY, openTotals, OPEN_TOTALS_TTL);
        }
        setOpenSalesTotal(openTotals.sales);
        setOpenExpensesTotal(openTotals.expenses);
        setOpenSaleRecordsCount(openTotals.saleRecordsCount);
        setOpenExpenseRecordsCount(openTotals.expenseRecordsCount);
        setOpenCategories(openTotals.categories);
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
    try {
      const today = new Date().toISOString().split("T")[0];
      await api.post("/day/close", { date: today });
      setIsLocked(true);

      cache.invalidate(dailyCacheKey(date), trendCacheKey());
      cache.invalidate("archive:summaries");
      cache.invalidate(OPEN_TOTALS_KEY);
      cache.invalidatePrefix("sales_page_");
      cache.invalidatePrefix("expense_page_");

      await fetchAnalytics(true);
    } catch (err) {
      const msg =
        err?.response?.data?.error?.message ??
        "Failed to close the day. Please try again.";
      showToast("error", msg);
    } finally {
      setLockLoading(false);
    }
  }, [date, fetchAnalytics, showToast]);

  const normalized = data ?? getEmptyAnalytics();

  const isEmpty =
    openSalesTotal === 0 &&
    openExpensesTotal === 0 &&
    normalized.saleRecordsCount === 0 &&
    normalized.expenseRecordsCount === 0 &&
    normalized.salesTrend.length === 0 &&
    normalized.categories.length === 0;

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

  const openNetProfit =
    openSalesTotal - openExpensesTotal - normalized.totalSalaries;

  return {
    data: normalized,
    profitTrend,
    loading,
    error,
    isEmpty,
    isLocked,
    lockLoading,
    holidayName,
    salesDelta,
    expensesDelta,
    profitDelta,
    openSalesTotal,
    openExpensesTotal,
    openNetProfit,
    openCategories,
    openSaleRecordsCount,
    openExpenseRecordsCount,
    handleLockRecords,
    refetch: () => fetchAnalytics(true),
  };
};
