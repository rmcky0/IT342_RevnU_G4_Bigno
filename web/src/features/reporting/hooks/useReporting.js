import { useState } from "react";
import { reportingApi } from "../api/reportingApi";

export const useReporting = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [summaries, setSummaries] = useState([]);
  const [currentSummary, setCurrentSummary] = useState(null);

  const closeDay = async (date) => {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const response = await reportingApi.closeDay(date);
      setSuccess("Records locked! EOD summary saved.");
      return true;
    } catch (err) {
      const errorMsg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        "Failed to lock records. They may already be locked for today.";
      setError(errorMsg);
      console.error("Failed to close day:", err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const loadAllSummaries = async () => {
    setLoading(true);
    setError("");
    try {
      const response = await reportingApi.getAllSummaries();
      setSummaries(response?.data || []);
      return true;
    } catch (err) {
      setError("Failed to load archived records.");
      console.error("Failed to load summaries:", err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const loadDayDetail = async (date) => {
    setLoading(true);
    setError("");
    try {
      const response = await reportingApi.getDayDetail(date);
      setCurrentSummary(response?.data);
      return true;
    } catch (err) {
      setError("Failed to load day details.");
      console.error("Failed to load day detail:", err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    loading,
    error,
    success,
    summaries,
    currentSummary,
    closeDay,
    loadAllSummaries,
    loadDayDetail,
    clearError: () => setError(""),
    clearSuccess: () => setSuccess(""),
  };
};
