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
  Mail,
  User,
  Calendar,
} from "lucide-react";

const CACHE_KEY = "admin:restaurants";
const PAGE_SIZE = 12;

export const AdminRestaurants = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [expandedId, setExpandedId] = useState(null);

  const load = async (force = false) => {
    setLoading(true);
    setError("");
    try {
      let r = !force ? cache.get(CACHE_KEY) : null;
      if (!r) {
        r = await adminApi.getAllRestaurants();
        cache.set(CACHE_KEY, r, 5 * 60 * 1000);
      }
      setRestaurants(Array.isArray(r) ? r : []);
    } catch {
      setError("Failed to load restaurants.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

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
  const paginated = filtered.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE,
  );

  return (
    <div className="flex flex-col flex-1 h-full min-h-0 overflow-y-auto overflow-x-hidden text-[#1e1b4b]">
      <div className="flex flex-col flex-1 min-h-[700px] gap-5 pb-4">
        {/* Header */}
        <div className="shrink-0 flex items-center gap-3">
          <div className="p-2 bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
            <Building2 className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Restaurants</h1>
            <p className="text-xs text-gray-400 font-medium">
              {filtered.length} registered establishments
            </p>
          </div>
        </div>

        {error && (
          <div className="shrink-0 px-4 py-3 bg-red-50 border border-red-100 rounded-xl text-red-600 text-sm font-medium">
            {error}
          </div>
        )}

        {/* Controls */}
        <div className="shrink-0 flex items-center gap-3 bg-white p-2.5 rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.02)]">
          <div className="relative flex-1 max-w-sm">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search by name, owner, location..."
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

        {/* Restaurant Cards */}
        <div className="flex-1 min-h-0">
          {loading ? (
            <div className="space-y-3">
              {[...Array(6)].map((_, i) => (
                <div
                  key={i}
                  className="h-20 bg-white rounded-xl border border-gray-100 animate-pulse"
                />
              ))}
            </div>
          ) : paginated.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full py-20">
              <Building2 className="w-10 h-10 text-gray-200 mb-3" />
              <p className="text-sm text-gray-400 font-bold uppercase tracking-wider">
                No restaurants found.
              </p>
            </div>
          ) : (
            <div className="space-y-2">
              {paginated.map((r, i) => {
                const isExpanded = expandedId === r.id;
                return (
                  <div
                    key={r.id}
                    className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.02)] overflow-hidden"
                  >
                    {/* Row */}
                    <div
                      className={`px-5 py-4 flex items-center gap-4 cursor-pointer transition-colors
                        ${isExpanded ? "bg-indigo-50/30" : "hover:bg-[#f8f9ff]/60"}`}
                      onClick={() =>
                        setExpandedId((prev) => (prev === r.id ? null : r.id))
                      }
                    >
                      {/* Icon */}
                      <div className="w-10 h-10 rounded-xl overflow-hidden bg-indigo-100 flex items-center justify-center text-[#7c83fd] font-black text-sm shrink-0 shadow-sm">
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
                        <p className="text-sm font-bold text-[#1e1b4b] truncate">
                          {r.name}
                        </p>
                        {r.physicalLocation && (
                          <p className="text-xs text-gray-400 flex items-center gap-1 mt-0.5 truncate">
                            <MapPin className="w-3 h-3 shrink-0" />
                            {r.physicalLocation}
                          </p>
                        )}
                      </div>

                      {/* Owner */}
                      <div className="hidden md:block min-w-0 w-48">
                        <p className="text-xs font-bold text-[#1e1b4b] truncate">
                          {r.ownerName}
                        </p>
                        <p className="text-xs text-gray-400 truncate">
                          {r.ownerEmail}
                        </p>
                      </div>

                      {/* Owner status */}
                      <span
                        className={`shrink-0 px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wide border
                        ${
                          r.ownerSuspended
                            ? "bg-red-50 text-red-600 border-red-100"
                            : "bg-emerald-50 text-emerald-700 border-emerald-100"
                        }`}
                      >
                        {r.ownerSuspended ? "Suspended" : "Active"}
                      </span>

                      {/* Registered */}
                      <div className="hidden lg:block text-right shrink-0 w-28">
                        <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wide">
                          Registered
                        </p>
                        <p className="text-xs font-bold text-gray-600">
                          {new Date(r.createdAt).toLocaleDateString("en-PH", {
                            month: "short",
                            day: "numeric",
                            year: "numeric",
                          })}
                        </p>
                      </div>

                      <ChevronDown
                        className={`w-4 h-4 text-gray-400 transition-transform shrink-0 ${isExpanded ? "rotate-180" : ""}`}
                      />
                    </div>

                    {/* Expanded detail — contact info only, no financials */}
                    {isExpanded && (
                      <div className="px-5 py-4 bg-indigo-50/20 border-t border-indigo-100/50">
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                          <div className="flex items-start gap-3 bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                            <div className="p-2 bg-indigo-50 rounded-lg shrink-0">
                              <User className="w-4 h-4 text-[#7c83fd]" />
                            </div>
                            <div className="min-w-0">
                              <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1">
                                Owner
                              </p>
                              <p className="text-sm font-bold text-[#1e1b4b] truncate">
                                {r.ownerName}
                              </p>
                              <p className="text-xs text-gray-400 truncate">
                                {r.ownerEmail}
                              </p>
                            </div>
                          </div>

                          <div className="flex items-start gap-3 bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                            <div className="p-2 bg-amber-50 rounded-lg shrink-0">
                              <MapPin className="w-4 h-4 text-amber-500" />
                            </div>
                            <div className="min-w-0">
                              <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1">
                                Location
                              </p>
                              <p className="text-sm font-bold text-[#1e1b4b]">
                                {r.physicalLocation || (
                                  <span className="text-gray-400 italic font-normal text-xs">
                                    Not specified
                                  </span>
                                )}
                              </p>
                            </div>
                          </div>

                          <div className="flex items-start gap-3 bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                            <div className="p-2 bg-emerald-50 rounded-lg shrink-0">
                              <Calendar className="w-4 h-4 text-emerald-600" />
                            </div>
                            <div className="min-w-0">
                              <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1">
                                Registered
                              </p>
                              <p className="text-sm font-bold text-[#1e1b4b]">
                                {new Date(r.createdAt).toLocaleDateString(
                                  "en-PH",
                                  {
                                    weekday: "short",
                                    year: "numeric",
                                    month: "long",
                                    day: "numeric",
                                  },
                                )}
                              </p>
                            </div>
                          </div>
                        </div>

                        {r.ownerSuspended && (
                          <p className="mt-3 text-xs text-red-500 font-semibold flex items-center gap-1.5 bg-red-50 border border-red-100 rounded-lg px-3 py-2">
                            <span className="w-1.5 h-1.5 rounded-full bg-red-500 shrink-0" />
                            This restaurant's owner account is suspended. The
                            restaurateur cannot log in or access their data.
                          </p>
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
