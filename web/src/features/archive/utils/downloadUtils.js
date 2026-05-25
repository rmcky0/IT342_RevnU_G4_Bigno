import * as XLSX from "xlsx";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const fmt = (n) =>
  parseFloat(n || 0).toLocaleString("en-PH", { minimumFractionDigits: 2 });

const fmtTime = (dateStr) =>
  new Date(dateStr).toLocaleString("en-PH", {
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });

// ── Excel ─────────────────────────────────────────────────────────────────────

export const downloadExcel = (
  detail,
  date,
  restaurantName = "RevnU",
  ownerName = "",
) => {
  const { summary, sales = [], expenses = [] } = detail;
  const wb = XLSX.utils.book_new();

  const summaryRows = [
    ["RevnU – End of Day Report", ""],
    ["Restaurant", restaurantName],
    ownerName ? ["Prepared by", ownerName] : null,
    ["Date", date],
    ["", ""],
    ["Total Sales", `PHP ${fmt(summary?.totalSales)}`],
    ["Total Expenses", `PHP ${fmt(summary?.totalExpenses)}`],
    ["Total Salaries", `PHP ${fmt(summary?.totalSalaries)}`],
    ["Net Profit / Loss", `PHP ${fmt(summary?.netProfit)}`],
    ["Email Report Sent", summary?.reportSent ? "Yes" : "No"],
  ].filter(Boolean);

  const wsSummary = XLSX.utils.aoa_to_sheet(summaryRows);
  wsSummary["!cols"] = [{ wch: 24 }, { wch: 20 }];
  XLSX.utils.book_append_sheet(wb, wsSummary, "Summary");

  const salesHeader = ["Time", "Amount (PHP)", "Category", "Notes"];
  const salesRows = sales.map((s) => [
    fmtTime(s.createdAt),
    parseFloat(s.amount || 0),
    s.categoryName || "—",
    s.notes || "—",
  ]);
  salesRows.push(["", "", "", ""]);
  salesRows.push([
    "TOTAL",
    sales.reduce((acc, s) => acc + parseFloat(s.amount || 0), 0),
    "",
    "",
  ]);
  const wsSales = XLSX.utils.aoa_to_sheet([salesHeader, ...salesRows]);
  wsSales["!cols"] = [{ wch: 22 }, { wch: 16 }, { wch: 20 }, { wch: 35 }];
  XLSX.utils.book_append_sheet(wb, wsSales, "Sales");

  const expHeader = ["Time", "Amount (PHP)", "Category", "Notes"];
  const expRows = expenses.map((e) => [
    fmtTime(e.createdAt),
    parseFloat(e.amount || 0),
    e.categoryName || "—",
    e.notes || "—",
  ]);
  expRows.push(["", "", "", ""]);
  expRows.push([
    "TOTAL",
    expenses.reduce((acc, e) => acc + parseFloat(e.amount || 0), 0),
    "",
    "",
  ]);
  const wsExpenses = XLSX.utils.aoa_to_sheet([expHeader, ...expRows]);
  wsExpenses["!cols"] = [{ wch: 22 }, { wch: 16 }, { wch: 20 }, { wch: 35 }];
  XLSX.utils.book_append_sheet(wb, wsExpenses, "Expenses");

  XLSX.writeFile(wb, `RevnU_EOD_${date}.xlsx`);
};

// ── PDF builder ───────────────────────────────────────────────────────────────

const BRAND = [99, 102, 241];
const TEXT_MAIN = [55, 65, 81];
const TEXT_LIGHT = [107, 114, 128];
const BORDER_COLOR = [229, 231, 235];

const USABLE_W = 182;
const COL_TIME = 35;
const COL_AMT = 40;
const COL_CAT = 40;
const COL_NOTE = USABLE_W - COL_TIME - COL_AMT - COL_CAT;

