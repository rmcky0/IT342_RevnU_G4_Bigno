import { useState, useEffect, useMemo } from "react";
import { salesAPI } from "../api/salesApi";
import * as cache from "../../../shared/cache/dataCache";

const CACHE_KEY = "sales_page_";
const ITEMS_PER_PAGE = 9;

export const useSales = () => {
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalRecords, setTotalRecords] = useState(0);
  const [sortConfig, setSortConfig] = useState({
    key: "date",
    direction: "desc",
  });
  const [formData, setFormData] = useState({
    amount: "",
    tagNames: [],
    description: "",
  });
  const [tagInput, setTagInput] = useState("");

  useEffect(() => {
    if (!success && !error) return;
    const t = setTimeout(() => {
      setSuccess("");
      setError("");
    }, 3000);
    return () => clearTimeout(t);
  }, [success, error]);

  useEffect(() => {
    loadSales(currentPage);
  }, [currentPage]);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, sortConfig]);

  const loadSales = async (pageNumber, forceRefresh = false) => {
    const springPage = pageNumber - 1;
    const cacheKey = `${CACHE_KEY}${springPage}`;

    if (!forceRefresh) {
      const cached = cache.get(cacheKey);
      if (cached) {
        setSales(cached.content);
        setTotalPages(cached.totalPages);
        setTotalRecords(cached.totalElements);
        return;
      }
    }

    setLoading(true);
    setError("");
    try {
      const res = await salesAPI.getAllSales(springPage, ITEMS_PER_PAGE);

      const pageData = res?.data || {};
      const rawContent = Array.isArray(pageData.content)
        ? pageData.content
        : [];

      const activeSales = rawContent.filter((sale) => sale.status !== "CLOSED");

      setSales(activeSales);
      setTotalPages(pageData.totalPages || 1);
      setTotalRecords(pageData.totalElements || 0);

      cache.set(cacheKey, {
        content: activeSales,
        totalPages: pageData.totalPages || 1,
        totalElements: pageData.totalElements || 0,
      });
    } catch (err) {
      setError("Failed to load sales.");
      console.error("loadSales error:", err);
    } finally {
      setTimeout(() => setLoading(false), 400);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) setCurrentPage((p) => p + 1);
  };
  const handlePrevPage = () => {
    if (currentPage > 1) setCurrentPage((p) => p - 1);
  };

  const filteredSales = useMemo(() => {
    let list = sales;
    if (searchTerm) {
      const s = searchTerm.toLowerCase();
      list = list.filter(
        (sale) =>
          sale.tags?.some((tag) => tag.toLowerCase().includes(s)) ||
          (sale.description || "").toLowerCase().includes(s) ||
          sale.amount?.toString().includes(s),
      );
    }
    return [...list].sort((a, b) => {
      if (sortConfig.key === "amount") {
        const diff = parseFloat(a.amount || 0) - parseFloat(b.amount || 0);
        return sortConfig.direction === "asc" ? diff : -diff;
      }
      return 0;
    });
  }, [sales, searchTerm, sortConfig]);

  const pageTotalSales = filteredSales.reduce(
    (sum, s) => sum + (parseFloat(s.amount) || 0),
    0,
  );

  const handleAddTag = (e) => {
    if (e.key !== "Enter") return;
    e.preventDefault();
    const newTag = tagInput.trim().toUpperCase();
    if (newTag && !formData.tagNames.includes(newTag)) {
      setFormData((prev) => ({
        ...prev,
        tagNames: [...prev.tagNames, newTag],
      }));
    }
    setTagInput("");
  };

  const handleRemoveTag = (tagToRemove) => {
    setFormData((prev) => ({
      ...prev,
      tagNames: prev.tagNames.filter((t) => t !== tagToRemove),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const payload = {
        amount: parseFloat(formData.amount),
        tagNames: formData.tagNames,
        description: formData.description,
      };

      let targetPage = currentPage;

      if (editingId) {
        await salesAPI.updateSale(editingId, payload);
        setSuccess("Sale updated!");
      } else {
        await salesAPI.createSale(payload);
        setSuccess("Sale added!");
        setCurrentPage(1);
        targetPage = 1;
        setSortConfig({ key: "date", direction: "desc" });
      }
      cache.invalidatePrefix(CACHE_KEY);

      resetForm();
      await loadSales(targetPage, true);
    } catch (err) {
      setError(
        err?.response?.data?.error?.message ||
          err?.response?.data?.message ||
          "Save failed. Please try again.",
      );
      console.error("handleSubmit error:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (sale) => {
    setFormData({
      amount: sale.amount,
      tagNames: sale.tags || [],
      description: sale.description || "",
    });
    setEditingId(sale.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await salesAPI.deleteSale(id);
      setSuccess("Sale deleted!");
      cache.invalidatePrefix(CACHE_KEY);
      await loadSales(currentPage, true);
    } catch (err) {
      setError(
        err?.response?.data?.error?.message ||
          err?.response?.data?.message ||
          "Delete failed.",
      );
    }
  };

  const resetForm = () => {
    setFormData({ amount: "", tagNames: [], description: "" });
    setTagInput("");
    setEditingId(null);
    setShowForm(false);
  };

  const requestSort = (key) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === "desc" ? "asc" : "desc",
    }));
  };

  return {
    sales: filteredSales,
    paginatedSales: filteredSales,
    currentPage,
    totalPages,
    totalRecords,
    handleNextPage,
    handlePrevPage,
    loading,
    error,
    success,
    showForm,
    setShowForm,
    formData,
    setFormData,
    tagInput,
    setTagInput,
    handleAddTag,
    handleRemoveTag,
    searchTerm,
    setSearchTerm,
    editingId,
    totalSales: pageTotalSales,
    handleSubmit,
    handleEdit,
    handleDelete,
    resetForm,
    sortConfig,
    requestSort,
    refetch: () => loadSales(currentPage, true),
  };
};
