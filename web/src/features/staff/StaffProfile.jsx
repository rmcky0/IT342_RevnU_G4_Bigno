import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { staffApi } from "./api/staffApi";
import { StaffProfileSkeleton } from "./components/StaffProfileSkeleton";
import { StaffSalaryHistoryTable } from "./components/StaffSalaryHistoryTable";

import {
  ChevronLeft,
  Wallet,
  Briefcase,
  User,
  TrendingUp,
  CalendarDays,
  AlertTriangle,
} from "lucide-react";

export const StaffProfile = () => {
  const { staffId } = useParams();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const res = await staffApi.getStaffById(staffId);
        setProfile(res?.data ?? res);
      } catch (err) {
        setError("Failed to load staff profile.");
        console.error("Failed to load profile:", err);
      } finally {
        setTimeout(() => setLoading(false), 300);
      }
    };
    if (staffId) loadProfile();
  }, [staffId]);

  if (error) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center h-full gap-4">
        <div className="w-14 h-14 bg-red-50 rounded-2xl flex items-center justify-center">
          <AlertTriangle className="w-7 h-7 text-red-400" />
        </div>
        <div className="text-center">
          <h3 className="text-[15px] font-bold text-[#1e1b4b] mb-1">Profile Error</h3>
          <p className="text-[13px] text-gray-400">{error}</p>
        </div>
        <button
          onClick={() => navigate("/staff")}
          className="px-4 py-2 bg-[#7c83fd] text-white rounded-xl font-bold text-[13px] hover:bg-[#6b72f5] transition-colors"
        >
          Return to Directory
        </button>
      </div>
    );
  }

  if (loading || !profile) return <StaffProfileSkeleton />;

  const initials = profile.fullname
    ? profile.fullname.split(" ").slice(0, 2).map((n) => n[0]).join("").toUpperCase()
    : "?";

  const totalPaid = (profile.salaryHistory || []).reduce(
    (sum, record) => sum + (parseFloat(record.amount) || 0),
    0,
  );

  const fmt = (n) => n.toLocaleString("en-PH", { minimumFractionDigits: 2 });

  return (
    <div className="flex flex-col flex-1 h-full max-h-full gap-3 overflow-hidden text-[#1e1b4b]">

      {/* ── Header ── */}
      <div className="shrink-0 flex items-center gap-2.5">
        <button
          onClick={() => navigate("/staff")}
          className="p-2 bg-white rounded-xl border border-gray-200 text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff] shadow-sm transition-colors shrink-0"
          title="Back to Directory"
        >
          <ChevronLeft className="w-4.5 h-4.5" />
        </button>
        <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
          <User className="w-4.5 h-4.5 text-white" />
        </div>
        <div className="min-w-0">
          <h2 className="text-lg font-bold tracking-tight truncate">Staff Profile</h2>
          <p className="text-[11px] font-medium text-gray-400 mt-0.5 truncate">{profile.fullname}</p>
        </div>
      </div>

      {/* ── Profile Card ── */}
      <div className="shrink-0 bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] p-5 flex flex-col sm:flex-row items-start sm:items-center gap-5">
        {/* Avatar */}
        <div className="w-16 h-16 rounded-xl bg-[#7c83fd]/10 border border-[#7c83fd]/20 flex items-center justify-center text-2xl font-black text-[#7c83fd] shrink-0 select-none">
          {initials}
        </div>

        {/* Name + chips */}
        <div className="flex-1 min-w-0">
          <h3 className="text-[17px] font-bold text-[#1e1b4b] truncate mb-2">
            {profile.fullname}
          </h3>
          <div className="flex flex-wrap gap-2">
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-gray-50 border border-gray-200 rounded-full text-[11px] font-bold text-gray-500 uppercase tracking-wider">
              <Briefcase className="w-3 h-3 text-gray-400" />
              {profile.position || "No position"}
            </span>
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-[#f0f1ff] border border-[#d6d9ff] rounded-full text-[11px] font-bold text-[#7c83fd] uppercase tracking-wider">
              <Wallet className="w-3 h-3" />
              ₱{fmt(parseFloat(profile.salaryRate || 0))} / mo
            </span>
          </div>
        </div>

        {/* Lifetime paid KPI */}
        <div className="sm:pl-5 sm:border-l border-gray-100 pt-4 sm:pt-0 border-t sm:border-t-0 w-full sm:w-auto shrink-0">
          <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest flex items-center gap-1.5 mb-1">
            <TrendingUp className="w-3 h-3 text-emerald-500" />
            Lifetime Paid
          </p>
          <p className="text-2xl font-black text-[#1e1b4b] tabular-nums leading-tight">
            <span className="text-base font-medium text-gray-400 mr-0.5">₱</span>
            {fmt(totalPaid)}
          </p>
          <p className="text-[10px] text-gray-400 font-medium mt-0.5">
            across {profile.salaryHistory?.length || 0} payouts
          </p>
        </div>
      </div>

      {/* ── Payout History header ── */}
      <div className="shrink-0 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center">
            <CalendarDays className="w-3.5 h-3.5 text-[#7c83fd]" />
          </div>
          <h3 className="text-[13px] font-bold text-[#1e1b4b]">Payout History</h3>
        </div>
        <span className="text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] border border-[#d6d9ff] rounded-full px-2.5 py-0.5">
          {profile.salaryHistory?.length || 0} records
        </span>
      </div>

      {/* ── Table ── */}
      <StaffSalaryHistoryTable
        fullname={profile.fullname}
        history={profile.salaryHistory}
      />
    </div>
  );
};
