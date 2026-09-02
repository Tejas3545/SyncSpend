import React, { useState, useEffect, useMemo } from 'react';
import { Expense, Category, PaymentMethod, Period, Settings } from './types';
import { StorageService } from './services/storageService';
import { NotionService } from './services/notionService';
import { TotalSpendingCard } from './components/TotalSpendingCard';
import { SpendingChart } from './components/SpendingChart';
import { ExpenseItem } from './components/ExpenseItem';
import { AddExpenseModal } from './components/AddExpenseModal';
import { HistoryModal } from './components/HistoryModal';
import { SettingsModal } from './components/SettingsModal';
import { formatDateHeader, toLocalDateString } from './utils/dateUtils';
import { Plus, Settings as SettingsIcon, Search } from 'lucide-react';

export const App: React.FC = () => {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [settings, setSettings] = useState<Settings>(StorageService.getSettings());
  const [period, setPeriod] = useState<Period>('WEEK');

  // Modals state
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isSyncing, setIsSyncing] = useState(false);
  const [syncNotice, setSyncNotice] = useState<string | null>(null);

  // Load data on mount
  useEffect(() => {
    setExpenses(StorageService.getExpenses());
    setCategories(StorageService.getCategories());
    setPaymentMethods(StorageService.getPaymentMethods());
    setSettings(StorageService.getSettings());
  }, []);

  // Handle Theme
  useEffect(() => {
    const root = document.documentElement;
    if (settings.theme === 'dark') {
      root.classList.add('dark');
    } else if (settings.theme === 'light') {
      root.classList.remove('dark');
    } else {
      // System
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      if (prefersDark) {
        root.classList.add('dark');
      } else {
        root.classList.remove('dark');
      }
    }
  }, [settings.theme]);

  // Map for fast lookups
  const categoryMap = useMemo(() => new Map(categories.map((c) => [c.id, c])), [categories]);
  const paymentMethodMap = useMemo(() => new Map(paymentMethods.map((p) => [p.id, p])), [paymentMethods]);

  // Period filtering logic
  const filteredExpenses = useMemo(() => {
    const now = new Date();
    const nowTime = now.getTime();

    return expenses.filter((e) => {
      if (period === 'ALL') return true;

      const expDate = new Date(e.date + 'T00:00:00');
      const diffDays = Math.floor((nowTime - expDate.getTime()) / (1000 * 60 * 60 * 24));

      if (period === 'WEEK') return diffDays <= 7;
      if (period === 'MONTH') return diffDays <= 30;
      if (period === 'YEAR') return diffDays <= 365;

      return true;
    });
  }, [expenses, period]);

  const totalSpending = useMemo(() => {
    return filteredExpenses.reduce((acc, curr) => acc + curr.amount, 0);
  }, [filteredExpenses]);

  // Chart data calculation
  const chartData = useMemo(() => {
    const today = new Date();

    if (period === 'WEEK') {
      // 7 days (last 7 days from today)
      const days = [];
      for (let i = 6; i >= 0; i--) {
        const d = new Date(today);
        d.setDate(d.getDate() - i);
        const dateStr = toLocalDateString(d);
        const dayLabel = d.toLocaleDateString('en-US', { weekday: 'short' });

        const amount = expenses
          .filter((e) => e.date === dateStr)
          .reduce((sum, e) => sum + e.amount, 0);

        days.push({ label: dayLabel, amount, dateStr });
      }
      return days;
    }

    if (period === 'MONTH') {
      // 4 weekly blocks in the month
      const weeks = [];
      for (let w = 3; w >= 0; w--) {
        const startDay = new Date(today);
        startDay.setDate(startDay.getDate() - w * 7);
        const endDay = new Date(startDay);
        endDay.setDate(endDay.getDate() + 6);

        const label = `W${4 - w}`;
        const amount = expenses
          .filter((e) => {
            const d = new Date(e.date + 'T00:00:00');
            return d >= new Date(startDay.getFullYear(), startDay.getMonth(), startDay.getDate()) &&
                   d <= new Date(endDay.getFullYear(), endDay.getMonth(), endDay.getDate());
          })
          .reduce((sum, e) => sum + e.amount, 0);

        weeks.push({ label, amount, dateStr: toLocalDateString(startDay) });
      }
      return weeks;
    }

    if (period === 'YEAR') {
      // 12 months
      const months = [];
      for (let m = 11; m >= 0; m--) {
        const d = new Date(today.getFullYear(), today.getMonth() - m, 1);
        const label = d.toLocaleDateString('en-US', { month: 'short' });
        const yearMonth = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;

        const amount = expenses
          .filter((e) => e.date.startsWith(yearMonth))
          .reduce((sum, e) => sum + e.amount, 0);

        months.push({ label, amount, dateStr: yearMonth });
      }
      return months;
    }

    // ALL TIME: top 6 categories breakdown
    const catTotals: { [id: string]: number } = {};
    for (const e of expenses) {
      catTotals[e.categoryId] = (catTotals[e.categoryId] || 0) + e.amount;
    }
    return categories.slice(0, 6).map((c) => ({
      label: c.emoji,
      amount: catTotals[c.id] || 0,
      dateStr: c.name,
    }));
  }, [expenses, period, categories]);

  // Group latest expenses by date (up to recent 10 transactions)
  const groupedRecentExpenses = useMemo(() => {
    const recent = [...expenses].slice(0, 10);
    const groups: { [date: string]: Expense[] } = {};
    for (const exp of recent) {
      if (!groups[exp.date]) {
        groups[exp.date] = [];
      }
      groups[exp.date].push(exp);
    }
    return Object.entries(groups).sort(([a], [b]) => b.localeCompare(a));
  }, [expenses]);

  // Add Expense
  const handleAddExpense = async (data: Omit<Expense, 'id' | 'createdAt'>) => {
    const created = StorageService.addExpense(data);
    const updated = StorageService.getExpenses();
    setExpenses(updated);

    // If Notion is configured, attempt auto sync
    if (settings.notionToken && settings.notionDatabaseId) {
      try {
        const cat = categoryMap.get(created.categoryId);
        const pm = created.paymentMethodId ? paymentMethodMap.get(created.paymentMethodId) : undefined;
        const syncResult = await NotionService.syncExpense(
          created,
          cat,
          pm,
          settings.notionToken,
          settings.notionDatabaseId
        );
        if (syncResult.success && syncResult.pageId) {
          StorageService.updateExpense(created.id, { isSynced: true, notionPageId: syncResult.pageId });
          setExpenses(StorageService.getExpenses());
        }
      } catch (err) {
        console.warn('Background Notion sync error', err);
      }
    }
  };

  // Delete Expense
  const handleDeleteExpense = (id: string) => {
    StorageService.deleteExpense(id);
    setExpenses(StorageService.getExpenses());
  };

  // Category & Payment method handlers
  const handleAddCategory = (name: string, emoji: string) => {
    StorageService.addCategory(name, emoji);
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

  // Settings update
  const handleSaveSettings = (newSettings: Settings) => {
    StorageService.saveSettings(newSettings);
    setSettings(newSettings);
  };

  // Export CSV
  const handleExportCSV = () => {
    const csvContent = StorageService.exportToCSV();
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `spendsync_export_${toLocalDateString(new Date())}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Trigger Notion Sync
  const handleTriggerSync = async () => {
    if (!settings.notionToken || !settings.notionDatabaseId) return;

    setIsSyncing(true);
    setSyncNotice(null);

    const unsynced = expenses.filter((e) => !e.isSynced);
    if (unsynced.length === 0) {
      setSyncNotice('All transactions are already synced.');
      setIsSyncing(false);
      return;
    }

    let syncedCount = 0;
    for (const exp of unsynced) {
      const cat = categoryMap.get(exp.categoryId);
      const pm = exp.paymentMethodId ? paymentMethodMap.get(exp.paymentMethodId) : undefined;
      const res = await NotionService.syncExpense(
        exp,
        cat,
        pm,
        settings.notionToken,
        settings.notionDatabaseId
      );
      if (res.success && res.pageId) {
        StorageService.updateExpense(exp.id, { isSynced: true, notionPageId: res.pageId });
        syncedCount++;
      }
    }

    setExpenses(StorageService.getExpenses());
    setIsSyncing(false);
    setSyncNotice(`Successfully synced ${syncedCount} of ${unsynced.length} records to Notion.`);
    setTimeout(() => setSyncNotice(null), 4000);
  };

  const isNotionConfigured = Boolean(settings.notionToken && settings.notionDatabaseId);

  return (
    <div className="min-h-screen bg-white text-black dark:bg-black dark:text-white transition-colors">
      {/* Centered Mobile/Desktop Container */}
      <div className="mx-auto max-w-lg min-h-screen flex flex-col px-4 pt-4 pb-24 sm:px-6">
        {/* Top App Bar Header */}
        <header className="flex items-center justify-between py-2">
          {/* Workspace / Account Title */}
          <div className="flex items-center gap-1.5">
            <h1 className="text-2xl font-bold tracking-tight text-black dark:text-white">
              SpendSync
            </h1>
            <span className="rounded-full bg-black/5 px-2 py-0.5 text-[11px] font-semibold text-neutral-600 dark:bg-white/10 dark:text-neutral-300">
              {settings.accountName}
            </span>
          </div>

          {/* Actions: Search & Settings */}
          <div className="flex items-center gap-1">
            <button
              id="btn-open-search"
              onClick={() => setIsHistoryOpen(true)}
              title="Search and History"
              className="flex h-10 w-10 items-center justify-center rounded-full hover:bg-neutral-100 text-neutral-800 dark:text-neutral-200 dark:hover:bg-[#1C1C1E] transition-colors"
            >
              <Search className="h-5 w-5" />
            </button>

            <button
              id="btn-open-settings"
              onClick={() => setIsSettingsOpen(true)}
              title="Settings"
              className="flex h-10 w-10 items-center justify-center rounded-full hover:bg-neutral-100 text-neutral-800 dark:text-neutral-200 dark:hover:bg-[#1C1C1E] transition-colors"
            >
              <SettingsIcon className="h-5 w-5" />
            </button>
          </div>
        </header>

        {/* Cloud Sync Alert Banner if triggered */}
        {syncNotice && (
          <div className="mt-2 rounded-xl bg-emerald-50 px-3.5 py-2 text-xs font-medium text-emerald-800 dark:bg-emerald-950/40 dark:text-emerald-300 transition-all flex items-center justify-between">
            <span>{syncNotice}</span>
            <button onClick={() => setSyncNotice(null)} className="text-xs font-bold">×</button>
          </div>
        )}

        {/* Main Dashboard Content */}
        <main className="mt-4 flex-1 space-y-4">
          {/* Total Spending Card */}
          <TotalSpendingCard
            amount={totalSpending}
            period={period}
            currencySymbol={settings.currencySymbol}
            itemCount={filteredExpenses.length}
          />

          {/* Spending Bar Chart & Period Selector */}
          <SpendingChart
            data={chartData}
            period={period}
            onPeriodChange={setPeriod}
            currencySymbol={settings.currencySymbol}
          />

          {/* Section: Latest Transactions */}
          <section className="pt-2">
            <div className="flex items-center justify-between pb-2.5">
              <h2 className="text-xs font-medium uppercase tracking-wider text-neutral-500 dark:text-neutral-400">
                Recent Expenses
              </h2>
              <button
                id="btn-view-all-history"
                onClick={() => setIsHistoryOpen(true)}
                className="text-xs font-semibold text-[#007AFF] hover:underline"
              >
                View All ({expenses.length})
              </button>
            </div>

            {/* List grouped by date */}
            {groupedRecentExpenses.length === 0 ? (
              <div className="rounded-2xl border border-dashed border-neutral-200 p-8 text-center dark:border-neutral-800">
                <p className="text-sm text-neutral-500 dark:text-neutral-400">
                  No expenses recorded yet.
                </p>
                <button
                  id="btn-add-first-expense"
                  onClick={() => setIsAddOpen(true)}
                  className="mt-3 rounded-full bg-black px-4 py-2 text-xs font-semibold text-white dark:bg-white dark:text-black hover:opacity-90"
                >
                  Log your first expense
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                {groupedRecentExpenses.map(([dateStr, dayExpenses]) => (
                  <div key={dateStr} className="space-y-1.5">
                    <p className="px-1 text-xs font-medium text-neutral-500 dark:text-neutral-400">
                      {formatDateHeader(dateStr)}
                    </p>
                    <div className="space-y-2">
                      {dayExpenses.map((exp) => (
                        <ExpenseItem
                          key={exp.id}
                          expense={exp}
                          category={categoryMap.get(exp.categoryId)}
                          paymentMethod={exp.paymentMethodId ? paymentMethodMap.get(exp.paymentMethodId) : undefined}
                          currencySymbol={settings.currencySymbol}
                          onDelete={handleDeleteExpense}
                        />
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </main>

        {/* Floating Action Button (FAB) */}
        <div className="fixed bottom-6 right-6 sm:right-auto sm:left-1/2 sm:translate-x-44 z-40">
          <button
            id="fab-add-expense"
            onClick={() => setIsAddOpen(true)}
            aria-label="Add Expense"
            className="flex h-14 w-14 items-center justify-center rounded-full bg-black text-white shadow-xl hover:scale-105 active:scale-95 transition-all dark:bg-white dark:text-black"
          >
            <Plus className="h-6 w-6 stroke-[2.5]" />
          </button>
        </div>

        {/* Modals */}
        <AddExpenseModal
          isOpen={isAddOpen}
          onClose={() => setIsAddOpen(false)}
          onSave={handleAddExpense}
          categories={categories}
          paymentMethods={paymentMethods}
          currencySymbol={settings.currencySymbol}
          isNotionConfigured={isNotionConfigured}
        />

        <HistoryModal
          isOpen={isHistoryOpen}
          onClose={() => setIsHistoryOpen(false)}
          expenses={expenses}
          categories={categories}
          paymentMethods={paymentMethods}
          currencySymbol={settings.currencySymbol}
          onDeleteExpense={handleDeleteExpense}
          onExportCSV={handleExportCSV}
        />

        <SettingsModal
          isOpen={isSettingsOpen}
          onClose={() => setIsSettingsOpen(false)}
          settings={settings}
          onSaveSettings={handleSaveSettings}
          categories={categories}
          onAddCategory={handleAddCategory}
          onDeleteCategory={handleDeleteCategory}
          paymentMethods={paymentMethods}
          onAddPaymentMethod={handleAddPaymentMethod}
          onDeletePaymentMethod={handleDeletePaymentMethod}
          onExportCSV={handleExportCSV}
          onTriggerSync={handleTriggerSync}
          isSyncing={isSyncing}
        />
      </div>
    </div>
  );
};
