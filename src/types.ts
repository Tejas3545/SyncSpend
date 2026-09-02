export interface Category {
  id: string;
  name: string;
  emoji: string;
  isDefault?: boolean;
}

export interface PaymentMethod {
  id: string;
  name: string;
  isDefault?: boolean;
}

export interface Expense {
  id: string;
  name: string;
  amount: number;
  categoryId: string;
  paymentMethodId?: string | null;
  date: string; // ISO format 'YYYY-MM-DD'
  notionPageId?: string | null;
  isSynced: boolean;
  createdAt: number; // timestamp
}

export type Period = 'WEEK' | 'MONTH' | 'YEAR' | 'ALL';

export interface SpendingSummary {
  totalThisWeek: number;
  totalThisMonth: number;
  totalThisYear: number;
  totalAllTime: number;
  dailyBreakdown: { label: string; amount: number; dateStr: string }[];
}

export interface Settings {
  notionToken: string;
  notionDatabaseId: string;
  theme: 'system' | 'light' | 'dark';
  currencySymbol: string;
  accountName: string;
}
