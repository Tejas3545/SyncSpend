import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { Expense, Category, PaymentMethod, Period, Settings, SpendingSummary } from './types';
import { StorageService } from './services/storageService';
import { SyncManager } from './services/syncManager';
import { PhoneFrame } from './components/PhoneFrame';
import { AccountSwitcher } from './components/AccountSwitcher';
import { MainSpendingCard } from './components/MainSpendingCard';
import { ExpenseItem } from './components/ExpenseItem';
import { AddExpenseModal } from './components/AddExpenseModal';
import { ShortcutsModal } from './components/ShortcutsModal';
import { WidgetsModal } from './components/WidgetsModal';
import { HistoryModal } from './components/HistoryModal';
import { SettingsModal } from './components/SettingsModal';
import { toLocalDateString } from './utils/dateUtils';
import { Search, Layers, Settings as SettingsIcon, Plus } from 'lucide-react';

export const App: React.FC = () => {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [settings, setSettings] = useState<Settings>(StorageService.getSettings());
  const [period, setPeriod] = useState<Period>('WEEK');
  const [currentAccount, setCurrentAccount] = useState<string>(settings.accountName || 'Personal');

  // Network & Sync State
  const [isOnline, setIsOnline] = useState<boolean>(true);
  const [isSyncing, setIsSyncing] = useState<boolean>(false);
  const [syncNotice, setSyncNotice] = useState<string | null>(null);

  // Modals
  const [isAddOpen, setIsAddOpen] = useState<boolean>(false);
  const [isShortcutsOpen, setIsShortcutsOpen] = useState<boolean>(false);
  const [isWidgetsOpen, setIsWidgetsOpen] = useState<boolean>(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState<boolean>(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState<boolean>(false);

  // Initialize data on mount
  useEffect(() => {
    setExpenses(StorageService.getExpenses());
    setCategories(StorageService.getCategories());
    setPaymentMethods(StorageService.getPaymentMethods());
    const storedSettings = StorageService.getSettings();
    setSettings(storedSettings);
    setCurrentAccount(storedSettings.accountName || 'Personal');
    setIsOnline(SyncManager.isOnline());
  }, []);

  // Theme effect and active dark state
  const isDarkMode = useMemo(() => {
    if (settings.theme === 'dark') return true;
    if (settings.theme === 'light') return false;
    return typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches;
  }, [settings.theme]);

  useEffect(() => {
    const root = document.documentElement;
    if (isDarkMode) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }, [isDarkMode]);

  // Trigger sync in background
  const triggerSync = useCallback(async () => {
    if (!settings.isNotionEnabled && !settings.isGoogleSheetsEnabled) return;
    setIsSyncing(true);
    setSyncNotice(null);
    try {
      const res = await SyncManager.syncAllPending(settings);
      setExpenses(StorageService.getExpenses());
      if (res.errors.length > 0) {
        setSyncNotice(`Sync Notice: ${res.errors[0]}`);
      } else if (res.syncedNotion > 0 || res.syncedGoogle > 0) {
        setSyncNotice(`Synced ${res.syncedNotion + res.syncedGoogle} item(s) to cloud!`);
        setTimeout(() => setSyncNotice(null), 3000);
      }
    } catch (e) {
      console.error('Sync error', e);
    } finally {
      setIsSyncing(false);
    }
  }, [settings]);

  // Offline / Online listeners with auto-sync
  useEffect(() => {
    const handleOnline = () => {
      setIsOnline(true);
      if (settings.autoSyncOnOnline) {
        triggerSync();
      }
    };
    const handleOffline = () => {
      setIsOnline(false);
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, [settings.autoSyncOnOnline, triggerSync]);

  // Maps for fast lookups
  const categoryMap = useMemo(() => new Map(categories.map((c) => [c.id, c])), [categories]);
  const paymentMethodMap = useMemo(() => new Map(paymentMethods.map((p) => [p.id, p])), [paymentMethods]);

  // Filter expenses by account & period
  const accountExpenses = useMemo(() => {
    return expenses.filter((e) => !e.account || e.account === currentAccount);
  }, [expenses, currentAccount]);

  const filteredExpenses = useMemo(() => {
    const now = new Date();
    const nowTime = now.getTime();

    return accountExpenses.filter((e) => {
      if (period === 'ALL') return true;

      const expDate = new Date(e.date + 'T00:00:00');
      const diffDays = Math.floor((nowTime - expDate.getTime()) / (1000 * 60 * 60 * 24));

      if (period === 'WEEK') return diffDays <= 7;
      if (period === 'MONTH') return diffDays <= 30;
      if (period === 'YEAR') return diffDays <= 365;

      return true;
    });
  }, [accountExpenses, period]);

  const totalSpending = useMemo(() => {
    return filteredExpenses.reduce((acc, curr) => acc + curr.amount, 0);
  }, [filteredExpenses]);

  // Spending summary for Widgets modal (Matching Screenshot 2)
  const spendingSummary: SpendingSummary = useMemo(() => {
    const now = new Date();
    const nowTime = now.getTime();

    let week = 0;
    let month = 0;
    let year = 0;
    let all = 0;

    for (const e of accountExpenses) {
      const expDate = new Date(e.date + 'T00:00:00');
      const diffDays = Math.floor((nowTime - expDate.getTime()) / (1000 * 60 * 60 * 24));
      all += e.amount;
      if (diffDays <= 7) week += e.amount;
      if (diffDays <= 30) month += e.amount;
      if (diffDays <= 365) year += e.amount;
    }

    return {
      totalThisWeek: week,
      totalThisMonth: month,
      totalThisYear: year,
      totalAllTime: all,
      dailyBreakdown: [],
    };
  }, [accountExpenses]);

  // 7-day chart data (Sun - Sat or last 7 days)
  const chartData = useMemo(() => {
    const today = new Date();
    const days = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date(today);
      d.setDate(d.getDate() - i);
      const dateStr = toLocalDateString(d);
      const dayLabel = d.toLocaleDateString('en-US', { weekday: 'short' });

      const amount = accountExpenses
        .filter((e) => e.date === dateStr)
        .reduce((sum, e) => sum + e.amount, 0);

      days.push({ label: dayLabel, amount, dateStr });
    }
    return days;
  }, [accountExpenses]);

  // Grouped expenses for main feed (Matching Screenshot 1: "Latest", "Monday", etc.)
  const groupedSections = useMemo(() => {
    // 1. Group strictly by exact date key
    const dateMap = new Map<string, Expense[]>();
    for (const exp of filteredExpenses) {
      if (!dateMap.has(exp.date)) {
        dateMap.set(exp.date, []);
      }
      dateMap.get(exp.date)!.push(exp);
    }

    // 2. Sort dates in descending chronological order (newest date first)
    const sortedDates = Array.from(dateMap.keys()).sort((a, b) => b.localeCompare(a));

    const todayStr = toLocalDateString(new Date());
    const yesterdayDate = new Date();
    yesterdayDate.setDate(yesterdayDate.getDate() - 1);
    const yesterdayStr = toLocalDateString(yesterdayDate);

    return sortedDates.map((dateKey) => {
      const items = dateMap.get(dateKey)!;
      // Sort within the day: newest creation time first
      items.sort((a, b) => b.createdAt - a.createdAt);

      let groupLabel = dateKey;
      if (dateKey === todayStr) {
        groupLabel = 'Latest';
      } else if (dateKey === yesterdayStr) {
        groupLabel = yesterdayDate.toLocaleDateString('en-US', { weekday: 'long' });
      } else {
        const parts = dateKey.split('-');
        if (parts.length === 3) {
          const d = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
          const weekday = d.toLocaleDateString('en-US', { weekday: 'long' });
          const dayMonth = d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
          groupLabel = `${weekday}, ${dayMonth}`;
        }
      }

      return [groupLabel, items] as [string, Expense[]];
    });
  }, [filteredExpenses]);

  // Pending unsynced count
  const pendingSyncCount = useMemo(() => {
    return expenses.filter(
      (e) =>
        ((settings.isNotionEnabled || settings.isNotionConnected) && !e.isNotionSynced) ||
        ((settings.isGoogleSheetsEnabled || settings.isGoogleConnected) && !e.isGoogleSynced)
    ).length;
  }, [expenses, settings]);

  // CRUD Handlers
  const handleSaveExpense = async (expenseData: Omit<Expense, 'id' | 'createdAt'>) => {
    const newExpense = StorageService.addExpense(expenseData);
    setExpenses(StorageService.getExpenses());

    // Auto sync immediately if online
    const isSyncActive =
      settings.isNotionEnabled ||
      settings.isNotionConnected ||
      settings.isGoogleSheetsEnabled ||
      settings.isGoogleConnected;

    if (isOnline && isSyncActive) {
      await SyncManager.syncSingleExpense(newExpense, settings);
      setExpenses(StorageService.getExpenses());
    }
  };

  const handleDeleteExpense = (id: string) => {
    StorageService.deleteExpense(id);
    setExpenses(StorageService.getExpenses());
  };

  const handleUpdateSettings = (newSettings: Settings) => {
    StorageService.saveSettings(newSettings);
    setSettings(newSettings);
  };

  const handleSelectAccount = (acc: string) => {
    setCurrentAccount(acc);
    const updated = { ...settings, accountName: acc };
    StorageService.saveSettings(updated);
    setSettings(updated);
  };

  const handleAddAccount = (acc: string) => {
    const updatedAccounts = Array.from(new Set([...(settings.accounts || []), acc]));
    const updated = { ...settings, accounts: updatedAccounts, accountName: acc };
    StorageService.saveSettings(updated);
    setSettings(updated);
    setCurrentAccount(acc);
  };

  const handleAddCategory = (name: string, iconName: string = 'tag') => {
    StorageService.addCategory(name, iconName);
    setCategories(StorageService.getCategories());
  };

  const handleDeleteCategory = (id: string) => {
    StorageService.deleteCategory(id);
    setCategories(StorageService.getCategories());
  };

  const handleAddPaymentMethod = (name: string) => {
    StorageService.addPaymentMethod(name);
    setPaymentMethods(StorageService.getPaymentMethods());
  };

  const handleDeletePaymentMethod = (id: string) => {
    StorageService.deletePaymentMethod(id);
    setPaymentMethods(StorageService.getPaymentMethods());
  };

  const handleExportCSV = () => {
    const csvContent = StorageService.exportToCSV();
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `syncspend_expenses_${toLocalDateString(new Date())}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleResetSampleData = () => {
    const reset = StorageService.resetToSampleData();
    setExpenses(reset);
    setSyncNotice('Official SyncSpend sample data reloaded.');
    setTimeout(() => setSyncNotice(null), 3000);
  };

  return (
    <PhoneFrame
      fitToFrame={settings.fitToFrame ?? true}
      onToggleFitToFrame={() => {
        const next = !settings.fitToFrame;
        const updated = { ...settings, fitToFrame: next };
        StorageService.saveSettings(updated);
        setSettings(updated);
      }}
      isOnline={isOnline}
      pendingSyncCount={pendingSyncCount}
      onQuickAddShortcut={() => setIsShortcutsOpen(true)}
      onOpenWidgets={() => setIsWidgetsOpen(true)}
      isDark={isDarkMode}
    >
      {/* Pinned Top Navigation Bar (Matching Screenshot 1) */}
      <div className="shrink-0 px-5 pt-3 pb-2 flex items-center justify-between z-30 select-none">
        {/* Left: Account Switcher Pill ("Personal ▾") */}
        <AccountSwitcher
          currentAccount={currentAccount}
          accounts={settings.accounts || ['Personal', 'Business', 'Joint']}
          onSelectAccount={handleSelectAccount}
          onAddAccount={handleAddAccount}
        />

        {/* Right: Action Buttons in Liquid Glass Circles */}
        <div className="flex items-center gap-2">
          {/* Search Button */}
          <button
            id="btn-nav-search"
            onClick={() => setIsHistoryOpen(true)}
            className="w-9 h-9 rounded-full liquid-glass flex items-center justify-center text-neutral-700 dark:text-neutral-200 hover:opacity-80 active:scale-95 transition-all shadow-xs"
            title="Search & History"
          >
            <Search className="w-4 h-4" />
          </button>

          {/* Widgets / Spending Trends Button */}
          <button
            id="btn-nav-widgets"
            onClick={() => setIsWidgetsOpen(true)}
            className="w-9 h-9 rounded-full liquid-glass flex items-center justify-center text-neutral-700 dark:text-neutral-200 hover:opacity-80 active:scale-95 transition-all shadow-xs"
            title="Spending Trends & Widgets"
          >
            <Layers className="w-4 h-4" />
          </button>

          {/* Settings Button */}
          <button
            id="btn-nav-settings"
            onClick={() => setIsSettingsOpen(true)}
            className="w-9 h-9 rounded-full liquid-glass flex items-center justify-center text-neutral-700 dark:text-neutral-200 hover:opacity-80 active:scale-95 transition-all shadow-xs"
            title="Settings & Cloud Sync"
          >
            <SettingsIcon className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Sync notification toast */}
      {syncNotice && (
        <div className="shrink-0 mx-5 my-1 px-3 py-1.5 rounded-full bg-neutral-900/90 text-white text-[11px] text-center shadow-lg transition-all animate-fade-in z-30">
          {syncNotice}
        </div>
      )}

      {/* Main Scrollable Viewport (strictly scrolls inside the phone screen) */}
      <div className="flex-1 overflow-y-auto no-scrollbar px-5 pb-24 pt-2 space-y-5">
        {/* Main Spending Card with Bar Chart (Exact Match to Screenshot 1) */}
        <MainSpendingCard
          amount={totalSpending}
          chartData={chartData}
          period={period}
          onPeriodChange={setPeriod}
          currencySymbol={settings.currencySymbol || '$'}
        />

        {/* Grouped Expenses List (Exact Match to Screenshot 1: "Latest", "Monday", etc.) */}
        {groupedSections.length === 0 ? (
          <div className="py-12 text-center rounded-[28px] liquid-glass-card p-6">
            <p className="text-sm font-medium text-neutral-500">No expenses in this period.</p>
            <button
              onClick={() => setIsAddOpen(true)}
              className="mt-3 px-4 py-1.5 rounded-full bg-neutral-900 text-white dark:bg-white dark:text-black text-xs font-semibold shadow-xs hover:opacity-90"
            >
              Log an Expense
            </button>
          </div>
        ) : (
          groupedSections.map(([sectionTitle, items]) => (
            <div key={sectionTitle} className="space-y-1.5">
              {/* Section Header */}
              <span className="text-[13px] font-semibold text-[#8E8E93] dark:text-[#98989D] pl-3 tracking-[-0.01em] select-none">
                {sectionTitle}
              </span>

              {/* White Group Card */}
              <div className="liquid-glass-card rounded-[24px] divide-y divide-black/[0.04] dark:divide-white/[0.06] p-1.5">
                {items.map((exp) => (
                  <ExpenseItem
                    key={exp.id}
                    expense={exp}
                    category={categoryMap.get(exp.categoryId)}
                    paymentMethod={exp.paymentMethodId ? paymentMethodMap.get(exp.paymentMethodId) : undefined}
                    currencySymbol={settings.currencySymbol || '$'}
                    onDelete={handleDeleteExpense}
                    onClick={() => setIsHistoryOpen(true)}
                  />
                ))}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Floating Action Button (+) at bottom right (Exact Match to Screenshot 1) */}
      <div className="absolute right-5 bottom-6 z-30">
        <button
          id="btn-add-expense-fab"
          onClick={() => setIsAddOpen(true)}
          className="w-13 h-13 rounded-full bg-neutral-950 dark:bg-white text-white dark:text-neutral-950 flex items-center justify-center shadow-[0_10px_25px_-5px_rgba(0,0,0,0.4)] hover:scale-105 active:scale-95 transition-all"
          title="Add Expense"
        >
          <Plus className="w-6 h-6 stroke-[2.5]" />
        </button>
      </div>

      {/* Add Expense Modal with Smart Suggestions (Screenshot 5) */}
      <AddExpenseModal
        isOpen={isAddOpen}
        onClose={() => setIsAddOpen(false)}
        onSave={handleSaveExpense}
        categories={categories}
        paymentMethods={paymentMethods}
        currencySymbol={settings.currencySymbol || '$'}
        account={currentAccount}
      />

      {/* Shortcuts / Quick Log Modal (Screenshot 3) */}
      <ShortcutsModal
        isOpen={isShortcutsOpen}
        onClose={() => setIsShortcutsOpen(false)}
        onQuickSave={handleSaveExpense}
        currencySymbol={settings.currencySymbol || '$'}
        defaultCategoryId={categories[0]?.id || 'cat-1'}
        defaultPaymentMethodId={paymentMethods[0]?.id}
        account={currentAccount}
      />

      {/* Widgets Preview Modal (Screenshot 2) */}
      <WidgetsModal
        isOpen={isWidgetsOpen}
        onClose={() => setIsWidgetsOpen(false)}
        summary={spendingSummary}
        currencySymbol={settings.currencySymbol || '$'}
        isNotionConfigured={Boolean((settings.isNotionEnabled ?? true) && (settings.isNotionConnected || (settings.notionToken && settings.notionDatabaseId)))}
        isGoogleConfigured={Boolean((settings.isGoogleSheetsEnabled ?? true) && (settings.isGoogleConnected || settings.googleSheetsWebhookUrl))}
        onOpenSettings={() => setIsSettingsOpen(true)}
      />

      {/* Search & History Modal */}
      <HistoryModal
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
        expenses={accountExpenses}
        categories={categories}
        paymentMethods={paymentMethods}
        currencySymbol={settings.currencySymbol || '$'}
        onDeleteExpense={handleDeleteExpense}
        onExportCSV={handleExportCSV}
      />

      {/* Settings & Integrations Modal */}
      <SettingsModal
        isOpen={isSettingsOpen}
        onClose={() => setIsSettingsOpen(false)}
        settings={settings}
        onSaveSettings={handleUpdateSettings}
        categories={categories}
        onAddCategory={handleAddCategory}
        onDeleteCategory={handleDeleteCategory}
        paymentMethods={paymentMethods}
        onAddPaymentMethod={handleAddPaymentMethod}
        onDeletePaymentMethod={handleDeletePaymentMethod}
        onExportCSV={handleExportCSV}
        onTriggerSync={triggerSync}
        onResetSampleData={handleResetSampleData}
        isSyncing={isSyncing}
        isOnline={isOnline}
        pendingSyncCount={pendingSyncCount}
      />
    </PhoneFrame>
  );
};
