import { useEffect, useState, useMemo } from "react";
import { adminApi } from "./api/adminApi";
import * as cache from "../../shared/cache/dataCache";
import { API_BASE_URL } from "../../shared/api/axios";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";
import {
  Users,
  Building2,
  UserPlus,
  UserCheck,
  UserX,
  RefreshCw,
  ShieldAlert,
  BarChart2,
  LayoutDashboard,
} from "lucide-react";
import { NotificationBell } from "../notifications/components/NotificationBell";
import { getFormattedDate } from "../analytics/utils/dateUtils";
import { useToast } from "../../shared/components/Toast";

const STATS_CACHE_KEY = "admin:stats";
const USERS_CACHE_KEY = "admin:users";
const CACHE_TTL = 3 * 60 * 1000;

const colorMap = {
  indigo:  { bg: "bg-[#7c83fd]/10", text: "text-[#7c83fd]",   accent: "border-l-[#7c83fd]" },
  emerald: { bg: "bg-emerald-50",   text: "text-emerald-500",  accent: "border-l-emerald-400" },
  red:     { bg: "bg-red-50",       text: "text-red-500",      accent: "border-l-red-400" },
  amber:   { bg: "bg-amber-50",     text: "text-amber-500",    accent: "border-l-amber-400" },
};

const KPICard = ({ title, value, sub, icon: Icon, color = "indigo" }) => {
  const theme = colorMap[color] ?? colorMap.indigo;
  return (
    <div className={`bg-white rounded-xl px-5 py-4 border border-gray-100 border-l-2 ${theme.accent} shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex items-center gap-4`}>
      <div className={`w-10 h-10 rounded-xl ${theme.bg} flex items-center justify-center shrink-0`}>
        {Icon && <Icon className={`w-5 h-5 ${theme.text}`} />}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate mb-0.5">{title}</p>
        <p className={`text-xl font-bold ${theme.text} leading-tight tabular-nums`}>{value}</p>
        {sub && <p className="text-[10px] text-gray-400 font-medium mt-0.5 truncate">{sub}</p>}
      </div>
    </div>
  );
};

const StatPill = ({ label, value, color, bg }) => (
  <div className={`bg-white rounded-xl px-5 py-4 border border-gray-100 border-l-2 ${bg} shadow-[0_2px_12px_rgb(0,0,0,0.04)]`}>
    <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-0.5">{label}</p>
    <p className={`text-xl font-bold ${color} tabular-nums leading-tight`}>{value}</p>
  </div>
);

const CustomTooltip = ({ active, payload }) => {
  if (!active || !payload?.length) return null;
  const d = payload[0];
  return (
    <div className="bg-white border border-gray-100 rounded-xl shadow-lg px-4 py-2.5 text-[12px]">
      <p className="font-bold text-[#1e1b4b]">{d.name}</p>
      <p className="text-gray-500 mt-0.5">{d.value} accounts</p>
    </div>
  );
};

