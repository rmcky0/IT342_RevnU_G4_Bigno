import React from "react";
import { Users, Eye, Edit2, Trash2, Inbox } from "lucide-react";

export const StaffTable = ({
  data,
  loading,
  searchTerm,
  onView,
  onEdit,
  onDelete,
  navigate,
}) => {
  return (
    <table className="w-full text-left border-collapse whitespace-nowrap">
      <thead className="bg-gradient-to-r from-[#8f9df7] to-[#9faaf5] text-white sticky top-0 z-10 shadow-sm">
        <tr>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase">
            Employee
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase">
            Position
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase text-right">
            Salary Rate
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase text-center">
            Actions
          </th>
        </tr>
      </thead>
      <tbody className="divide-y divide-gray-50 relative">
        {loading ? (
          Array.from({ length: 6 }).map((_, idx) => (
            <tr key={`skel-${idx}`} className="animate-pulse">
              <td className="px-6 py-3.5">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 bg-gray-100 rounded-lg"></div>
                  <div className="h-3.5 bg-gray-100 rounded w-28"></div>
                </div>
              </td>
              <td className="px-6 py-3.5">
                <div className="h-4 bg-gray-100 rounded-md w-20"></div>
              </td>
              <td className="px-6 py-3.5 flex justify-end">
                <div className="h-4 bg-gray-100 rounded-md w-16 mt-2"></div>
              </td>
              <td className="px-6 py-3.5">
                <div className="flex justify-center gap-2">
                  <div className="w-6 h-6 bg-gray-100 rounded-md"></div>
                  <div className="w-6 h-6 bg-gray-100 rounded-md"></div>
                </div>
              </td>
            </tr>
          ))
        ) : data.length === 0 ? (
          <tr>
            <td colSpan="4" className="px-6 py-16 text-center">
              <div className="flex flex-col items-center justify-center max-w-sm mx-auto animate-in fade-in zoom-in-95 duration-300">
                <div className="w-16 h-16 bg-gradient-to-br from-indigo-50 to-gray-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner border border-white">
                  <Inbox className="w-8 h-8 text-[#7c83fd]" />
                </div>
                <h3 className="text-lg font-bold text-[#1e1b4b] mb-1">
                  No staff found
                </h3>
                <p className="text-sm text-gray-500 mb-5">
                  {searchTerm
                    ? "No employees match your search."
                    : "Your directory is currently empty."}
                </p>
              </div>
            </td>
          </tr>
        ) : (
          data.map((staff) => (
            <tr
              key={staff.id}
              onClick={() => navigate(`/staff/${staff.id}`)}
              className="group hover:bg-[#f8f9ff] hover:shadow-[inset_3px_0_0_0_#7c83fd] transition-all duration-200 cursor-pointer"
            >
              <td className="px-6 py-3 text-sm">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-lg bg-indigo-50 border border-indigo-100 flex items-center justify-center text-[#7c83fd] font-bold text-xs shadow-sm shrink-0">
                    {staff.fullname ? (
                      staff.fullname.charAt(0).toUpperCase()
                    ) : (
                      <Users className="w-4 h-4" />
                    )}
                  </div>
                  <p className="font-bold text-[#1e1b4b] group-hover:text-[#7c83fd] transition-colors truncate max-w-[200px]">
                    {staff.fullname || "N/A"}
                  </p>
                </div>
              </td>
              <td className="px-6 py-3">
                <span className="px-2.5 py-1 bg-gray-50 border border-gray-100 text-gray-600 text-[10px] font-bold uppercase rounded-md shadow-sm truncate max-w-[150px]">
                  {staff.position || "N/A"}
                </span>
              </td>
              <td className="px-6 py-3 text-sm font-bold text-gray-700 text-right">
                ₱
                {parseFloat(staff.salaryRate || 0).toLocaleString(undefined, {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td className="px-6 py-3">
                <div className="flex justify-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-200 transform translate-x-2 group-hover:translate-x-0">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onView(staff);
                    }}
                    className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-md transition-colors"
                    title="Quick View"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onEdit(staff);
                    }}
                    className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-md transition-colors"
                    title="Edit"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onDelete(staff);
                    }}
                    className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-md transition-colors"
                    title="Remove"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
};
