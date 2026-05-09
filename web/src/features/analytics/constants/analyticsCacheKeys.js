export const DAILY_TTL = 2 * 60 * 1000;
export const TREND_TTL = 5 * 60 * 1000;
export const HOLIDAY_TTL = 60 * 60 * 1000;
export const dailyCacheKey = (date) => `analytics:daily:${date ?? "today"}`;
export const trendCacheKey = () => `analytics:trends`;
export const holidayCacheKey = (date) => `analytics:holiday:${date}`;
