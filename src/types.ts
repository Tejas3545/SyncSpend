export interface Category {
  id: string;
  name: string;
  iconName: string;
  emoji?: string;
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
  account: string; // e.g. 'Personal', 'Business'
  date: string; // ISO format 'YYYY-MM-DD'
  notionPageId?: string | null;
  googleRowId?: string | null;
  isSynced: boolean;
  isGoogleSynced?: boolean;
  isNotionSynced?: boolean;
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
  isNotionEnabled: boolean;
  googleSheetsWebhookUrl: string;
  isGoogleSheetsEnabled: boolean;
  theme: 'system' | 'light' | 'dark';
  currencySymbol: string;
  accountName: string;
  accounts: string[];
  fitToFrame: boolean;
  autoSyncOnOnline: boolean;
}