export const buildEodPdf = (
  detail,
  date,
  restaurantName = "RevnU",
  ownerName = "",
) => {
  const { summary, sales = [], expenses = [] } = detail;
  const doc = new jsPDF({ orientation: "portrait", unit: "mm", format: "a4" });

  // ── Header band ────────────────────────────────────────────────────────────
  doc.setFillColor(...BRAND);
  doc.rect(0, 0, 210, 35, "F");

  doc.setTextColor(255, 255, 255);
  doc.setFontSize(22);
  doc.setFont("helvetica", "bold");
  doc.text("RevnU", 14, 16);

  doc.setFontSize(10);
  doc.setFont("helvetica", "normal");
  doc.text("End of Day Report", 14, 24);
  doc.setFontSize(11);
  doc.setFont("helvetica", "bold");
  doc.text(restaurantName, 196, 15, { align: "right" });

  doc.setFontSize(9);
  doc.setFont("helvetica", "normal");
  if (ownerName) {
    doc.text(`Prepared by: ${ownerName}`, 196, 21, { align: "right" });
  }
  doc.text(`Date: ${date}`, 196, ownerName ? 27 : 21, { align: "right" });

  // ── Summary KPIs ───────────────────────────────────────────────────────────
  doc.setTextColor(...TEXT_MAIN);
  doc.setFontSize(12);
  doc.setFont("helvetica", "bold");
  doc.text("Daily Summary", 14, 48);

  const profit = parseFloat(summary?.netProfit || 0);
  const profitColor = profit >= 0 ? [5, 150, 105] : [220, 38, 38];

  autoTable(doc, {
    startY: 52,
    head: [["Metric", "Amount (PHP)"]],
    body: [
      ["Total Sales", `PHP ${fmt(summary?.totalSales)}`],
      ["Total Expenses", `PHP ${fmt(summary?.totalExpenses)}`],
      ["Total Salaries", `PHP ${fmt(summary?.totalSalaries)}`],
      ["Net Profit / Loss", `PHP ${fmt(summary?.netProfit)}`],
    ],
    theme: "plain",
    headStyles: {
      textColor: TEXT_LIGHT,
      fontStyle: "bold",
      fontSize: 9,
      cellPadding: { top: 4, bottom: 4, left: 4, right: 4 },
    },
    bodyStyles: {
      fontSize: 10,
      textColor: TEXT_MAIN,
      cellPadding: { top: 4, bottom: 4, left: 4, right: 4 },
      lineColor: BORDER_COLOR,
      lineWidth: { bottom: 0.1 },
    },
    columnStyles: {
      0: { cellWidth: 108 },
      1: { cellWidth: 74, halign: "right", fontStyle: "bold" },
    },
    didParseCell(data) {
      if (data.row.index === 3 && data.column.index === 1) {
        data.cell.styles.textColor = profitColor;
      }
    },
    margin: { left: 14, right: 14 },
  });

  // ── Sales table ────────────────────────────────────────────────────────────
  const y1 = doc.lastAutoTable.finalY + 12;
  doc.setFont("helvetica", "bold");
  doc.setFontSize(12);
  doc.setTextColor(...TEXT_MAIN);
  doc.text(`Sales Records (${sales.length})`, 14, y1);

  autoTable(doc, {
    startY: y1 + 4,
    head: [["Time", "Amount (PHP)", "Category", "Notes"]],
    body: sales.map((s) => [
      fmtTime(s.createdAt),
      `PHP ${fmt(s.amount)}`,
      s.categoryName || "—",
      s.notes || "—",
    ]),
    foot: sales.length
      ? [
          [
            "Total",
            `PHP ${fmt(sales.reduce((a, s) => a + parseFloat(s.amount || 0), 0))}`,
            "",
            "",
          ],
        ]
      : undefined,
    theme: "striped",
    headStyles: {
      fillColor: BRAND,
      textColor: [255, 255, 255],
      fontStyle: "bold",
      fontSize: 9,
    },
    alternateRowStyles: {
      fillColor: [249, 250, 251],
    },
    footStyles: {
      fillColor: [243, 244, 246],
      textColor: TEXT_MAIN,
      fontStyle: "bold",
      fontSize: 9,
    },
    styles: {
      fontSize: 9,
      textColor: TEXT_MAIN,
      cellPadding: 4,
      overflow: "linebreak",
    },
    columnStyles: {
      0: { cellWidth: COL_TIME },
      1: { cellWidth: COL_AMT, halign: "right" },
      2: { cellWidth: COL_CAT },
      3: { cellWidth: COL_NOTE },
    },
    margin: { left: 14, right: 14 },
  });

  // ── Expenses table ─────────────────────────────────────────────────────────
  const y2 = doc.lastAutoTable.finalY + 12;
  doc.setFont("helvetica", "bold");
  doc.setFontSize(12);
  doc.setTextColor(...TEXT_MAIN);
  doc.text(`Expense Records (${expenses.length})`, 14, y2);

  autoTable(doc, {
    startY: y2 + 4,
    head: [["Time", "Amount (PHP)", "Category", "Notes"]],
    body: expenses.map((e) => [
      fmtTime(e.createdAt),
      `PHP ${fmt(e.amount)}`,
      e.categoryName || "—",
      e.notes || "—",
    ]),
    foot: expenses.length
      ? [
          [
            "Total",
            `PHP ${fmt(expenses.reduce((a, e) => a + parseFloat(e.amount || 0), 0))}`,
            "",
            "",
          ],
        ]
      : undefined,
    theme: "striped",
    headStyles: {
      fillColor: [245, 158, 11],
      textColor: [255, 255, 255],
      fontStyle: "bold",
      fontSize: 9,
    },
    alternateRowStyles: {
      fillColor: [255, 251, 235],
    },
    footStyles: {
      fillColor: [254, 243, 199],
      textColor: TEXT_MAIN,
      fontStyle: "bold",
      fontSize: 9,
    },
    styles: {
      fontSize: 9,
      textColor: TEXT_MAIN,
      cellPadding: 4,
      overflow: "linebreak",
    },
    columnStyles: {
      0: { cellWidth: COL_TIME },
      1: { cellWidth: COL_AMT, halign: "right" },
      2: { cellWidth: COL_CAT },
      3: { cellWidth: COL_NOTE },
    },
    margin: { left: 14, right: 14 },
  });

  // ── Page footer ────────────────────────────────────────────────────────────
  const pageCount = doc.getNumberOfPages();
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i);
    doc.setFontSize(8);
    doc.setTextColor(156, 163, 175);
    doc.text(
      `${restaurantName} · RevnU EOD Report · ${date} · Page ${i} of ${pageCount}`,
      105,
      290,
      { align: "center" },
    );
  }

  return doc;
};

export const downloadPdf = (detail, date, restaurantName, ownerName) => {
  buildEodPdf(detail, date, restaurantName, ownerName).save(
    `RevnU_EOD_${date}.pdf`,
  );
};

export const previewPdfUrl = (detail, date, restaurantName, ownerName) => {
  const doc = buildEodPdf(detail, date, restaurantName, ownerName);
  const blob = doc.output("blob");
  return URL.createObjectURL(blob);
};
