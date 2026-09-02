/**
 * Date utilities conforming to SpendSync UI Fix Patch v1.1
 */

export function formatDateHeader(rawDate: string): string {
  try {
    const today = new Date();
    const todayStr = toLocalDateString(today);
    
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayStr = toLocalDateString(yesterday);

    if (rawDate === todayStr) {
      return 'Today';
    }
    if (rawDate === yesterdayStr) {
      return 'Yesterday';
    }

    // If matches YYYY-MM-DD
    if (/^\d{4}-\d{2}-\d{2}$/.test(rawDate)) {
      const [year, month, day] = rawDate.split('-').map(Number);
      const d = new Date(year, month - 1, day);
      return d.toLocaleDateString('en-US', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
      }); // e.g. "Saturday, 6 June"
    }

    // If matches YYYY-MM
    if (/^\d{4}-\d{2}$/.test(rawDate)) {
      const [year, month] = rawDate.split('-').map(Number);
      const d = new Date(year, month - 1, 1);
      return d.toLocaleDateString('en-US', {
        month: 'long',
        year: 'numeric',
      }); // e.g. "June 2026"
    }

    return rawDate;
  } catch {
    return rawDate;
  }
}

export function toLocalDateString(d: Date): string {
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function formatCurrency(amount: number, symbol: string = '₹'): string {
  return `${symbol}${amount.toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}
