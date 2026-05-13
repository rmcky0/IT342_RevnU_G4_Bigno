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
  Calendar,
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
      <div className="flex-1 flex flex-col items-center justify-center h-full">
        <div className="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center mb-4">
          <User className="w-8 h-8 text-red-500" />
        </div>
        <h3 className="text-xl font-bold text-[#1e1b4b] mb-2">Profile Error</h3>
        <p className="text-sm text-gray-500 mb-6">{error}</p>
        <button
          onClick={() => navigate("/staff")}
          className="px-6 py-2.5 bg-[#7c83fd] text-white rounded-xl font-bold text-sm shadow-md hover:bg-[#6b72f5] transition-colors"
        >
          Return to Directory
        </button>
      </div>
    );
  }

  if (loading || !profile) return <StaffProfileSkeleton />;

  const initials = profile.fullname
    ? profile.fullname
        .split(" ")
        .slice(0, 2)
        .map((n) => n[0])
        .join("")
        .toUpperCase()
    : "?";

  const totalPaid = (profile.salaryHistory || []).reduce(
    (sum, record) => sum + (parseFloat(record.amount) || 0),
    0,
  );

  return (
    <div className="flex flex-col flex-1 h-full max-h-full space-y-5 overflow-hidden text-[#1e1b4b]">
      {/* Header Area */}
      <div className="shrink-0 flex items-center gap-4">
        <button
          onClick={() => navigate("/staff")}
          className="p-2 bg-white rounded-lg border border-gray-200 text-gray-500 hover:text-[#7c83fd] hover:border-indigo-100 shadow-sm transition-all"
          title="Back to Directory"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Staff Overview</h2>
        </div>
      </div>

      {/* Premium Profile Card */}
      <div className="shrink-0 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 p-6 flex flex-col md:flex-row items-start md:items-center gap-6 relative overflow-hidden group">
        <div className="absolute -right-10 -top-10 text-indigo-50 opacity-40 group-hover:scale-105 transition-transform duration-700 pointer-events-none">
          <User className="w-64 h-64" />
        </div>

        <div className="relative z-10 w-20 h-20 rounded-2xl bg-gradient-to-br from-indigo-50 to-white border border-indigo-100 flex items-center justify-center text-2xl font-black text-[#7c83fd] shadow-sm shrink-0">
          {initials}
        </div>

        <div className="relative z-10 flex-1">
          <h2 className="text-2xl font-bold text-[#1e1b4b] mb-2.5">
            {profile.fullname}
          </h2>
          <div className="flex flex-wrap gap-3">
            <div className="flex items-center gap-1.5 px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-xs font-bold text-gray-600 uppercase tracking-wider shadow-sm">
              <Briefcase className="w-3.5 h-3.5 text-gray-400" />
              {profile.position || "No position set"}
            </div>
            <div className="flex items-center gap-1.5 px-3 py-1.5 bg-indigo-50/50 border border-indigo-100 rounded-lg text-xs font-bold text-[#7c83fd] shadow-sm">
              <Wallet className="w-3.5 h-3.5" />
              BASE: ₱
              {parseFloat(profile.salaryRate || 0).toLocaleString(undefined, {
                minimumFractionDigits: 2,
              })}{" "}
              / MO
            </div>
          </div>
        </div>

        <div className="relative z-10 w-full md:w-auto md:pl-8 md:border-l border-gray-100 flex flex-col gap-1.5 pt-4 md:pt-0 border-t md:border-t-0 mt-2 md:mt-0">
          <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
            <TrendingUp className="w-3.5 h-3.5 text-emerald-500" /> Lifetime
            Paid
          </p>
          <div className="text-3xl font-black text-[#1e1b4b] flex items-baseline gap-1">
            <span className="text-xl text-gray-400 font-medium">₱</span>
            {totalPaid.toLocaleString(undefined, { minimumFractionDigits: 2 })}
          </div>
        </div>
      </div>

      {/* Salary History Header */}
      <div className="shrink-0 flex items-center justify-between pt-2">
        <h3 className="text-lg font-bold text-[#1e1b4b] flex items-center gap-2">
          <Calendar className="w-5 h-5 text-gray-400" /> Payout History
        </h3>
        <span className="px-2.5 py-0.5 bg-white text-gray-500 text-xs font-bold rounded-md border border-gray-200 shadow-sm">
          {profile.salaryHistory?.length || 0} records
        </span>
      </div>

      {/* Extracted Table Component */}
      <StaffSalaryHistoryTable
        fullname={profile.fullname}
        history={profile.salaryHistory}
      />
    </div>
  );
};
