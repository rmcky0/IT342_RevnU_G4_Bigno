export const getEmptyAnalytics = () => ({
  totalSales: 0,
  totalExpenses: 0,
  totalSalaries: 0,
  netProfit: 0,
  yesterdaySales: 0,
  yesterdayExpenses: 0,
  yesterdayProfit: 0,
  saleRecordsCount: 0,
  expenseRecordsCount: 0,
  salesTrend: [],
  tags: [],
  yesterdayLabel: "",
  isClosed: false,
  date: new Date().toISOString().split("T")[0],
});

export const normalizeAnalytics = (payload) => {
  if (!payload) return getEmptyAnalytics();
  const rawTags = Array.isArray(payload.tags)
    ? payload.tags
    : payload.categories;
  const tags = Array.isArray(rawTags)
    ? rawTags.map((cat) => ({
        name: cat.name ?? "Uncategorized",
        value: Number(cat.value ?? 0),
        color: cat.color ?? "#5a7cff",
      }))
    : [];
  return {
    totalSales: Number(payload.totalSales ?? 0),
    totalExpenses: Number(payload.totalExpenses ?? 0),
    totalSalaries: Number(payload.totalSalaries ?? 0),
    netProfit: Number(payload.netProfit ?? 0),
    yesterdaySales: Number(payload.yesterdaySales ?? 0),
    yesterdayExpenses: Number(payload.yesterdayExpenses ?? 0),
    yesterdayProfit: Number(payload.yesterdayProfit ?? 0),
    saleRecordsCount: Number(payload.saleRecordsCount ?? 0),
    expenseRecordsCount: Number(payload.expenseRecordsCount ?? 0),
    salesTrend: Array.isArray(payload.salesTrend) ? payload.salesTrend : [],
    tags,
    yesterdayLabel: payload.yesterdayLabel ?? "",
    isClosed: Boolean(payload.isClosed),
    date: payload.date ?? new Date().toISOString().split("T")[0],
  };
};

export const calcDelta = (today, yesterday) => {
  if (!yesterday || yesterday === 0) return null;
  return Math.round(((today - yesterday) / Math.abs(yesterday)) * 1000) / 10;
};