export const AdminHome = () => {
  const { showToast } = useToast();
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async (force = false) => {
    setLoading(true);
    try {
      const s = !force ? cache.get(STATS_CACHE_KEY) : null;
      const u = !force ? cache.get(USERS_CACHE_KEY) : null;

      const [sRes, uRes] = await Promise.all([
        s ? Promise.resolve(s) : adminApi.getStats(),
        u ? Promise.resolve(u) : adminApi.getAllUsers(),
      ]);

      if (!s) cache.set(STATS_CACHE_KEY, sRes, CACHE_TTL);
      if (!u) cache.set(USERS_CACHE_KEY, uRes, CACHE_TTL);

      setStats(sRes);
      setUsers(Array.isArray(uRes) ? uRes : []);
    } catch {
      showToast("error", "Failed to load platform data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const restaurateurs = useMemo(() => users.filter((u) => u.role === "RESTAURATEUR"), [users]);
  const setupComplete = restaurateurs.filter((u) => u.restaurantName).length;
  const setupPending  = restaurateurs.filter((u) => !u.restaurantName).length;
  const setupRate     = restaurateurs.length > 0 ? Math.round((setupComplete / restaurateurs.length) * 100) : 0;
  const activeRate    = restaurateurs.length > 0 ? Math.round(((stats?.activeRestaurateurs ?? 0) / restaurateurs.length) * 100) : 0;

  const pieData = useMemo(() => {
    if (restaurateurs.length === 0) return [];
    return [
      { name: "Active",         value: restaurateurs.filter((t) => !t.suspended && t.restaurantName).length, color: "#10b981" },
      { name: "Suspended",      value: restaurateurs.filter((t) => t.suspended).length,                      color: "#ef4444" },
      { name: "Setup pending",  value: restaurateurs.filter((t) => !t.suspended && !t.restaurantName).length, color: "#f59e0b" },
    ].filter((d) => d.value > 0);
  }, [restaurateurs]);

  const recentRestaurateurs = restaurateurs.slice(0, 8);

  if (loading) {
    return (
      <div className="h-full flex flex-col items-center justify-center gap-3">
        <div className="w-9 h-9 border-[3px] border-indigo-100 border-t-[#7c83fd] rounded-full animate-spin" />
        <p className="text-[11px] font-bold tracking-widest text-gray-400 uppercase">Loading platform data…</p>
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col text-[#1e1b4b] overflow-y-auto lg:overflow-hidden custom-scrollbar">
      <div className="flex flex-col flex-1 min-h-175 gap-3 pb-4">

        {/* ── Header ── */}
        <div className="shrink-0 flex items-center justify-between gap-3 mb-1 flex-wrap">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
              <LayoutDashboard className="w-4.5 h-4.5 text-white" />
            </div>
            <div className="min-w-0">
              <div className="flex items-center gap-2 flex-wrap">
                <h1 className="text-lg font-bold tracking-tight truncate">Platform Overview</h1>
                <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-[#7c83fd]/10 text-[#7c83fd] text-[10px] font-bold rounded-full border border-[#7c83fd]/20 uppercase tracking-wider shrink-0">
                  <ShieldAlert className="w-2.5 h-2.5" /> Admin
                </span>
              </div>
              <p className="text-[11px] font-medium text-gray-400 mt-0.5">{getFormattedDate()}</p>
            </div>
          </div>

          {/* Action pill */}
          <div className="flex items-center bg-white rounded-xl border border-gray-200 shrink-0">
            <button
              onClick={() => load(true)}
              className="p-2.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] rounded-l-xl transition-colors"
              title="Refresh"
            >
              <RefreshCw className="w-4 h-4" />
            </button>
            <div className="w-px h-5 bg-gray-100" />
            <div className="px-1">
              <NotificationBell />
            </div>
          </div>
        </div>

        {/* ── New signups banner ── */}
        {(stats?.newRestaurateursThisMonth ?? 0) > 0 && (
          <div className="shrink-0 flex items-center gap-3 px-4 py-2.5 bg-[#f0f1ff] border border-[#d6d9ff] rounded-xl animate-in fade-in duration-200">
            <div className="w-7 h-7 rounded-xl bg-[#7c83fd]/20 flex items-center justify-center shrink-0">
              <UserPlus className="w-3.5 h-3.5 text-[#7c83fd]" />
            </div>
            <p className="text-[13px] font-semibold text-[#1e1b4b]">
              <span className="font-black text-[#7c83fd]">{stats.newRestaurateursThisMonth}</span>{" "}
              new restaurateur{stats.newRestaurateursThisMonth !== 1 ? "s" : ""} registered this month.
            </p>
          </div>
        )}

        {/* ── KPI Row ── */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 shrink-0">
          <KPICard icon={Users}     title="Total Restaurateurs" value={stats?.totalRestaurateurs ?? 0}   sub={`${stats?.newRestaurateursThisMonth ?? 0} joined this month`} color="indigo"  />
          <KPICard icon={Building2} title="Restaurants"          value={stats?.totalRestaurants ?? 0}    sub={`${setupRate}% setup complete`}                               color="amber"   />
          <KPICard icon={UserCheck} title="Active"               value={stats?.activeRestaurateurs ?? 0} sub="Accounts in good standing"                                    color="emerald" />
          <KPICard icon={UserX}     title="Suspended"            value={stats?.suspendedRestaurateurs ?? 0}
            sub={stats?.suspendedRestaurateurs > 0 ? "Requires attention" : "None suspended"}
            color={stats?.suspendedRestaurateurs > 0 ? "red" : "emerald"}
          />
        </div>

        {/* ── Middle Row ── */}
        <div className="flex-[1.5] min-h-70 grid grid-cols-1 lg:grid-cols-3 gap-3">

          {/* Account Status Donut */}
          <div className="bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex flex-col min-h-0">
            <div className="px-5 py-3.5 border-b border-gray-100 shrink-0">
              <p className="text-[13px] font-bold text-[#1e1b4b]">Account Status</p>
              <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mt-0.5">Restaurateur breakdown</p>
            </div>
            <div className="flex-1 p-5 min-h-0 flex flex-col gap-3">
              <div className="flex-1 min-h-0">
                {pieData.length === 0 ? (
                  <div className="w-full h-full flex flex-col items-center justify-center bg-gray-50/50 rounded-xl border border-dashed border-gray-200 p-6">
                    <div className="w-10 h-10 bg-white rounded-xl shadow-sm border border-gray-100 flex items-center justify-center mb-2">
                      <BarChart2 className="w-5 h-5 text-gray-300" />
                    </div>
                    <p className="text-[10px] text-gray-400 font-bold text-center uppercase tracking-widest">No restaurateurs yet</p>
                  </div>
                ) : (
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie data={pieData} innerRadius="55%" outerRadius="75%" paddingAngle={4} dataKey="value" stroke="none">
                        {pieData.map((entry, i) => (
                          <Cell key={i} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip content={<CustomTooltip />} />
                    </PieChart>
                  </ResponsiveContainer>
                )}
              </div>

              {/* Legend */}
              <div className="flex flex-col gap-1.5 shrink-0">
                {[
                  { label: "Active",         count: stats?.activeRestaurateurs ?? 0,   color: "#10b981", chip: "bg-emerald-50 border-emerald-100 text-emerald-700" },
                  { label: "Suspended",      count: stats?.suspendedRestaurateurs ?? 0, color: "#ef4444", chip: "bg-red-50 border-red-100 text-red-600" },
                  { label: "Setup pending",  count: setupPending,                       color: "#f59e0b", chip: "bg-amber-50 border-amber-100 text-amber-600" },
                  { label: "Setup complete", count: setupComplete,                      color: "#7c83fd", chip: "bg-[#f0f1ff] border-[#d6d9ff] text-[#7c83fd]" },
                ].map(({ label, count, color, chip }) => (
                  <div key={label} className="flex items-center justify-between px-3 py-2 rounded-xl border bg-gray-50/50 border-gray-100">
                    <div className="flex items-center gap-2">
                      <div className="w-2 h-2 rounded-full shrink-0" style={{ backgroundColor: color }} />
                      <span className="text-[10px] font-bold text-gray-500 uppercase tracking-wider">{label}</span>
                    </div>
                    <span className={`text-[10px] font-black px-2 py-0.5 rounded-full border ${chip}`}>{count}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Recent Signups table */}
          <div className="lg:col-span-2 bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex flex-col min-h-0 overflow-hidden">
            <div className="px-5 py-3.5 border-b border-gray-100 flex items-center justify-between shrink-0">
              <div>
                <p className="text-[13px] font-bold text-[#1e1b4b]">Recent Signups</p>
                <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mt-0.5">
                  Last {recentRestaurateurs.length} registered
                </p>
              </div>
              <span className="text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] border border-[#d6d9ff] rounded-full px-2.5 py-1">
                {restaurateurs.length} total
              </span>
            </div>
            <div className="flex-1 overflow-auto min-h-0">
              {recentRestaurateurs.length === 0 ? (
                <div className="h-full flex flex-col items-center justify-center gap-2 py-10">
                  <div className="w-12 h-12 rounded-2xl bg-indigo-50 flex items-center justify-center">
                    <Users className="w-6 h-6 text-[#7c83fd]/40" />
                  </div>
                  <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest">No restaurateurs yet</p>
                </div>
              ) : (
                <table className="w-full text-left border-collapse whitespace-nowrap">
                  <thead className="sticky top-0 z-10 bg-gray-50">
                    <tr>
                      {["Restaurateur", "Restaurant", "Joined", "Status"].map((h) => (
                        <th key={h} className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {recentRestaurateurs.map((u) => (
                      <tr key={u.id} className="hover:bg-[#f8f9ff] transition-colors border-b border-gray-50 last:border-0">
                        <td className="px-5 py-3">
                          <div className="flex items-center gap-3">
                            <div className="w-8 h-8 rounded-xl bg-[#7c83fd]/10 overflow-hidden shrink-0">
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
                              <p className="text-[13px] font-bold text-[#1e1b4b]">{u.fullname || "—"}</p>
                              <p className="text-[10px] text-gray-400 font-medium">{u.email}</p>
                            </div>
                          </div>
                        </td>
                        <td className="px-5 py-3 text-[13px] font-medium text-gray-600">
                          {u.restaurantName || (
                            <span className="inline-flex items-center text-[10px] font-bold text-amber-600 bg-amber-50 border border-amber-100 rounded-full px-2.5 py-0.5 uppercase tracking-wider">
                              Setup pending
                            </span>
                          )}
                        </td>
                        <td className="px-5 py-3 text-[12px] text-gray-400 font-medium">
                          {new Date(u.createdAt).toLocaleDateString("en-PH", { month: "short", day: "numeric", year: "numeric" })}
                        </td>
                        <td className="px-5 py-3">
                          <span className={`inline-flex items-center text-[10px] font-bold rounded-full px-2.5 py-0.5 border uppercase tracking-wider ${
                            u.suspended
                              ? "bg-red-50 text-red-600 border-red-100"
                              : "bg-emerald-50 text-emerald-700 border-emerald-100"
                          }`}>
                            {u.suspended ? "Suspended" : "Active"}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </div>

        {/* ── Bottom: Platform Health ── */}
        <div className="shrink-0 grid grid-cols-2 lg:grid-cols-3 gap-3">
          <StatPill
            label="Setup Rate"
            value={`${setupRate}%`}
            color={setupRate >= 80 ? "text-emerald-500" : setupRate >= 50 ? "text-amber-500" : "text-red-500"}
            bg={setupRate >= 80 ? "border-l-emerald-400" : setupRate >= 50 ? "border-l-amber-400" : "border-l-red-400"}
          />
          <StatPill
            label="Active Rate"
            value={restaurateurs.length > 0 ? `${activeRate}%` : "—"}
            color="text-emerald-500"
            bg="border-l-emerald-400"
          />
          <StatPill
            label="New This Month"
            value={stats?.newRestaurateursThisMonth ?? 0}
            color="text-[#7c83fd]"
            bg="border-l-[#7c83fd]"
          />
        </div>

      </div>
    </div>
  );
};
