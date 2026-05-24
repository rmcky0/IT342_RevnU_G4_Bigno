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
  RefreshCw,
} from "lucide-react";
import { useToast } from "../../shared/components/Toast";

const CACHE_KEY = "admin:users";
const PAGE_SIZE = 12;

const avatarSrc = (u) =>
  u.avatarFileId
    ? `${API_BASE_URL}/files/${u.avatarFileId}`
    : `https://ui-avatars.com/api/?name=${encodeURIComponent(u.fullname || u.email || "U")}&background=c7d2fe&color=4338ca&bold=true`;

const ModalOverlay = ({ children, onClose }) => (
  <div
    className="fixed inset-0 bg-[#1e1b4b]/40 backdrop-blur-[3px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200"
    onClick={(e) => e.target === e.currentTarget && onClose()}
  >
    {children}
  </div>
);

export const AdminUsers = () => {
  const { showToast } = useToast();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [actionLoading, setActionLoading] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [confirmAction, setConfirmAction] = useState(null);
  const [confirmText, setConfirmText] = useState("");

  const loadUsers = async (force = false) => {
    setLoading(true);
    try {
      let u = !force ? cache.get(CACHE_KEY) : null;
      if (!u) {
        u = await adminApi.getAllUsers();
        cache.set(CACHE_KEY, u, 3 * 60 * 1000);
      }
      setUsers(Array.isArray(u) ? u : []);
    } catch {
      showToast("error", "Failed to load restaurateurs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadUsers(); }, []);
  useEffect(() => { setCurrentPage(1); }, [searchTerm, filterStatus]);

  const filtered = useMemo(() => {
    let list = users.filter((u) => u.role !== "ADMIN");
    if (filterStatus === "active")    list = list.filter((u) => !u.suspended);
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
  const paginated = filtered.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

  const act = async (id, fn) => {
    setActionLoading(id);
    try {
      const updated = await fn(id);
      setUsers((prev) => prev.map((u) => (u.id === id ? updated : u)));
      cache.invalidate(CACHE_KEY, "admin:stats");
    } catch (e) {
      showToast("error", e?.response?.data?.error?.message ?? "Action failed.");
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
      showToast("error", e?.response?.data?.error?.message ?? "Delete failed.");
    } finally {
      setActionLoading(null);
    }
  };

  const getConfirmTarget = (ca) => {
    const email = ca?.user?.email || "";
    return ca?.action === "suspend" ? `SUSPEND ${email}` : `PROMOTE ${email}`;
  };

  const handleConfirmAction = async () => {
    if (!confirmAction) return;
    if (confirmText.trim().toLowerCase() !== getConfirmTarget(confirmAction).toLowerCase()) {
      showToast("error", "Confirmation text does not match.");
      return;
    }
    const { user, action } = confirmAction;
    await act(user.id, action === "suspend" ? adminApi.suspendUser : adminApi.promoteToAdmin);
    setConfirmAction(null);
    setConfirmText("");
  };

  return (
    <div className="flex flex-col gap-3 text-[#1e1b4b] h-full overflow-y-auto custom-scrollbar pb-4">

      {/* ── Header ── */}
      <div className="shrink-0 flex items-center justify-between gap-3 flex-wrap">
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
            <Users className="w-4.5 h-4.5 text-white" />
          </div>
          <div className="min-w-0">
            <h1 className="text-lg font-bold tracking-tight truncate">Restaurateurs</h1>
            <p className="text-[11px] font-medium text-gray-400 mt-0.5">
              {filtered.length} {filterStatus !== "all" ? filterStatus : "total"} accounts
            </p>
          </div>
        </div>
        <button
          onClick={() => loadUsers(true)}
          className="p-2.5 bg-white border border-gray-200 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] rounded-xl transition-colors shrink-0"
          title="Refresh"
        >
          <RefreshCw className="w-4 h-4" />
        </button>
      </div>

      {/* ── Control bar ── */}
      <div className="shrink-0 flex items-center gap-2 flex-wrap">
        {/* Search */}
        <div className="relative flex-1 min-w-48">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400 pointer-events-none" />
          <input
            type="text"
            placeholder="Search by name, email, restaurant…"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-9 pr-8 py-2.5 bg-white rounded-xl border border-gray-200 text-[13px] text-[#1e1b4b] placeholder:text-gray-400 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 outline-none transition-all"
          />
          {searchTerm && (
            <button
              onClick={() => setSearchTerm("")}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 transition-colors"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        {/* Status filter */}
        <div className="flex items-center bg-gray-100 rounded-xl p-1 gap-1 shrink-0">
          {[["all", "All"], ["active", "Active"], ["suspended", "Suspended"]].map(([val, lbl]) => (
            <button
              key={val}
              onClick={() => setFilterStatus(val)}
              className={`px-3 py-1.5 rounded-lg text-[12px] font-bold transition-all ${
                filterStatus === val
                  ? "bg-white text-[#7c83fd] shadow-sm"
                  : "text-gray-400 hover:text-gray-600"
              }`}
            >
              {lbl}
            </button>
          ))}
        </div>

        {/* Pagination */}
        <div className="flex items-center bg-white rounded-xl border border-gray-200 shrink-0">
          <button
            onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
            disabled={currentPage === 1}
            className="p-2.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] rounded-l-xl disabled:opacity-30 transition-colors"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <span className="px-3.5 text-[12px] font-bold text-gray-500 border-x border-gray-200">
            {currentPage} / {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
            disabled={currentPage === totalPages}
            className="p-2.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] rounded-r-xl disabled:opacity-30 transition-colors"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* ── Table ── */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse whitespace-nowrap">
            <thead className="bg-gray-50">
              <tr>
                {["Restaurateur", "Restaurant", "Joined", "Role", "Status", "Actions"].map((h) => (
                  <th key={h} className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {loading ? (
                [...Array(6)].map((_, i) => (
                  <tr key={i} className="animate-pulse border-b border-gray-50">
                    {[...Array(6)].map((_, j) => (
                      <td key={j} className="px-5 py-4">
                        <div className="h-3.5 bg-gray-100 rounded-full w-24" />
                      </td>
                    ))}
                  </tr>
                ))
              ) : paginated.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-16 text-center">
                    <div className="flex flex-col items-center gap-2">
                      <div className="w-12 h-12 rounded-2xl bg-indigo-50 flex items-center justify-center">
                        <Users className="w-6 h-6 text-[#7c83fd]/40" />
                      </div>
                      <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest">No restaurateurs found</p>
                    </div>
                  </td>
                </tr>
              ) : (
                paginated.map((u) => {
                  const busy = actionLoading === u.id;
                  return (
                    <tr key={u.id} className="group hover:bg-[#f8f9ff] transition-colors border-b border-gray-50 last:border-0">
                      {/* Restaurateur */}
                      <td className="px-5 py-3.5">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-xl overflow-hidden bg-[#7c83fd]/10 shrink-0">
                            <img
                              src={avatarSrc(u)}
                              alt="avatar"
                              className="w-full h-full object-cover"
                              onError={(e) => { e.currentTarget.src = avatarSrc(u); }}
                            />
                          </div>
                          <div>
                            <p className="text-[13px] font-bold text-[#1e1b4b]">{u.fullname || "—"}</p>
                            <p className="text-[10px] text-gray-400 font-medium">{u.email}</p>
                          </div>
                        </div>
                      </td>

                      {/* Restaurant */}
                      <td className="px-5 py-3.5 text-[13px] font-medium text-gray-600">
                        {u.restaurantName || (
                          <span className="inline-flex items-center text-[10px] font-bold text-amber-600 bg-amber-50 border border-amber-100 rounded-full px-2.5 py-0.5 uppercase tracking-wider">
                            Setup pending
                          </span>
                        )}
                      </td>

                      {/* Joined */}
                      <td className="px-5 py-3.5 text-[12px] text-gray-400 font-medium">
                        {new Date(u.createdAt).toLocaleDateString("en-PH", { month: "short", day: "numeric", year: "numeric" })}
                      </td>

                      {/* Role */}
                      <td className="px-5 py-3.5">
                        <span className={`inline-flex items-center text-[10px] font-bold rounded-full px-2.5 py-0.5 border uppercase tracking-wider ${
                          u.role === "ADMIN"
                            ? "bg-[#f0f1ff] text-[#7c83fd] border-[#d6d9ff]"
                            : "bg-gray-50 text-gray-500 border-gray-200"
                        }`}>
                          {u.role}
                        </span>
                      </td>

                      {/* Status */}
                      <td className="px-5 py-3.5">
                        <span className={`inline-flex items-center text-[10px] font-bold rounded-full px-2.5 py-0.5 border uppercase tracking-wider ${
                          u.suspended
                            ? "bg-red-50 text-red-600 border-red-100"
                            : "bg-emerald-50 text-emerald-700 border-emerald-100"
                        }`}>
                          {u.suspended ? "Suspended" : "Active"}
                        </span>
                      </td>

                      {/* Actions */}
                      <td className="px-5 py-3.5">
                        {u.role !== "ADMIN" && (
                          <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                            {u.suspended ? (
                              <button
                                disabled={busy}
                                title="Activate"
                                onClick={() => act(u.id, adminApi.activateUser)}
                                className="p-1.5 text-emerald-500 hover:bg-emerald-50 rounded-lg transition-colors disabled:opacity-40"
                              >
                                <ShieldCheck className="w-4 h-4" />
                              </button>
                            ) : (
                              <button
                                disabled={busy}
                                title="Suspend"
                                onClick={() => { setConfirmText(""); setConfirmAction({ user: u, action: "suspend" }); }}
                                className="p-1.5 text-amber-500 hover:bg-amber-50 rounded-lg transition-colors disabled:opacity-40"
                              >
                                <ShieldOff className="w-4 h-4" />
                              </button>
                            )}
                            <button
                              disabled={busy}
                              title="Promote to Admin"
                              onClick={() => { setConfirmText(""); setConfirmAction({ user: u, action: "promote" }); }}
                              className="p-1.5 text-[#7c83fd] hover:bg-[#f0f1ff] rounded-lg transition-colors disabled:opacity-40"
                            >
                              <ShieldAlert className="w-4 h-4" />
                            </button>
                            <div className="w-px h-3.5 bg-gray-200 mx-0.5" />
                            <button
                              disabled={busy}
                              title="Delete account"
                              onClick={() => setConfirmDelete(u)}
                              className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-40"
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

      {/* ── Delete confirm modal ── */}
      {confirmDelete && (
        <ModalOverlay onClose={() => setConfirmDelete(null)}>
          <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200">
            <div className="px-5 py-4 border-b border-gray-100 flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-red-50 flex items-center justify-center shrink-0">
                <Trash2 className="w-4.5 h-4.5 text-red-500" />
              </div>
              <div>
                <p className="text-[13px] font-bold text-[#1e1b4b]">Delete Account</p>
                <p className="text-[11px] text-gray-400 mt-0.5">This action cannot be undone.</p>
              </div>
            </div>
            <div className="p-5 flex flex-col gap-4">
              <p className="text-[13px] text-gray-600">
                Permanently delete{" "}
                <span className="font-bold text-[#1e1b4b]">{confirmDelete.fullname || confirmDelete.email}</span>?
                This will also remove their restaurant and all associated records.
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() => setConfirmDelete(null)}
                  className="flex-1 py-2.5 rounded-xl border border-gray-200 text-[13px] font-bold text-gray-500 hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={handleDelete}
                  disabled={actionLoading === confirmDelete.id}
                  className="flex-1 py-2.5 bg-red-500 text-white rounded-xl text-[13px] font-bold hover:bg-red-600 transition-colors disabled:opacity-60"
                >
                  {actionLoading === confirmDelete.id ? "Deleting…" : "Delete"}
                </button>
              </div>
            </div>
          </div>
        </ModalOverlay>
      )}

      {/* ── Suspend / Promote confirm modal ── */}
      {confirmAction && (
        <ModalOverlay onClose={() => { setConfirmAction(null); setConfirmText(""); }}>
          <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200">
            <div className="px-5 py-4 border-b border-gray-100 flex items-center gap-3">
              <div className={`w-9 h-9 rounded-xl flex items-center justify-center shrink-0 ${
                confirmAction.action === "suspend" ? "bg-amber-50" : "bg-[#f0f1ff]"
              }`}>
                {confirmAction.action === "suspend"
                  ? <ShieldOff className="w-4.5 h-4.5 text-amber-500" />
                  : <ShieldAlert className="w-4.5 h-4.5 text-[#7c83fd]" />
                }
              </div>
              <div>
                <p className="text-[13px] font-bold text-[#1e1b4b]">
                  {confirmAction.action === "suspend" ? "Suspend Account" : "Promote to Admin"}
                </p>
                <p className="text-[11px] text-gray-400 mt-0.5">Confirm before proceeding.</p>
              </div>
            </div>
            <div className="p-5 flex flex-col gap-4">
              <p className="text-[13px] text-gray-600">
                {confirmAction.action === "suspend"
                  ? "This will block the restaurateur from accessing their account."
                  : "This grants full admin access and removes restaurateur restrictions."}
              </p>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                  Type to confirm
                </label>
                <p className="text-[11px] text-gray-500">
                  Enter <span className="font-bold text-[#1e1b4b]">{getConfirmTarget(confirmAction)}</span>
                </p>
                <input
                  type="text"
                  value={confirmText}
                  onChange={(e) => setConfirmText(e.target.value)}
                  placeholder={getConfirmTarget(confirmAction)}
                  className="w-full px-4 py-2.5 bg-gray-50 rounded-xl border border-gray-200 focus:bg-white focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 text-[13px] text-[#1e1b4b] placeholder:text-gray-300 outline-none transition-all"
                />
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => { setConfirmAction(null); setConfirmText(""); }}
                  className="flex-1 py-2.5 rounded-xl border border-gray-200 text-[13px] font-bold text-gray-500 hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={handleConfirmAction}
                  disabled={
                    actionLoading === confirmAction.user.id ||
                    confirmText.trim().toLowerCase() !== getConfirmTarget(confirmAction).toLowerCase()
                  }
                  className={`flex-1 py-2.5 text-white rounded-xl text-[13px] font-bold transition-colors disabled:opacity-50 ${
                    confirmAction.action === "suspend"
                      ? "bg-amber-500 hover:bg-amber-600"
                      : "bg-[#7c83fd] hover:bg-[#6b72f5]"
                  }`}
                >
                  {actionLoading === confirmAction.user.id ? "Processing…" : "Confirm"}
                </button>
              </div>
            </div>
          </div>
        </ModalOverlay>
      )}
    </div>
  );
};
