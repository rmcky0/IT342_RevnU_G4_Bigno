import { useState, useEffect, useMemo } from "react";
import { salesAPI } from "../api/salesApi";
import { categoriesAPI } from "../../categories/api/categoriesApi";
import * as cache from "../../../shared/cache/dataCache";

const CACHE_KEY = "sales_page_";
const ITEMS_PER_PAGE = 9;

export const useSales = () => {
  const [sales, setSales] = useState([]);
  const [categories, setCategories] = useState([]);
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
    categoryId: "",
    notes: "",
  });

  useEffect(() => {
    categoriesAPI
      .getCategories("SALE")
      .then(setCategories)
      .catch(() => {});
  }, []);

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
          (sale.categoryName || "").toLowerCase().includes(s) ||
          (sale.notes || "").toLowerCase().includes(s) ||
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const payload = {
        amount: parseFloat(formData.amount),
        categoryId: formData.categoryId,
        notes: formData.notes,
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
      categoryId: sale.categoryId || "",
      notes: sale.notes || "",
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
    setFormData({ amount: "", categoryId: "", notes: "" });
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
    categories,
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
