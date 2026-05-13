import * as cache from "../../../shared/cache/dataCache";
import { HOLIDAY_TTL, holidayCacheKey } from "../constants/analyticsCacheKeys";

export const fetchPhHoliday = async (dateStr) => {
  const key = holidayCacheKey(dateStr);
  const cached = cache.get(key);
  if (cached !== null) return cached;

  try {
    const year = dateStr.split("-")[0];
    const res = await fetch(
      `https://date.nager.at/api/v3/PublicHolidays/${year}/PH`,
    );
    if (!res.ok) return null;
    const holidays = await res.json();
    const match = holidays.find((h) => h.date === dateStr);
    const name = match ? match.localName || match.name : "";
    cache.set(key, name, HOLIDAY_TTL);
    return name || null;
  } catch {
    return null;
  }
};
