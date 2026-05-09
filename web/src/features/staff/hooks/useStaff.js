import { useState, useEffect, useMemo } from "react";
import { staffApi } from "../api/staffApi";
import * as cache from "../../../shared/cache/dataCache";

const STAFF_CACHE_KEY = "staff";

export const useStaff = () => {
  const [activeTab, setActiveTab] = useState("directory");
  const [staffList, setStaffList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [showStaffForm, setShowStaffForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  const [staffFormData, setStaffFormData] = useState({
    fullname: "",
    position: "",
    salaryRate: "",
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

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async (forceRefresh = false) => {
    if (!forceRefresh) {
      const cachedStaff = cache.get(STAFF_CACHE_KEY);
      if (cachedStaff) {
        setStaffList(cachedStaff);
        return;
      }
    }

    setLoading(true);
    setError("");
    try {
      const staffRes = await staffApi.getAllStaff();
      const staffData = Array.isArray(staffRes?.data) ? staffRes.data : [];

      setStaffList(staffData);
      cache.set(STAFF_CACHE_KEY, staffData);
    } catch (err) {
      setError("Failed to load staff data.");
      console.error("Failed to load staff data:", err);
      setStaffList([]);
    } finally {
      setTimeout(() => setLoading(false), 400);
    }
  };

  const handleStaffSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const dataToSend = {
        fullname: staffFormData.fullname,
        position: staffFormData.position,
        salaryRate: parseFloat(staffFormData.salaryRate),
      };

      if (editingId) {
        await staffApi.updateStaff(editingId, dataToSend);
        setSuccess("Staff updated!");
      } else {
        await staffApi.createStaff(dataToSend);
        setSuccess("Staff added!");
      }

      if (typeof cache.invalidate === "function") {
        cache.invalidate(STAFF_CACHE_KEY);
      } else if (typeof cache.clear === "function") {
        cache.clear();
      }

      resetStaffForm();
      await loadData(true);
    } catch (err) {
      const msg =
        err?.response?.data?.error?.message ??
        err?.response?.data?.message ??
        "Failed to save staff.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleEditStaff = (staff) => {
    setStaffFormData({
      fullname: staff.fullname || "",
      position: staff.position || "",
      salaryRate: staff.salaryRate || "",
    });
    setEditingId(staff.id);
    setShowStaffForm(true);
  };

  const handleDeleteStaff = async (id) => {
    try {
      await staffApi.deleteStaff(id);
      setSuccess("Staff member deleted!");

      if (typeof cache.invalidate === "function") {
        cache.invalidate(STAFF_CACHE_KEY);
      } else if (typeof cache.clear === "function") {
        cache.clear();
      }

      await loadData(true);
    } catch (err) {
      setError(
        err?.response?.data?.error?.message ?? "Failed to delete staff.",
      );
    }
  };

  const resetStaffForm = () => {
    setStaffFormData({ fullname: "", position: "", salaryRate: "" });
    setEditingId(null);
    setShowStaffForm(false);
  };

  const filteredStaff = useMemo(() => {
    if (!Array.isArray(staffList)) return [];
    const s = searchTerm.toLowerCase();
    return staffList.filter(
      (member) =>
        (member.fullname || "").toLowerCase().includes(s) ||
        (member.position || "").toLowerCase().includes(s),
    );
  }, [staffList, searchTerm]);

  return {
    activeTab,
    setActiveTab,
    staffList: filteredStaff,
    loading,
    error,
    success,
    showStaffForm,
    setShowStaffForm,
    staffFormData,
    setStaffFormData,
    searchTerm,
    setSearchTerm,
    editingId,
    handleStaffSubmit,
    handleEditStaff,
    handleDeleteStaff,
    resetStaffForm,
    refetch: () => loadData(true),
  };
};
