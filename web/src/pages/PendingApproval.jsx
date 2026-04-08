import { useNavigate, useSearchParams } from "react-router-dom";
import { Clock3, ArrowLeft } from "lucide-react";

export const PendingApproval = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const sessionUser = JSON.parse(sessionStorage.getItem("user") || "{}");

  const fullName =
    searchParams.get("name") || sessionUser?.fullName || "User Name";

  const handleBack = () => {
    navigate("/login");
  };

  return (
    <div className="relative min-h-screen overflow-hidden bg-[#f5f5f5] flex items-center justify-center px-6">
      <div className="absolute inset-y-0 left-0 w-40 bg-gradient-to-b from-[#3b82f6] to-[#60a5fa]" />
      <div className="absolute inset-y-0 right-0 w-40 bg-gradient-to-b from-[#8b5cf6] to-[#a5b4fc]" />
      <div className="absolute inset-y-0 left-1/2 -translate-x-1/2 w-[85%] rounded-[999px] bg-[#f5f5f5]" />

      <div className="relative z-10 flex max-w-2xl flex-col items-center text-center">
        <div className="mb-10 flex h-36 w-36 items-center justify-center rounded-full bg-[#8f8cf3]">
          <Clock3 className="h-14 w-14 text-white stroke-[2.5]" />
        </div>

        <h1 className="text-4xl md:text-5xl font-extrabold leading-tight text-[#111243]">
          Account Pending Approval
        </h1>

        <p className="mt-5 max-w-xl text-lg md:text-xl leading-relaxed text-[#1a1d4f]">
          Hi {fullName}, thanks for signing up for the RevnU portal for ChiNyMic
          Restobar.
        </p>

        <button
          onClick={handleBack}
          className="mt-10 inline-flex items-center gap-2 rounded-2xl bg-[#8f8cf3] px-6 py-3 text-base font-semibold text-white shadow-sm transition hover:opacity-90"
        >
          <ArrowLeft className="h-5 w-5" />
          Check back later
        </button>
      </div>
    </div>
  );
};
