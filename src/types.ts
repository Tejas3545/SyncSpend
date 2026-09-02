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

export interface GoogleAccount {
  email: string;
  name: string;
  avatar?: string;
  spreadsheetName: string;
  spreadsheetId: string;
  connectedAt: string;
  lastSyncedAt?: string;
}

export interface NotionAccount {
  workspaceName: string;
  workspaceIcon?: string;
  userEmail?: string;
  databaseName: string;
  databaseId: string;
  connectedAt: string;
  lastSyncedAt?: string;
}

export interface Settings {
  // Account Cloud Sync
  isGoogleConnected: boolean;
  googleAccount?: GoogleAccount | null;
  isNotionConnected: boolean;
  notionAccount?: NotionAccount | null;

  // Legacy fallback support
  isNotionEnabled?: boolean;
  notionToken?: string;
  notionDatabaseId?: string;
  isGoogleSheetsEnabled?: boolean;
  googleSheetsWebhookUrl?: string;

  // App Appearance & Options
  theme: 'system' | 'light' | 'dark';
  currencySymbol: string;
  accountName: string;
  accounts: string[];
  fitToFrame: boolean;
  autoSyncOnOnline: boolean;
  quickTapGestureEnabled?: boolean;
}
