import { useState, useEffect, useMemo } from "react";
import { expensesAPI } from "../api/expensesApi";
import { categoriesAPI } from "../../categories/api/categoriesApi";
import * as cache from "../../../shared/cache/dataCache";

const CACHE_KEY = "expense_page_";
const ITEMS_PER_PAGE = 9;

export const useExpenses = () => {
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [receiptFile, setReceiptFile] = useState(null);
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
      .getCategories("EXPENSE")
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
    loadExpenses(currentPage);
  }, [currentPage]);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, sortConfig]);

  const loadExpenses = async (pageNumber, forceRefresh = false) => {
    const springPage = pageNumber - 1;
    const cacheKey = `${CACHE_KEY}${springPage}`;

    if (!forceRefresh) {
      const cached = cache.get(cacheKey);
      if (cached) {
        setExpenses(cached.content);
        setTotalPages(cached.totalPages);
        setTotalRecords(cached.totalElements);
        return;
      }
    }

    setLoading(true);
    setError("");
    try {
      const res = await expensesAPI.getAllExpenses(springPage, ITEMS_PER_PAGE);

      const pageData = res?.data || {};
      const rawContent = Array.isArray(pageData.content)
        ? pageData.content
        : [];

      const activeExpenses = rawContent.filter((e) => e.status !== "CLOSED");

      setExpenses(activeExpenses);
      setTotalPages(pageData.totalPages || 1);
      setTotalRecords(pageData.totalElements || 0);

      cache.set(cacheKey, {
        content: activeExpenses,
        totalPages: pageData.totalPages || 1,
        totalElements: pageData.totalElements || 0,
      });
    } catch (err) {
      setError("Failed to load expenses.");
      console.error("loadExpenses error:", err);
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

  const filteredExpenses = useMemo(() => {
    let list = expenses;
    if (searchTerm) {
      const s = searchTerm.toLowerCase();
      list = list.filter(
        (e) =>
          (e.categoryName || "").toLowerCase().includes(s) ||
          (e.notes || "").toLowerCase().includes(s) ||
          e.amount?.toString().includes(s),
      );
    }
    return [...list].sort((a, b) => {
      if (sortConfig.key === "amount") {
        const diff = (parseFloat(a.amount) || 0) - (parseFloat(b.amount) || 0);
        return sortConfig.direction === "asc" ? diff : -diff;
      }
      return 0;
    });
  }, [expenses, searchTerm, sortConfig]);

  const totalExpenses = filteredExpenses.reduce(
    (sum, e) => sum + (parseFloat(e.amount) || 0),
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

      let expenseId;
      let targetPage = currentPage;

      if (editingId) {
        await expensesAPI.updateExpense(editingId, payload);
        expenseId = editingId;
        setSuccess("Expense updated!");
      } else {
        const createRes = await expensesAPI.createExpense(payload);
        expenseId = createRes?.data?.id;
        setSuccess("Expense added!");
        setCurrentPage(1);
        targetPage = 1;
        setSortConfig({ key: "date", direction: "desc" });
      }

      if (receiptFile && expenseId) {
        await expensesAPI.uploadReceipt(expenseId, receiptFile);
        setSuccess("Expense and receipt saved!");
      }

      cache.invalidatePrefix(CACHE_KEY);
      resetForm();
      await loadExpenses(targetPage, true);
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

  const handleEdit = (expense) => {
    setFormData({
      amount: expense.amount,
      categoryId: expense.categoryId || "",
      notes: expense.notes || "",
    });
    setEditingId(expense.id);
    setReceiptFile(null);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await expensesAPI.deleteExpense(id);
      setSuccess("Expense deleted!");
      cache.invalidatePrefix(CACHE_KEY);
      await loadExpenses(currentPage, true);
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
    setReceiptFile(null);
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
    expenses: filteredExpenses,
    paginatedExpenses: filteredExpenses,
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
    receiptFile,
    setReceiptFile,
    searchTerm,
    setSearchTerm,
    editingId,
    totalExpenses,
    handleSubmit,
    handleEdit,
    handleDelete,
    resetForm,
    sortConfig,
    requestSort,
    refetch: () => loadExpenses(currentPage, true),
  };
};
