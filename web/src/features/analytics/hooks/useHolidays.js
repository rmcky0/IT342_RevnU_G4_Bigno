import { useState, useEffect, useMemo } from "react";
import { externalApi } from "../../external/api/externalApi";

export const useHolidays = () => {
  const today = new Date();
  const [viewYear, setViewYear] = useState(today.getFullYear());
  const [viewMonth, setViewMonth] = useState(today.getMonth());
  const [holidays, setHolidays] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchHolidays = async () => {
      if (holidays[viewYear]) return;
      setLoading(true);
      try {
        const res = await externalApi.getHolidays(viewYear);
        const map = {};
        res.data.forEach((h) => {
          map[h.date] = h.localName || h.name;
        });
        setHolidays((prev) => ({ ...prev, [viewYear]: map }));
      } catch (err) {
        console.error(err);
        setHolidays((prev) => ({ ...prev, [viewYear]: {} }));
      } finally {
        setLoading(false);
      }
    };
    fetchHolidays();
  }, [viewYear, holidays]);

  const prevMonth = () => {
    if (viewMonth === 0) {
      setViewMonth(11);
      setViewYear((y) => y - 1);
    } else setViewMonth((m) => m - 1);
  };

  const nextMonth = () => {
    if (viewMonth === 11) {
      setViewMonth(0);
      setViewYear((y) => y + 1);
    } else setViewMonth((m) => m + 1);
  };

  const { cells, monthHolidays } = useMemo(() => {
    const firstDay = new Date(viewYear, viewMonth, 1).getDay();
    const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate();

    const gridCells = Array(firstDay)
      .fill(null)
      .concat(Array.from({ length: daysInMonth }, (_, i) => i + 1));

    const yearData = holidays[viewYear] ?? {};
    const filteredHolidays = Object.entries(yearData)
      .filter(([date]) => {
        const [y, m] = date.split("-").map(Number);
        return y === viewYear && m - 1 === viewMonth;
      })
      .map(([date, name]) => ({ date, name }));

    return { cells: gridCells, monthHolidays: filteredHolidays };
  }, [viewYear, viewMonth, holidays]);

  return {
    viewYear,
    viewMonth,
    cells,
    monthHolidays,
    yearHolidays: holidays[viewYear] || {},
    loading,
    prevMonth,
    nextMonth,
    today,
  };
};
