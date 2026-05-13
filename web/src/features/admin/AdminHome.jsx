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
  AlertCircle,
  BarChart2,
} from "lucide-react";
import { NotificationBell } from "../notifications/components/NotificationBell";

const STATS_CACHE_KEY = "admin:stats";
const USERS_CACHE_KEY = "admin:users";
const CACHE_TTL = 3 * 60 * 1000;

const KPICard = ({ title, value, sub, icon: Icon, color = "indigo" }) => {
  const theme = {
    indigo: { bg: "bg-indigo-50", text: "text-[#7c83fd]" },
    emerald: { bg: "bg-emerald-50", text: "text-emerald-600" },
    red: { bg: "bg-red-50", text: "text-red-500" },
    amber: { bg: "bg-amber-50", text: "text-amber-500" },
  }[color];

  return (
    <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
      <div
        className={`absolute -right-4 -top-4 ${theme.bg} opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none rounded-full p-4`}
      >
        {Icon && <Icon className="w-20 h-20" />}
      </div>
      <div className="relative z-10">
        <div className="flex justify-between items-start mb-3">
          <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
            {title}
          </p>
          <div className={`p-1.5 ${theme.bg} rounded-lg`}>
            <Icon className={`w-4 h-4 ${theme.text}`} />
          </div>
        </div>
        <p className={`text-3xl font-black ${theme.text} leading-none`}>
          {value}
        </p>
        {sub && (
          <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mt-2">
            {sub}
          </p>
        )}
      </div>
    </div>
  );
};

const EmptyChart = ({ message }) => (
  <div className="w-full h-full flex flex-col items-center justify-center bg-gray-50/50 rounded-xl border border-dashed border-gray-200 p-6">
    <div className="w-12 h-12 bg-white rounded-xl shadow-sm border border-gray-100 flex items-center justify-center mb-3">
      <BarChart2 className="w-6 h-6 text-gray-300" />
    </div>
    <p className="text-xs text-gray-400 font-bold text-center uppercase tracking-wider leading-relaxed">
      {message}
    </p>
  </div>
);

