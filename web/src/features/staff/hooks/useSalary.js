import { useEffect, useState } from "react";
import { salaryApi } from "../api/salaryApi";
import * as cache from "../../../shared/cache/dataCache";

const SALARIES_CACHE_KEY = "salaries";

export const useSalary = () => {
  const [salaries, setSalaries] = useState([]);
  const [salaryTotalPages, setSalaryTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showSalaryForm, setShowSalaryForm] = useState(false);
  const [salaryFormData, setSalaryFormData] = useState({
    staffId: "",
    amount: "",
    paymentDate: "",
  });

  useEffect(() => {
    if (success || error) {
      const timer = setTimeout(() => {
        setSuccess("");
        setError("");
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [success, error]);

  const loadSalaryHistory = async (
    page = 0,
    size = 8,
    forceRefresh = false,
  ) => {
    const cacheKey = `${SALARIES_CACHE_KEY}_${page}_${size}`;

    if (!forceRefresh) {
      const cached = cache.get(cacheKey);
      if (cached) {
        setSalaries(cached.content);
        setSalaryTotalPages(cached.totalPages);
        return;
      }
    }

    setLoading(true);
    setError("");

    try {
      const res = await salaryApi.getSalaryHistory(page, size);

      const pageData = res?.data || {};
      const content = Array.isArray(pageData.content) ? pageData.content : [];
      const totalPages = pageData.totalPages || 1;

      setSalaries(content);
      setSalaryTotalPages(totalPages);
      cache.set(cacheKey, { content, totalPages });
    } catch (err) {
      setError("Failed to load salary history.");
      console.error("Failed to load salary history:", err);
      setSalaries([]);
    } finally {
      setTimeout(() => setLoading(false), 400);
    }
  };

  const handleSalarySubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const dataToSend = {
        staffId: salaryFormData.staffId,
        amount: parseFloat(salaryFormData.amount),
        paymentDate: salaryFormData.paymentDate,
      };

      await salaryApi.recordSalary(dataToSend);
      setSuccess("Salary recorded!");

      if (typeof cache.invalidate === "function")
        cache.invalidate(SALARIES_CACHE_KEY);
      else if (typeof cache.clear === "function") cache.clear();

      resetSalaryForm();
      await loadSalaryHistory(0, 8, true);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to record salary.");
    } finally {
      setLoading(false);
    }
  };

  const resetSalaryForm = () => {
    setSalaryFormData({ staffId: "", amount: "", paymentDate: "" });
    setShowSalaryForm(false);
  };

  return {
    salaries,
    salaryTotalPages,
    loading,
    error,
    success,
    showSalaryForm,
    setShowSalaryForm,
    salaryFormData,
    setSalaryFormData,
    handleSalarySubmit,
    resetSalaryForm,
    loadSalaryHistory,
  };
};
