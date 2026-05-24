import { useState } from "react";
import { reportingApi } from "../api/reportingApi";
import { useToast } from "../../../shared/components/Toast";

export const useReporting = () => {
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);
  const [summaries, setSummaries] = useState([]);
  const [currentSummary, setCurrentSummary] = useState(null);

  const closeDay = async (date) => {
    setLoading(true);
    try {
      await reportingApi.closeDay(date);
      showToast("success", "Records locked! EOD summary saved.");
      return true;
    } catch (err) {
      const errorMsg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        "Failed to lock records. They may already be locked for today.";
      showToast("error", errorMsg);
      console.error("Failed to close day:", err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const loadAllSummaries = async () => {
    setLoading(true);
    try {
      const response = await reportingApi.getAllSummaries();
      setSummaries(response?.data || []);
      return true;
    } catch (err) {
      showToast("error", "Failed to load archived records.");
      console.error("Failed to load summaries:", err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const loadDayDetail = async (date) => {
    setLoading(true);
    try {
      const response = await reportingApi.getDayDetail(date);
      setCurrentSummary(response?.data);
      return true;
    } catch (err) {
      showToast("error", "Failed to load day details.");
      console.error("Failed to load day detail:", err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    loading,
    summaries,
    currentSummary,
    closeDay,
    loadAllSummaries,
    loadDayDetail,
  };
};
