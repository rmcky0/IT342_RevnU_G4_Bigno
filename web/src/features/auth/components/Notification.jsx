export const Notification = ({ type = "info", children }) => {
  const base = "mb-4 px-4 py-3 rounded-lg text-sm";
  if (type === "error")
    return (
      <div className={`${base} bg-red-50 border border-red-200 text-red-600`}>
        {children}
      </div>
    );
  if (type === "success")
    return (
      <div
        className={`${base} bg-green-50 border border-green-200 text-green-700`}
      >
        {children}
      </div>
    );
  return (
    <div className={`${base} bg-gray-50 border border-gray-200 text-gray-700`}>
      {children}
    </div>
  );
};
