import React from "react";
import { Users, Eye, Edit2, Trash2, Inbox } from "lucide-react";

const Avatar = ({ name }) => (
  <div className="w-8 h-8 rounded-xl bg-[#7c83fd]/10 border border-[#7c83fd]/20 flex items-center justify-center text-[#7c83fd] font-bold text-xs shrink-0">
    {name ? name.charAt(0).toUpperCase() : <Users className="w-4 h-4" />}
  </div>
);

export const StaffTable = ({ data, loading, searchTerm, onView, onEdit, onDelete, navigate }) => {
  if (loading) {
    return (
      <div className="flex-1 overflow-auto rounded-xl border border-gray-100 bg-white">
        <table className="w-full text-left border-collapse whitespace-nowrap">
          <thead className="bg-gray-50 sticky top-0 z-10">
            <tr>
              {["Employee", "Position", "Salary Rate", "Actions"].map((h, i) => (
                <th
                  key={i}
                  className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {Array.from({ length: 6 }).map((_, idx) => (
              <tr key={idx} className="animate-pulse">
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 bg-gray-100 rounded-xl shrink-0" />
                    <div className="h-3 bg-gray-100 rounded w-28" />
                  </div>
                </td>
                <td className="px-5 py-3.5">
                  <div className="h-4 bg-gray-100 rounded-full w-20" />
                </td>
                <td className="px-5 py-3.5 text-right">
                  <div className="h-3 bg-gray-100 rounded w-16 ml-auto" />
                </td>
                <td className="px-5 py-3.5">
                  <div className="flex justify-center gap-1.5">
                    <div className="w-6 h-6 bg-gray-100 rounded-lg" />
                    <div className="w-6 h-6 bg-gray-100 rounded-lg" />
                    <div className="w-6 h-6 bg-gray-100 rounded-lg" />
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center py-20 rounded-xl border border-gray-100 bg-white animate-in fade-in zoom-in-95 duration-300">
        <div className="w-14 h-14 bg-indigo-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner">
          <Inbox className="w-7 h-7 text-[#7c83fd]" />
        </div>
        <p className="text-base font-bold text-[#1e1b4b] mb-1">No staff found</p>
        <p className="text-sm text-gray-400">
          {searchTerm ? "No employees match your search." : "Your directory is currently empty."}
        </p>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-auto rounded-xl border border-gray-100 bg-white">
      <table className="w-full text-left border-collapse whitespace-nowrap">
        <thead className="bg-gray-50 sticky top-0 z-10">
          <tr>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
              Employee
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
              Position
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 text-right">
              Salary Rate
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 text-center w-32">
              Actions
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-50">
          {data.map((staff) => (
            <tr
              key={staff.id}
              onClick={() => navigate(`/staff/${staff.id}`)}
              className="group hover:bg-[#f8f9ff] hover:shadow-[inset_3px_0_0_0_#7c83fd] transition-all duration-150 cursor-pointer"
            >
              <td className="px-5 py-3">
                <div className="flex items-center gap-3">
                  <Avatar name={staff.fullname} />
                  <span className="text-sm font-bold text-[#1e1b4b] group-hover:text-[#7c83fd] transition-colors truncate max-w-48">
                    {staff.fullname || "N/A"}
                  </span>
                </div>
              </td>
              <td className="px-5 py-3">
                <span className="px-2.5 py-1 bg-gray-100 text-gray-500 text-[10px] font-bold uppercase rounded-full border border-gray-200 truncate block max-w-36">
                  {staff.position || "N/A"}
                </span>
              </td>
              <td className="px-5 py-3 text-sm font-bold text-gray-800 text-right tabular-nums">
                ₱{parseFloat(staff.salaryRate || 0).toLocaleString(undefined, {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td className="px-5 py-3">
                <div className="flex justify-center gap-1 opacity-0 group-hover:opacity-100 translate-x-1 group-hover:translate-x-0 transition-all duration-150">
                  <button
                    onClick={(e) => { e.stopPropagation(); onView(staff); }}
                    className="p-1.5 rounded-lg text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 transition-colors"
                    title="Quick View"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); onEdit(staff); }}
                    className="p-1.5 rounded-lg text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 transition-colors"
                    title="Edit"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); onDelete(staff); }}
                    className="p-1.5 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                    title="Remove"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
