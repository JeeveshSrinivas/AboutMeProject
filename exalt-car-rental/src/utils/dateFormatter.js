/**
 * Formats a standard ISO date string (YYYY-MM-DD) into a clean, readable layout
 * @param {string} dateString 
 * @returns {string} e.g., "Aug 19, 2026"
 */
export const formatDateDisplay = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric"
  });
};

/**
 * Parses a custom range string ("YYYY-MM-DD - YYYY-MM-DD") and extracts legibly formatted dates
 * @param {string} periodString 
 * @returns {Object} { start, end }
 */
export const parsePeriodDates = (periodString) => {
  if (!periodString || !periodString.includes(" - ")) return { start: "", end: "" };
  const [start, end] = periodString.split(" - ");
  return {
    start: formatDateDisplay(start),
    end: formatDateDisplay(end)
  };
};