export const AdminHome = () => {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async (force = false) => {
    setLoading(true);
    setError("");
    try {
      let s = !force ? cache.get(STATS_CACHE_KEY) : null;
      let u = !force ? cache.get(USERS_CACHE_KEY) : null;

      const [sRes, uRes] = await Promise.all([
        s ? Promise.resolve(s) : adminApi.getStats(),
        u ? Promise.resolve(u) : adminApi.getAllUsers(),
      ]);

      if (!s) cache.set(STATS_CACHE_KEY, sRes, CACHE_TTL);
      if (!u) cache.set(USERS_CACHE_KEY, uRes, CACHE_TTL);

      setStats(sRes);
      setUsers(Array.isArray(uRes) ? uRes : []);
    } catch {
      setError("Failed to load platform data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const tenants = useMemo(
    () => users.filter((u) => u.role === "TENANT"),
    [users],
  );

  const setupComplete = tenants.filter((u) => u.restaurantName).length;
  const setupPending = tenants.filter((u) => !u.restaurantName).length;
  const setupRate =
    tenants.length > 0 ? Math.round((setupComplete / tenants.length) * 100) : 0;

  const pieData = useMemo(() => {
    if (tenants.length === 0) return [];

    const activeCount = tenants.filter(
      (t) => !t.suspended && t.restaurantName,
    ).length;
    const suspendedCount = tenants.filter((t) => t.suspended).length;
    const pendingCount = tenants.filter(
      (t) => !t.suspended && !t.restaurantName,
    ).length;

    return [
      { name: "Active", value: activeCount, color: "#10b981" },
      { name: "Suspended", value: suspendedCount, color: "#ef4444" },
      { name: "Setup pending", value: pendingCount, color: "#f59e0b" },
    ].filter((d) => d.value > 0);
  }, [tenants]);

  const recentTenants = tenants.slice(0, 8);

  const getFormattedDate = () =>
    new Date().toLocaleDateString("en-GB", {
      weekday: "long",
      day: "numeric",
      month: "long",
    });

  if (loading) {
    return (
      <div className="h-full flex flex-col items-center justify-center space-y-4 animate-in fade-in duration-300">
        <div className="w-10 h-10 border-4 border-indigo-100 border-t-[#7c83fd] rounded-full animate-spin" />
        <p className="text-sm font-bold tracking-wider text-gray-400 uppercase">
          Loading Platform Data
        </p>
      </div>
    );
  }

  return (
    <div className="flex flex-col flex-1 h-full min-h-0 overflow-y-auto overflow-x-hidden text-[#1e1b4b]">
      <div className="flex flex-col flex-1 min-h-[700px] gap-5 pb-4">
        {/* ── Header ─────────────────────────────────────────────────────────── */}
        <div className="shrink-0 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3 mb-1">
              <h1 className="text-2xl font-bold tracking-tight">
                Platform Overview
              </h1>
              <span className="flex items-center gap-1.5 text-[10px] font-bold text-[#7c83fd] bg-indigo-50 border border-indigo-100 px-2 py-0.5 rounded-md uppercase tracking-wider">
                <ShieldAlert className="w-3 h-3" /> Admin
              </span>
            </div>
            <p className="text-sm font-medium text-gray-500">
              {getFormattedDate()}
            </p>
          </div>
          <div className="flex items-center bg-white p-1 rounded-xl shadow-sm border border-gray-100">
            <button
              onClick={() => load(true)}
              className="p-2 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-lg transition-colors flex items-center gap-2 px-3 text-xs font-semibold"
            >
              <RefreshCw className="w-4 h-4" /> Refresh
            </button>
            <div className="w-px h-4 bg-gray-200 mx-1" />
            <NotificationBell />
          </div>
        </div>

        {/* ── Error ──────────────────────────────────────────────────────────── */}
        {error && (
          <div className="shrink-0 flex items-center justify-between bg-red-50 border border-red-100 rounded-xl px-5 py-3 shadow-sm">
            <div className="flex items-center gap-3 text-sm font-medium text-red-600">
              <AlertCircle className="w-4 h-4 shrink-0" />
              {error}
            </div>
            <button
              onClick={() => load(true)}
              className="text-xs font-bold text-red-700 bg-red-100 hover:bg-red-200 px-3 py-1.5 rounded-lg transition-colors"
            >
              Retry
            </button>
          </div>
        )}

        {/* ── KPI Row ────────────────────────────────────────────────────────── */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-5 shrink-0">
          <KPICard
            icon={Users}
            title="Total Tenants"
            value={stats?.totalTenants ?? 0}
            sub={`${stats?.newTenantsThisMonth ?? 0} joined this month`}
            color="indigo"
          />
          <KPICard
            icon={Building2}
            title="Restaurants"
            value={stats?.totalRestaurants ?? 0}
            sub={`${setupRate}% setup complete`}
            color="amber"
          />
          <KPICard
            icon={UserCheck}
            title="Active Tenants"
            value={stats?.activeTenants ?? 0}
            sub="Accounts in good standing"
            color="emerald"
          />
          <KPICard
            icon={UserX}
            title="Suspended"
            value={stats?.suspendedTenants ?? 0}
            sub={
              stats?.suspendedTenants > 0
                ? "Requires attention"
                : "None suspended"
            }
            color={stats?.suspendedTenants > 0 ? "red" : "emerald"}
          />
        </div>

        {/* New signups callout */}
        {(stats?.newTenantsThisMonth ?? 0) > 0 && (
          <div className="bg-indigo-50/50 border border-indigo-100 rounded-xl px-5 py-3 text-sm text-[#1e1b4b] font-medium flex items-center gap-3 shrink-0 shadow-sm">
            <div className="p-1.5 bg-indigo-100 text-[#7c83fd] rounded-md">
              <UserPlus className="w-4 h-4" />
            </div>
            <p>
              <span className="font-black text-[#7c83fd]">
                {stats.newTenantsThisMonth}
              </span>{" "}
              new tenant{stats.newTenantsThisMonth !== 1 ? "s" : ""} registered
              this month.
            </p>
          </div>
        )}

        {/* ── Middle Row ─────────────────────────────────────────────────────── */}
        <div className="flex-[1.3] grid grid-cols-1 lg:grid-cols-3 gap-5 min-h-[280px]">
          {/* Account Status Donut */}
          <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0">
            <div className="px-6 py-4 border-b border-gray-50 shrink-0">
              <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider">
                Account Status
              </h3>
              <p className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-0.5">
                Tenant breakdown
              </p>
            </div>
            <div className="flex-1 p-5 min-h-0 flex flex-col">
              <div className="flex-1 min-h-0">
                {pieData.length === 0 ? (
                  <EmptyChart message="No tenants registered yet." />
                ) : (
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={pieData}
                        innerRadius="58%"
                        outerRadius="78%"
                        paddingAngle={4}
                        dataKey="value"
                        stroke="none"
                      >
                        {pieData.map((entry, i) => (
                          <Cell key={i} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip
                        contentStyle={{
                          borderRadius: "12px",
                          border: "1px solid #f1f5f9",
                          boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)",
                          fontSize: 12,
                        }}
                        formatter={(value, name) => [`${value} accounts`, name]}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                )}
              </div>

              {/* Legend */}
              <div className="space-y-2 pt-4 shrink-0">
                {[
                  {
                    label: "Active",
                    count: stats?.activeTenants ?? 0,
                    color: "#10b981",
                    bg: "bg-emerald-50 border-emerald-100",
                  },
                  {
                    label: "Suspended",
                    count: stats?.suspendedTenants ?? 0,
                    color: "#ef4444",
                    bg: "bg-red-50 border-red-100",
                  },
                  {
                    label: "Setup pending",
                    count: setupPending,
                    color: "#f59e0b",
                    bg: "bg-amber-50 border-amber-100",
                  },
                  {
                    label: "Setup complete",
                    count: setupComplete,
                    color: "#7c83fd",
                    bg: "bg-indigo-50 border-indigo-100",
                  },
                ].map(({ label, count, color, bg }) => (
                  <div
                    key={label}
                    className={`flex items-center justify-between px-3 py-2 rounded-lg border ${bg}`}
                  >
                    <div className="flex items-center gap-2">
                      <div
                        className="w-2 h-2 rounded-full shrink-0"
                        style={{ backgroundColor: color }}
                      />
                      <span className="text-[10px] font-bold text-gray-600 uppercase tracking-wide">
                        {label}
                      </span>
                    </div>
                    <span className="text-sm font-black" style={{ color }}>
                      {count}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Recent Signups */}
          <div className="lg:col-span-2 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0 overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-50 flex items-center justify-between shrink-0">
              <div>
                <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider">
                  Recent Signups
                </h3>
                <p className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-0.5">
                  Last {recentTenants.length} registered tenants
                </p>
              </div>
              <span className="text-xs font-bold text-[#7c83fd] bg-indigo-50 border border-indigo-100 px-2.5 py-1 rounded-lg">
                {tenants.length} total
              </span>
            </div>
            <div className="flex-1 overflow-auto min-h-0">
              {recentTenants.length === 0 ? (
                <div className="h-full flex items-center justify-center">
                  <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">
                    No tenants yet.
                  </p>
                </div>
              ) : (
                <table className="w-full text-left border-collapse whitespace-nowrap">
                  <thead className="bg-gray-50/80 sticky top-0 z-10">
                    <tr>
                      {["Tenant", "Restaurant", "Joined", "Status"].map((h) => (
                        <th
                          key={h}
                          className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-wider border-b border-gray-100"
                        >
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-50">
                    {recentTenants.map((u, i) => (
                      <tr
                        key={u.id}
                        className={`transition-colors hover:bg-[#f8f9ff]/60 ${i % 2 === 0 ? "bg-white" : "bg-[#f8f9ff]/20"}`}
                      >
                        <td className="px-5 py-3.5">
                          <div className="flex items-center gap-3">
                            <div className="w-8 h-8 rounded-full bg-indigo-100 overflow-hidden flex items-center justify-center text-[#7c83fd] font-black text-xs shrink-0">
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
                              <p className="text-[10px] text-gray-400 font-medium">
                                {u.email}
                              </p>
                            </div>
                          </div>
                        </td>
                        <td className="px-5 py-3.5 text-sm font-medium text-gray-600">
                          {u.restaurantName || (
                            <span className="text-[10px] text-amber-500 font-bold uppercase tracking-wide bg-amber-50 border border-amber-100 px-2 py-0.5 rounded-md">
                              Setup pending
                            </span>
                          )}
                        </td>
                        <td className="px-5 py-3.5 text-xs text-gray-500 font-medium">
                          {new Date(u.createdAt).toLocaleDateString("en-PH", {
                            month: "short",
                            day: "numeric",
                            year: "numeric",
                          })}
                        </td>
                        <td className="px-5 py-3.5">
                          <span
                            className={`px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wide border
                            ${
                              u.suspended
                                ? "bg-red-50 text-red-600 border-red-100"
                                : "bg-emerald-50 text-emerald-700 border-emerald-100"
                            }`}
                          >
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

        {/* ── Bottom: Platform Health Summary ────────────────────────────────── */}
        <div className="shrink-0 grid grid-cols-2 lg:grid-cols-4 gap-5">
          {[
            {
              label: "Setup Rate",
              value: `${setupRate}%`,
              sub: `${setupComplete} of ${tenants.length} tenants`,
              color:
                setupRate >= 80
                  ? "text-emerald-600"
                  : setupRate >= 50
                    ? "text-amber-500"
                    : "text-red-500",
              bg:
                setupRate >= 80
                  ? "bg-emerald-50 border-emerald-100"
                  : setupRate >= 50
                    ? "bg-amber-50 border-amber-100"
                    : "bg-red-50 border-red-100",
            },
            {
              label: "Active Rate",
              value:
                tenants.length > 0
                  ? `${Math.round(((stats?.activeTenants ?? 0) / tenants.length) * 100)}%`
                  : "—",
              sub: `${stats?.activeTenants ?? 0} active accounts`,
              color: "text-emerald-600",
              bg: "bg-emerald-50 border-emerald-100",
            },
            {
              label: "Avg. per Restaurant",
              value:
                stats?.totalRestaurants > 0
                  ? `${Math.round((tenants.length / stats.totalRestaurants) * 10) / 10} users`
                  : "—",
              sub: "Tenants per establishment",
              color: "text-[#7c83fd]",
              bg: "bg-indigo-50 border-indigo-100",
            },
            {
              label: "New This Month",
              value: stats?.newTenantsThisMonth ?? 0,
              sub: "Tenant registrations",
              color: "text-[#7c83fd]",
              bg: "bg-indigo-50 border-indigo-100",
            },
          ].map(({ label, value, sub, color, bg }) => (
            <div
              key={label}
              className={`rounded-xl px-5 py-4 border ${bg} flex flex-col`}
            >
              <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-2">
                {label}
              </p>
              <p className={`text-2xl font-black ${color} leading-none`}>
                {value}
              </p>
              <p className="text-[10px] text-gray-400 font-medium mt-1.5">
                {sub}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
