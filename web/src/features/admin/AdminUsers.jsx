import { useEffect, useState, useMemo } from "react";
import { adminApi } from "./api/adminApi";
import { API_BASE_URL } from "../../shared/api/axios";
import * as cache from "../../shared/cache/dataCache";
import {
  Search,
  X,
  ShieldCheck,
  ShieldOff,
  ShieldAlert,
  Trash2,
  ChevronLeft,
  ChevronRight,
  Users,
} from "lucide-react";

const CACHE_KEY = "admin:users";
const PAGE_SIZE = 12;

export const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [actionLoading, setActionLoading] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [confirmAction, setConfirmAction] = useState(null);
  const [confirmText, setConfirmText] = useState("");

  const loadUsers = async (force = false) => {
    setLoading(true);
    setError("");
    try {
      let u = !force ? cache.get(CACHE_KEY) : null;
      if (!u) {
        u = await adminApi.getAllUsers();
        cache.set(CACHE_KEY, u, 3 * 60 * 1000);
      }
      setUsers(Array.isArray(u) ? u : []);
    } catch {
      setError("Failed to load restaurateurs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterStatus]);

  const filtered = useMemo(() => {
    let list = users.filter((u) => u.role !== "ADMIN");

    if (filterStatus === "active") list = list.filter((u) => !u.suspended);
    if (filterStatus === "suspended") list = list.filter((u) => u.suspended);

    if (searchTerm) {
      const s = searchTerm.toLowerCase();
      list = list.filter(
        (u) =>
          (u.fullname || "").toLowerCase().includes(s) ||
          (u.email || "").toLowerCase().includes(s) ||
          (u.restaurantName || "").toLowerCase().includes(s),
      );
    }
    return list;
  }, [users, searchTerm, filterStatus]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const paginated = filtered.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE,
  );

  const act = async (id, fn) => {
    setActionLoading(id);
    try {
      const updated = await fn(id);
      setUsers((prev) => prev.map((u) => (u.id === id ? updated : u)));
      cache.invalidate(CACHE_KEY, "admin:stats");
    } catch (e) {
      setError(e?.response?.data?.error?.message ?? "Action failed.");
    } finally {
      setActionLoading(null);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    setActionLoading(confirmDelete.id);
    try {
      await adminApi.deleteUser(confirmDelete.id);
      setUsers((prev) => prev.filter((u) => u.id !== confirmDelete.id));
      cache.invalidate(CACHE_KEY, "admin:stats");
      setConfirmDelete(null);
    } catch (e) {
      setError(e?.response?.data?.error?.message ?? "Delete failed.");
    } finally {
      setActionLoading(null);
    }
  };

  const handleConfirmAction = async () => {
    if (!confirmAction) return;
    const targetText = getConfirmTarget(confirmAction);
    if (confirmText.trim().toLowerCase() !== targetText.toLowerCase()) {
      setError("Confirmation text does not match.");
      return;
    }
    const { user, action } = confirmAction;
    const actionFn =
      action === "suspend" ? adminApi.suspendUser : adminApi.promoteToAdmin;
    await act(user.id, actionFn);
    setConfirmAction(null);
    setConfirmText("");
  };

  const getConfirmTarget = (action) => {
    const email = action?.user?.email || "";
    return action?.action === "suspend"
      ? `SUSPEND ${email}`
      : `PROMOTE ${email}`;
  };

  return (
    <div className="space-y-6 text-[#1e1b4b]">
      {/* Header */}
      <div className="flex items-center gap-3">
        <div className="p-2 bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
          <Users className="w-5 h-5 text-white" />
        </div>
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Restaurateurs</h1>
          <p className="text-xs text-gray-400 font-medium">
            {filtered.length} {filterStatus !== "all" ? filterStatus : "total"}{" "}
            accounts
          </p>
        </div>
      </div>

      {error && (
        <div className="px-4 py-3 bg-red-50 border border-red-100 rounded-xl text-red-600 text-sm font-medium flex items-center gap-2">
          {error}
          <button onClick={() => setError("")} className="ml-auto">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Controls */}
      <div className="flex items-center gap-3 bg-white p-2.5 rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.02)]">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search by name, email, restaurant..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-9 pr-8 py-2 rounded-lg border border-transparent bg-gray-50/50 text-sm text-[#1e1b4b] placeholder:text-gray-400 focus:bg-white focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 outline-none transition-all"
          />
          {searchTerm && (
            <button
              onClick={() => setSearchTerm("")}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        <div className="flex gap-1 p-1 bg-gray-50/80 rounded-lg border border-gray-100">
          {[
            ["all", "All"],
            ["active", "Active"],
            ["suspended", "Suspended"],
          ].map(([val, lbl]) => (
            <button
              key={val}
              onClick={() => setFilterStatus(val)}
              className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-all
                ${filterStatus === val ? "bg-white text-[#7c83fd] shadow-sm border border-gray-100" : "text-gray-500 hover:text-[#1e1b4b]"}`}
            >
              {lbl}
            </button>
          ))}
        </div>

        <div className="flex items-center ml-auto gap-1 bg-gray-50/50 rounded-lg border border-gray-100">
          <span className="text-xs font-semibold text-gray-500 px-3 border-r border-gray-100 min-w-[4.5rem] text-center">
            {currentPage} / {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
            disabled={currentPage === 1}
            className="p-1.5 text-gray-400 hover:text-[#7c83fd] disabled:opacity-30 transition-colors"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <button
            onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
            disabled={currentPage === totalPages}
            className="p-1.5 text-gray-400 hover:text-[#7c83fd] disabled:opacity-30 transition-colors rounded-r-lg"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-[0_4px_20px_rgb(0,0,0,0.03)] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse whitespace-nowrap">
            <thead className="bg-gradient-to-r from-[#8f9df7] to-[#9faaf5] text-white">
              <tr>
                {[
                  "Restaurateur",
                  "Restaurant",
                  "Joined",
                  "Role",
                  "Status",
                  "Actions",
                ].map((h) => (
                  <th
                    key={h}
                    className="px-6 py-3 font-semibold text-xs tracking-wider uppercase"
                  >
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {loading ? (
                [...Array(6)].map((_, i) => (
                  <tr key={i} className="animate-pulse">
                    {[...Array(6)].map((_, j) => (
                      <td key={j} className="px-6 py-4">
                        <div className="h-3.5 bg-gray-100 rounded w-24" />
                      </td>
                    ))}
                  </tr>
                ))
              ) : paginated.length === 0 ? (
                <tr>
                  <td
                    colSpan={6}
                    className="px-6 py-12 text-center text-gray-400 text-sm font-medium"
                  >
                    No restaurateurs found.
                  </td>
                </tr>
              ) : (
                paginated.map((u, i) => {
                  const busy = actionLoading === u.id;
                  return (
                    <tr
                      key={u.id}
                      className={`group transition-colors ${i % 2 === 0 ? "bg-white" : "bg-[#f8f9ff]/40"} hover:bg-indigo-50/30`}
                    >
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-9 h-9 rounded-full overflow-hidden bg-indigo-100 flex items-center justify-center text-[#7c83fd] font-black text-sm shrink-0">
                            <img
                              src={
                                u.avatarFileId
                                  ? `${API_BASE_URL}/files/${u.avatarFileId}`
                                  : `https://ui-avatars.com/api/?name=${encodeURIComponent(u.fullname || u.email || "U")}&background=c7d2fe&color=4338ca&bold=true`
                              }
                              alt="avatar"
                              className="w-full h-full object-cover"
                              onError={(e) => {
                                e.currentTarget.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(u.fullname || u.email || "U")}&background=c7d2fe&color=4338ca&bold=true`;
                              }}
                            />
                          </div>
                          <div>
                            <p className="text-sm font-bold text-[#1e1b4b]">
                              {u.fullname || "—"}
                            </p>
                            <p className="text-xs text-gray-400">{u.email}</p>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 font-medium">
                        {u.restaurantName || (
                          <span className="text-gray-400 italic text-xs">
                            Setup pending
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4 text-xs text-gray-500 font-medium">
                        {new Date(u.createdAt).toLocaleDateString("en-PH", {
                          month: "short",
                          day: "numeric",
                          year: "numeric",
                        })}
                      </td>
                      <td className="px-6 py-4">
                        <span
                          className={`px-2.5 py-1 rounded-lg text-[11px] font-bold uppercase tracking-wide
                          ${
                            u.role === "ADMIN"
                              ? "bg-indigo-50 text-[#7c83fd] border border-indigo-100"
                              : "bg-gray-50 text-gray-600 border border-gray-100"
                          }`}
                        >
                          {u.role}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <span
                          className={`px-2.5 py-1 rounded-lg text-[11px] font-bold uppercase tracking-wide
                          ${
                            u.suspended
                              ? "bg-red-50 text-red-600 border border-red-100"
                              : "bg-emerald-50 text-emerald-700 border border-emerald-100"
                          }`}
                        >
                          {u.suspended ? "Suspended" : "Active"}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        {u.role !== "ADMIN" && (
                          <div className="flex items-center gap-1.5 opacity-0 group-hover:opacity-100 transition-opacity">
                            {/* Suspend / Activate */}
                            {u.suspended ? (
                              <button
                                disabled={busy}
                                title="Activate"
                                onClick={() => act(u.id, adminApi.activateUser)}
                                className="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-md transition-colors disabled:opacity-40"
                              >
                                <ShieldCheck className="w-4 h-4" />
                              </button>
                            ) : (
                              <button
                                disabled={busy}
                                title="Suspend"
                                onClick={() => {
                                  setConfirmText("");
                                  setConfirmAction({
                                    user: u,
                                    action: "suspend",
                                  });
                                }}
                                className="p-1.5 text-amber-600 hover:bg-amber-50 rounded-md transition-colors disabled:opacity-40"
                              >
                                <ShieldOff className="w-4 h-4" />
                              </button>
                            )}
                            {/* Promote to Admin */}
                            <button
                              disabled={busy}
                              title="Promote to Admin"
                              onClick={() => {
                                setConfirmText("");
                                setConfirmAction({
                                  user: u,
                                  action: "promote",
                                });
                              }}
                              className="p-1.5 text-[#7c83fd] hover:bg-indigo-50 rounded-md transition-colors disabled:opacity-40"
                            >
                              <ShieldAlert className="w-4 h-4" />
                            </button>
                            <div className="w-px h-3.5 bg-gray-200 mx-0.5" />
                            {/* Delete */}
                            <button
                              disabled={busy}
                              title="Delete account"
                              onClick={() => setConfirmDelete(u)}
                              className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-md transition-colors disabled:opacity-40"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Delete confirm modal */}
      {confirmDelete && (
        <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl border border-gray-100 overflow-hidden">
            <div className="p-6 text-center">
              <div className="w-14 h-14 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4 border border-red-100">
                <Trash2 className="w-7 h-7" />
              </div>
              <h3 className="text-lg font-bold text-[#1e1b4b] mb-2">
                Delete Account
              </h3>
              <p className="text-sm text-gray-500 mb-1">
                Permanently delete{" "}
                <span className="font-bold text-[#1e1b4b]">
                  {confirmDelete.fullname || confirmDelete.email}
                </span>
                ?
              </p>
              <p className="text-xs text-red-500 font-semibold mb-6">
                This will also delete their restaurant and ALL associated
                records. This cannot be undone.
              </p>
              <div className="flex gap-3">
                <button
                  onClick={() => setConfirmDelete(null)}
                  className="flex-1 py-2.5 bg-gray-50 text-gray-600 rounded-lg font-semibold text-sm hover:bg-gray-100 border border-gray-200 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={handleDelete}
                  disabled={actionLoading === confirmDelete.id}
                  className="flex-1 py-2.5 bg-red-500 text-white rounded-lg font-semibold text-sm hover:bg-red-600 shadow-md shadow-red-200 transition-colors disabled:opacity-60"
                >
                  {actionLoading === confirmDelete.id ? "Deleting…" : "Delete"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {confirmAction && (
        <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl border border-gray-100 overflow-hidden">
            <div className="p-6 text-center">
              <div
                className={`w-14 h-14 rounded-full flex items-center justify-center mx-auto mb-4 border ${confirmAction.action === "suspend" ? "bg-amber-50 text-amber-500 border-amber-100" : "bg-indigo-50 text-[#7c83fd] border-indigo-100"}`}
              >
                {confirmAction.action === "suspend" ? (
                  <ShieldOff className="w-7 h-7" />
                ) : (
                  <ShieldAlert className="w-7 h-7" />
                )}
              </div>
              <h3 className="text-lg font-bold text-[#1e1b4b] mb-2">
                {confirmAction.action === "suspend"
                  ? "Suspend Account"
                  : "Promote to Admin"}
              </h3>
              <p className="text-sm text-gray-500 mb-1">
                {confirmAction.action === "suspend" ? "Suspend" : "Promote"}{" "}
                <span className="font-bold text-[#1e1b4b]">
                  {confirmAction.user.fullname || confirmAction.user.email}
                </span>
                ?
              </p>
              <p className="text-xs text-gray-500 font-semibold mb-6">
                {confirmAction.action === "suspend"
                  ? "This will block the restaurateur from accessing their account."
                  : "This grants full admin access and removes restaurateur restrictions."}
              </p>
              <div className="flex gap-3">
                <button
                  onClick={() => {
                    setConfirmAction(null);
                    setConfirmText("");
                  }}
                  className="flex-1 py-2.5 bg-gray-50 text-gray-600 rounded-lg font-semibold text-sm hover:bg-gray-100 border border-gray-200 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={handleConfirmAction}
                  disabled={
                    actionLoading === confirmAction.user.id ||
                    confirmText.trim().toLowerCase() !==
                      getConfirmTarget(confirmAction).toLowerCase()
                  }
                  className={`flex-1 py-2.5 text-white rounded-lg font-semibold text-sm shadow-md transition-colors disabled:opacity-60 ${confirmAction.action === "suspend" ? "bg-amber-500 hover:bg-amber-600 shadow-amber-200" : "bg-[#7c83fd] hover:bg-[#6b72f5] shadow-indigo-200"}`}
                >
                  {actionLoading === confirmAction.user.id
                    ? "Processing…"
                    : "Confirm"}
                </button>
              </div>
            </div>
            <div className="px-6 pb-6">
              <label className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-2">
                Type to confirm
              </label>
              <p className="text-xs text-gray-500 mb-2">
                Enter{" "}
                <span className="font-bold">
                  {getConfirmTarget(confirmAction)}
                </span>
              </p>
              <input
                type="text"
                value={confirmText}
                onChange={(e) => setConfirmText(e.target.value)}
                placeholder={getConfirmTarget(confirmAction)}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm text-[#1e1b4b] placeholder:text-gray-300 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 outline-none transition-all"
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
