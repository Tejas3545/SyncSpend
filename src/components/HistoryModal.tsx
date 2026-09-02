import React, { useState, useMemo } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Expense, Category, PaymentMethod, Period } from '../types';
import { ExpenseItem } from './ExpenseItem';
import { CategoryIcon } from './CategoryIcon';
import { Search, X, Download } from 'lucide-react';

interface HistoryModalProps {
  isOpen: boolean;
  onClose: () => void;
  expenses: Expense[];
  categories: Category[];
  paymentMethods: PaymentMethod[];
  currencySymbol?: string;
  onDeleteExpense: (id: string) => void;
  onExportCSV: () => void;
}

export const HistoryModal: React.FC<HistoryModalProps> = ({
  isOpen,
  onClose,
  expenses,
  categories,
  paymentMethods,
  currencySymbol = '$',
  onDeleteExpense,
  onExportCSV,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedPeriod, setSelectedPeriod] = useState<Period>('ALL');
  const [selectedCategoryId, setSelectedCategoryId] = useState<string>('ALL');

  const categoryMap = useMemo(() => new Map(categories.map((c) => [c.id, c])), [categories]);
  const paymentMethodMap = useMemo(() => new Map(paymentMethods.map((p) => [p.id, p])), [paymentMethods]);

  // Filter expenses
  const filteredExpenses = useMemo(() => {
    const today = new Date();
    const todayTime = today.getTime();

    return expenses.filter((expense) => {
      // 1. Text search
      if (searchQuery.trim()) {
        const query = searchQuery.toLowerCase();
        const catName = categoryMap.get(expense.categoryId)?.name.toLowerCase() || '';
        const pmName = expense.paymentMethodId ? paymentMethodMap.get(expense.paymentMethodId)?.name.toLowerCase() || '' : '';
        const matchName = expense.name.toLowerCase().includes(query);
        const matchCat = catName.includes(query);
        const matchPm = pmName.includes(query);
        const matchAmount = expense.amount.toString().includes(query);
        if (!matchName && !matchCat && !matchPm && !matchAmount) {
          return false;
        }
      }

      // 2. Category filter
      if (selectedCategoryId !== 'ALL' && expense.categoryId !== selectedCategoryId) {
        return false;
      }

      // 3. Period filter
      if (selectedPeriod !== 'ALL') {
        const expDate = new Date(expense.date + 'T00:00:00');
        const diffDays = Math.floor((todayTime - expDate.getTime()) / (1000 * 60 * 60 * 24));

        if (selectedPeriod === 'WEEK' && diffDays > 7) return false;
        if (selectedPeriod === 'MONTH' && diffDays > 30) return false;
        if (selectedPeriod === 'YEAR' && diffDays > 365) return false;
      }

      return true;
    });
  }, [expenses, searchQuery, selectedPeriod, selectedCategoryId, categoryMap, paymentMethodMap]);

  // Group by date
  const groupedExpenses = useMemo(() => {
    const groups: { [date: string]: Expense[] } = {};
    for (const exp of filteredExpenses) {
      if (!groups[exp.date]) {
        groups[exp.date] = [];
      }
      groups[exp.date].push(exp);
    }
    return Object.entries(groups).sort((a, b) => b[0].localeCompare(a[0]));
  }, [filteredExpenses]);

  const totalFilteredAmount = useMemo(() => {
    return filteredExpenses.reduce((acc, curr) => acc + curr.amount, 0);
  }, [filteredExpenses]);

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/60 backdrop-blur-md">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 15 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 10 }}
          className="relative w-full max-w-sm rounded-[36px] bg-[#F2F2F7] dark:bg-[#121214] p-5 shadow-2xl border border-white/40 dark:border-white/10 max-h-[90vh] flex flex-col overflow-hidden"
        >
          {/* Header */}
          <div className="flex items-center justify-between pb-3 border-b border-neutral-200/60 dark:border-neutral-800 shrink-0">
            <div>
              <h2 className="text-[17px] font-bold text-neutral-900 dark:text-white">All Expenses</h2>
              <span className="text-[11px] text-neutral-400">
                {filteredExpenses.length} records • Total: {currencySymbol}{totalFilteredAmount.toFixed(2)}
              </span>
            </div>
            <div className="flex items-center gap-1.5">
              <button
                onClick={onExportCSV}
                title="Export CSV"
                className="p-1.5 rounded-full bg-neutral-200/80 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 hover:opacity-80"
              >
                <Download className="w-3.5 h-3.5" />
              </button>
              <button
                onClick={onClose}
                className="p-1.5 rounded-full bg-neutral-200/80 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 hover:opacity-80"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Search Bar */}
          <div className="mt-3 relative shrink-0">
            <Search className="absolute left-3.5 top-3 w-4 h-4 text-neutral-400" />
            <input
              type="text"
              placeholder="Search expenses..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-8 py-2 text-xs rounded-2xl bg-white dark:bg-[#1C1C1E] text-neutral-900 dark:text-white outline-hidden border border-black/[0.04] dark:border-white/[0.05]"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="absolute right-3 top-2.5 text-neutral-400 hover:text-black"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>

          {/* Filter Pills */}
          <div className="flex gap-1 mt-2.5 overflow-x-auto no-scrollbar shrink-0 pb-1">
            {(['ALL', 'WEEK', 'MONTH', 'YEAR'] as Period[]).map((p) => (
              <button
                key={p}
                onClick={() => setSelectedPeriod(p)}
                className={`px-3 py-1 rounded-full text-[11px] font-semibold transition-colors shrink-0 ${
                  selectedPeriod === p
                    ? 'bg-neutral-900 text-white dark:bg-white dark:text-black'
                    : 'bg-neutral-200/70 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400'
                }`}
              >
                {p === 'ALL' ? 'All Time' : p === 'WEEK' ? 'Week' : p === 'MONTH' ? 'Month' : 'Year'}
              </button>
            ))}
          </div>

          {/* Category Filter Pills */}
          <div className="flex gap-1 mt-1.5 overflow-x-auto no-scrollbar shrink-0 pb-1">
            <button
              onClick={() => setSelectedCategoryId('ALL')}
              className={`px-2.5 py-0.5 rounded-full text-[10px] font-medium transition-colors shrink-0 ${
                selectedCategoryId === 'ALL'
                  ? 'bg-neutral-800 text-white dark:bg-neutral-200 dark:text-black'
                  : 'bg-neutral-200/50 dark:bg-neutral-800/60 text-neutral-500'
              }`}
            >
              All Categories
            </button>
            {categories.map((c) => (
              <button
                key={c.id}
                onClick={() => setSelectedCategoryId(selectedCategoryId === c.id ? 'ALL' : c.id)}
                className={`px-2.5 py-0.5 rounded-full text-[10px] font-medium transition-colors shrink-0 flex items-center gap-1.5 ${
                  selectedCategoryId === c.id
                    ? 'bg-neutral-800 text-white dark:bg-neutral-200 dark:text-black'
                    : 'bg-neutral-200/50 dark:bg-neutral-800/60 text-neutral-500'
                }`}
              >
                <CategoryIcon iconName={c.iconName} className="w-3 h-3" />
                <span>{c.name}</span>
              </button>
            ))}
          </div>

          {/* Grouped List */}
          <div className="flex-1 overflow-y-auto no-scrollbar mt-3 space-y-4">
            {groupedExpenses.length === 0 ? (
              <div className="py-12 text-center text-xs text-neutral-400">
                No matching expenses found.
              </div>
            ) : (
              groupedExpenses.map(([dateKey, items]) => {
                const parts = dateKey.split('-');
                let headerText = dateKey;
                if (parts.length === 3) {
                  const d = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
                  headerText = d.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' });
                }

                return (
                  <div key={dateKey} className="space-y-1.5">
                    <span className="text-[12px] font-medium text-neutral-400 dark:text-neutral-500 pl-2">
                      {headerText}
                    </span>
                    <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] divide-y divide-neutral-100 dark:divide-neutral-800/80 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
                      {items.map((exp) => (
                        <ExpenseItem
                          key={exp.id}
                          expense={exp}
                          category={categoryMap.get(exp.categoryId)}
                          paymentMethod={exp.paymentMethodId ? paymentMethodMap.get(exp.paymentMethodId) : undefined}
                          currencySymbol={currencySymbol}
                          onDelete={onDeleteExpense}
                        />
                      ))}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
