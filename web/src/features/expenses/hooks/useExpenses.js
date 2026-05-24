import { useState, useEffect, useMemo } from "react";
import { expensesAPI } from "../api/expensesApi";
import { categoriesAPI } from "../../categories/api/categoriesApi";
import * as cache from "../../../shared/cache/dataCache";
import { useToast } from "../../../shared/components/Toast";

const CACHE_KEY = "expense_page_";
const ITEMS_PER_PAGE = 9;

export const useExpenses = () => {
  const { showToast } = useToast();
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [receiptFile, setReceiptFile] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalRecords, setTotalRecords] = useState(0);
  const [pageOpenAmounts, setPageOpenAmounts] = useState({});
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
        const pageAmt = cached.content.reduce((s, r) => s + (parseFloat(r.amount) || 0), 0);
        setPageOpenAmounts((prev) => ({ ...prev, [springPage]: pageAmt }));
        return;
      }
    }

    if (forceRefresh) setPageOpenAmounts({});

    setLoading(true);
    try {
      const res = await expensesAPI.getAllExpenses(springPage, ITEMS_PER_PAGE);

      const pageData = res?.data || {};
      const rawContent = Array.isArray(pageData.content)
        ? pageData.content
        : [];

      const activeExpenses = rawContent.filter((e) => e.status !== "CLOSED");
      const pageAmt = activeExpenses.reduce((s, r) => s + (parseFloat(r.amount) || 0), 0);

      setExpenses(activeExpenses);
      setTotalPages(pageData.totalPages || 1);
      setTotalRecords(pageData.totalElements || 0);
      setPageOpenAmounts((prev) => ({ ...prev, [springPage]: pageAmt }));

      cache.set(cacheKey, {
        content: activeExpenses,
        totalPages: pageData.totalPages || 1,
        totalElements: pageData.totalElements || 0,
      });
    } catch (err) {
      showToast("error", "Failed to load expenses.");
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

  const totalExpenses = Object.values(pageOpenAmounts).reduce((sum, a) => sum + a, 0);

  const handleSubmit = async (e) => {
    e.preventDefault();
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
        showToast("success", "Expense updated!");
      } else {
        const createRes = await expensesAPI.createExpense(payload);
        expenseId = createRes?.data?.id;
        showToast("success", "Expense added!");
        setCurrentPage(1);
        targetPage = 1;
        setSortConfig({ key: "date", direction: "desc" });
      }

      if (receiptFile && expenseId) {
        await expensesAPI.uploadReceipt(expenseId, receiptFile);
        showToast("success", "Expense and receipt saved!");
      }

      cache.invalidatePrefix(CACHE_KEY);
      resetForm();
      await loadExpenses(targetPage, true);
    } catch (err) {
      showToast(
        "error",
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
      showToast("success", "Expense deleted!");
      cache.invalidatePrefix(CACHE_KEY);
      await loadExpenses(currentPage, true);
    } catch (err) {
      showToast(
        "error",
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
