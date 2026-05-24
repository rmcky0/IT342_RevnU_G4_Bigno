import { useEffect } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";

const TERMS_CONTENT = (
  <>
    <p className="text-xs text-gray-400 mb-4">Last updated: May 22, 2025</p>

    <h3 className="font-semibold text-gray-800 mb-1">1. Acceptance of Terms</h3>
    <p className="text-sm text-gray-600 mb-4">
      By creating an account or using RevnU, you agree to be bound by these
      Terms of Service. If you do not agree, please do not use the platform.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      2. Description of Service
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      RevnU is a restaurant management platform that helps owners and staff
      track daily sales, manage expenses, monitor payroll, and view analytics.
      Features are provided on an as-is basis and may change without notice.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      3. Account Responsibilities
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      You are responsible for maintaining the confidentiality of your
      credentials. You agree to notify us immediately of any unauthorized use of
      your account. RevnU is not liable for losses caused by unauthorized access
      resulting from your failure to secure your credentials.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">4. Acceptable Use</h3>
    <p className="text-sm text-gray-600 mb-4">
      You agree not to misuse RevnU, including but not limited to: attempting to
      gain unauthorized access, uploading malicious content, or using the
      service for any unlawful purpose.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">5. Data Ownership</h3>
    <p className="text-sm text-gray-600 mb-4">
      You retain full ownership of the data you input into RevnU (sales records,
      expenses, staff information, etc.). By using the service you grant RevnU a
      limited license to store and process that data solely to provide the
      service to you.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">6. Termination</h3>
    <p className="text-sm text-gray-600 mb-4">
      We reserve the right to suspend or terminate accounts that violate these
      terms. You may delete your account at any time through your account
      settings.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      7. Limitation of Liability
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      RevnU is provided "as is" without warranties of any kind. We are not
      liable for any indirect, incidental, or consequential damages arising from
      your use of the platform.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">8. Changes to Terms</h3>
    <p className="text-sm text-gray-600">
      We may update these terms periodically. Continued use of RevnU after
      changes are posted constitutes your acceptance of the revised terms.
    </p>
  </>
);

const PRIVACY_CONTENT = (
  <>
    <p className="text-xs text-gray-400 mb-4">Last updated: May 22, 2025</p>

    <h3 className="font-semibold text-gray-800 mb-1">
      1. Information We Collect
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      We collect information you provide directly (name, email address,
      password) when you register, as well as business data you enter (sales
      records, expenses, staff details). If you sign in with Google, we receive
      your Google profile name and email.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      2. How We Use Your Information
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      Your data is used solely to provide and improve RevnU services:
      authenticating your account, displaying your restaurant's records and
      analytics, and sending transactional emails (e.g. OTP codes, password
      resets). We do not sell or share your personal data with third parties for
      marketing purposes.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      3. Data Storage and Security
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      Your data is stored on secured servers. Passwords are hashed using
      industry-standard algorithms and are never stored in plain text. We use
      HTTPS for all data in transit.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      4. Cookies and Sessions
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      RevnU uses HTTP-only cookies to maintain your authenticated session. No
      third-party advertising cookies are used.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">
      5. Third-Party Services
    </h3>
    <p className="text-sm text-gray-600 mb-4">
      We use Google OAuth for social sign-in and an email delivery service for
      transactional emails. These providers have their own privacy policies
      governing the data they process.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">6. Your Rights</h3>
    <p className="text-sm text-gray-600 mb-4">
      You have the right to access, correct, or delete your personal data at any
      time. You can update your profile in account settings or contact us to
      request full data deletion.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">7. Data Retention</h3>
    <p className="text-sm text-gray-600 mb-4">
      We retain your data for as long as your account is active. Upon account
      deletion, personal data is removed within 30 days, except where retention
      is required by law.
    </p>

    <h3 className="font-semibold text-gray-800 mb-1">8. Contact</h3>
    <p className="text-sm text-gray-600">
      For privacy-related questions or data requests, contact us at{" "}
      <span className="text-[#2563EB]">support@revnu.app</span>.
    </p>
  </>
);

export const PolicyModal = ({ type, onClose }) => {
  const isTerms = type === "terms";
  const title = isTerms ? "Terms of Service" : "Privacy Policy";
  const content = isTerms ? TERMS_CONTENT : PRIVACY_CONTENT;

  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "unset";
    };
  }, []);

  const modalContent = (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4"
      onClick={onClose}
    >
      <div
        className="relative bg-white rounded-2xl shadow-xl w-full max-w-lg max-h-[80vh] flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h2 className="text-lg font-bold text-gray-900">{title}</h2>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors"
          >
            <X size={18} />
          </button>
        </div>

        {/* Scrollable body */}
        <div className="overflow-y-auto px-6 py-5 flex-1">{content}</div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-100 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2 bg-[#2563EB] hover:bg-blue-700 text-white text-sm font-semibold rounded-lg transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );

  return createPortal(modalContent, document.body);
};
