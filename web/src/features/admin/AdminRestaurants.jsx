import { useEffect, useState, useMemo } from "react";
import { adminApi } from "./api/adminApi";
import { API_BASE_URL } from "../../shared/api/axios";
import * as cache from "../../shared/cache/dataCache";
import {
  Search,
  X,
  Building2,
  ChevronLeft,
  ChevronRight,
  ChevronDown,
  MapPin,
  User,
  Calendar,
  RefreshCw,
} from "lucide-react";
import { useToast } from "../../shared/components/Toast";

const CACHE_KEY = "admin:restaurants";
const PAGE_SIZE = 12;

const DetailChip = ({ icon: Icon, iconBg, iconColor, label, value }) => (
  <div className="flex items-start gap-3 bg-white rounded-xl p-4 border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)]">
    <div className={`w-8 h-8 rounded-xl ${iconBg} flex items-center justify-center shrink-0`}>
      <Icon className={`w-4 h-4 ${iconColor}`} />
    </div>
    <div className="min-w-0">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">{label}</p>
      <p className="text-[13px] font-bold text-[#1e1b4b] truncate">{value}</p>
    </div>
  </div>
);

export const AdminRestaurants = () => {
  const { showToast } = useToast();
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [expandedId, setExpandedId] = useState(null);

  const load = async (force = false) => {
    setLoading(true);
    try {
      let r = !force ? cache.get(CACHE_KEY) : null;
      if (!r) {
        r = await adminApi.getAllRestaurants();
        cache.set(CACHE_KEY, r, 5 * 60 * 1000);
      }
      setRestaurants(Array.isArray(r) ? r : []);
    } catch {
      showToast("error", "Failed to load restaurants.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);
  useEffect(() => { setCurrentPage(1); }, [searchTerm]);

  const filtered = useMemo(() => {
    if (!searchTerm) return restaurants;
    const s = searchTerm.toLowerCase();
    return restaurants.filter(
      (r) =>
        (r.name || "").toLowerCase().includes(s) ||
        (r.ownerName || "").toLowerCase().includes(s) ||
        (r.ownerEmail || "").toLowerCase().includes(s) ||
        (r.physicalLocation || "").toLowerCase().includes(s),
    );
  }, [restaurants, searchTerm]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const paginated = filtered.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

  return (
    <div className="flex flex-col flex-1 h-full min-h-0 overflow-y-auto overflow-x-hidden text-[#1e1b4b]">
      <div className="flex flex-col flex-1 min-h-175 gap-3 pb-4">

        {/* ── Header ── */}
        <div className="shrink-0 flex items-center justify-between gap-3 flex-wrap">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
              <Building2 className="w-4.5 h-4.5 text-white" />
            </div>
            <div className="min-w-0">
              <h1 className="text-lg font-bold tracking-tight truncate">Restaurants</h1>
              <p className="text-[11px] font-medium text-gray-400 mt-0.5">
                {filtered.length} registered establishments
              </p>
            </div>
          </div>
          <button
            onClick={() => load(true)}
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
              placeholder="Search by name, owner, location…"
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

        {/* ── Cards ── */}
        <div className="flex-1 min-h-0">
          {loading ? (
            <div className="flex flex-col gap-2">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="h-18 bg-white rounded-xl border border-gray-100 animate-pulse" />
              ))}
            </div>
          ) : paginated.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full py-20 gap-3">
              <div className="w-14 h-14 rounded-2xl bg-indigo-50 flex items-center justify-center">
                <Building2 className="w-7 h-7 text-[#7c83fd]/40" />
              </div>
              <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest">
                No restaurants found
              </p>
            </div>
          ) : (
            <div className="flex flex-col gap-2">
              {paginated.map((r) => {
                const isExpanded = expandedId === r.id;
                return (
                  <div
                    key={r.id}
                    className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] overflow-hidden"
                  >
                    {/* Row */}
                    <div
                      className={`px-5 py-3.5 flex items-center gap-4 cursor-pointer transition-colors ${
                        isExpanded ? "bg-[#f8f9ff]" : "hover:bg-[#f8f9ff]"
                      }`}
                      onClick={() => setExpandedId((prev) => (prev === r.id ? null : r.id))}
                    >
                      {/* Logo */}
                      <div className="w-10 h-10 rounded-xl overflow-hidden bg-[#7c83fd]/10 shrink-0">
                        <img
                          src={
                            r.logoFileId
                              ? `${API_BASE_URL}/files/${r.logoFileId}`
                              : `https://ui-avatars.com/api/?name=${encodeURIComponent(r.name || "R")}&background=e0e7ff&color=4338ca&bold=true`
                          }
                          alt="logo"
                          className="w-full h-full object-cover"
                          onError={(e) => {
                            e.currentTarget.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(r.name || "R")}&background=e0e7ff&color=4338ca&bold=true`;
                          }}
                        />
                      </div>

                      {/* Name + Location */}
                      <div className="flex-1 min-w-0">
                        <p className="text-[13px] font-bold text-[#1e1b4b] truncate">{r.name}</p>
                        {r.physicalLocation && (
                          <p className="text-[11px] text-gray-400 flex items-center gap-1 mt-0.5 truncate">
                            <MapPin className="w-3 h-3 shrink-0" />
                            {r.physicalLocation}
                          </p>
                        )}
                      </div>

                      {/* Owner */}
                      <div className="hidden md:block min-w-0 w-44 shrink-0">
                        <p className="text-[12px] font-bold text-[#1e1b4b] truncate">{r.ownerName}</p>
                        <p className="text-[11px] text-gray-400 truncate">{r.ownerEmail}</p>
                      </div>

                      {/* Status badge */}
                      <span className={`shrink-0 inline-flex items-center text-[10px] font-bold rounded-full px-2.5 py-0.5 border uppercase tracking-wider ${
                        r.ownerSuspended
                          ? "bg-red-50 text-red-600 border-red-100"
                          : "bg-emerald-50 text-emerald-700 border-emerald-100"
                      }`}>
                        {r.ownerSuspended ? "Suspended" : "Active"}
                      </span>

                      {/* Date */}
                      <div className="hidden lg:block text-right shrink-0 w-24">
                        <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Registered</p>
                        <p className="text-[11px] font-bold text-gray-600 mt-0.5">
                          {new Date(r.createdAt).toLocaleDateString("en-PH", { month: "short", day: "numeric", year: "numeric" })}
                        </p>
                      </div>

                      <ChevronDown className={`w-4 h-4 text-gray-400 transition-transform shrink-0 ${isExpanded ? "rotate-180" : ""}`} />
                    </div>

                    {/* Expanded detail */}
                    {isExpanded && (
                      <div className="px-5 pb-5 pt-3 border-t border-gray-100 bg-gray-50/40">
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                          <DetailChip
                            icon={User}
                            iconBg="bg-[#7c83fd]/10"
                            iconColor="text-[#7c83fd]"
                            label="Owner"
                            value={`${r.ownerName} · ${r.ownerEmail}`}
                          />
                          <DetailChip
                            icon={MapPin}
                            iconBg="bg-amber-50"
                            iconColor="text-amber-500"
                            label="Location"
                            value={r.physicalLocation || "—"}
                          />
                          <DetailChip
                            icon={Calendar}
                            iconBg="bg-emerald-50"
                            iconColor="text-emerald-500"
                            label="Registered"
                            value={new Date(r.createdAt).toLocaleDateString("en-PH", {
                              weekday: "short", year: "numeric", month: "long", day: "numeric",
                            })}
                          />
                        </div>
                        {r.ownerSuspended && (
                          <div className="mt-3 flex items-center gap-2 px-4 py-2.5 bg-red-50 border border-red-100 rounded-xl text-[12px] font-semibold text-red-600">
                            <AlertTriangle className="w-3.5 h-3.5 shrink-0" />
                            This restaurant's owner is suspended and cannot access their data.
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
