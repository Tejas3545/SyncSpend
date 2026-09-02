import React, { useState, useMemo } from 'react';
import { Expense, Category, PaymentMethod, Period } from '../types';
import { ExpenseItem } from './ExpenseItem';
import { formatDateHeader } from '../utils/dateUtils';
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
  currencySymbol = '₹',
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

        if (selectedPeriod === 'WEEK' && diffDays > 7) {
          return false;
        }
        if (selectedPeriod === 'MONTH' && diffDays > 30) {
          return false;
        }
        if (selectedPeriod === 'YEAR' && diffDays > 365) {
          return false;
        }
      }

      return true;
    });
  }, [expenses, searchQuery, selectedPeriod, selectedCategoryId, categoryMap, paymentMethodMap]);

  // Group by date
  const grouped = useMemo(() => {
    const groups: { [dateStr: string]: Expense[] } = {};
    for (const exp of filteredExpenses) {
      if (!groups[exp.date]) {
        groups[exp.date] = [];
      }
      groups[exp.date].push(exp);
    }
    // Sort dates descending
    return Object.entries(groups).sort(([a], [b]) => b.localeCompare(a));
  }, [filteredExpenses]);

  const totalFilteredAmount = filteredExpenses.reduce((acc, curr) => acc + curr.amount, 0);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-xs p-0 sm:p-4">
      <div
        id="history-modal"
        className="relative w-full max-w-2xl h-full sm:h-auto sm:max-h-[90vh] overflow-hidden rounded-none sm:rounded-3xl bg-white shadow-2xl dark:bg-[#121212] flex flex-col"
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-neutral-100 px-5 py-4 dark:border-neutral-800">
          <div>
            <h2 className="text-lg font-bold text-black dark:text-white">Expense History</h2>
            <p className="text-xs text-neutral-500 dark:text-neutral-400">
              {filteredExpenses.length} transactions &bull; Total: {currencySymbol}{totalFilteredAmount.toFixed(2)}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <button
              id="btn-export-csv-history"
              onClick={onExportCSV}
              title="Export to CSV"
              className="flex items-center gap-1.5 rounded-full bg-[#F5F5F5] px-3 py-1.5 text-xs font-semibold text-black hover:bg-neutral-200 dark:bg-[#2C2C2E] dark:text-white dark:hover:bg-[#3A3A3C] transition-colors"
            >
              <Download className="h-3.5 w-3.5" />
              <span>CSV</span>
            </button>
            <button
              id="btn-close-history"
              onClick={onClose}
              className="p-1.5 text-neutral-500 hover:text-black dark:hover:text-white transition-colors"
            >
              <X className="h-5 w-5" />
            </button>
          </div>
        </div>

        {/* Search Bar */}
        <div className="border-b border-neutral-100 px-5 py-3 dark:border-neutral-800">
          <div className="relative flex items-center">
            <Search className="absolute left-3.5 h-4 w-4 text-neutral-400" />
            <input
              id="input-search-history"
              type="text"
              placeholder="Search expenses, categories, amounts..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full rounded-xl bg-[#F5F5F5] py-2 pl-9.5 pr-8 text-sm text-black outline-none placeholder:text-neutral-400 dark:bg-[#1C1C1E] dark:text-white"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="absolute right-2.5 text-neutral-400 hover:text-black dark:hover:text-white"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            )}
          </div>

          {/* Period Filter Chips */}
          <div className="mt-2.5 flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
            {(['ALL', 'WEEK', 'MONTH', 'YEAR'] as Period[]).map((p) => (
              <button
                key={p}
                id={`filter-period-${p.toLowerCase()}`}
                onClick={() => setSelectedPeriod(p)}
                className={`shrink-0 rounded-full px-3 py-1 text-xs font-medium transition-all ${
                  selectedPeriod === p
                    ? 'bg-black text-white dark:bg-white dark:text-black shadow-xs'
                    : 'bg-[#F5F5F5] text-neutral-600 hover:bg-neutral-200 dark:bg-[#1C1C1E] dark:text-neutral-400'
                }`}
              >
                {p === 'ALL' ? 'All Time' : p === 'WEEK' ? 'This Week' : p === 'MONTH' ? 'This Month' : 'This Year'}
              </button>
            ))}

            {/* Category dropdown pill */}
            <div className="relative shrink-0 flex items-center">
              <select
                id="select-category-filter"
                value={selectedCategoryId}
                onChange={(e) => setSelectedCategoryId(e.target.value)}
                className="rounded-full bg-[#F5F5F5] px-3 py-1 text-xs font-medium text-neutral-600 outline-none hover:bg-neutral-200 dark:bg-[#1C1C1E] dark:text-neutral-400 cursor-pointer"
              >
                <option value="ALL">All Categories</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.emoji} {c.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Grouped Transaction List */}
        <div className="flex-1 overflow-y-auto px-5 py-4 space-y-6">
          {grouped.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-sm font-medium text-neutral-500 dark:text-neutral-400">
                No expenses found matching your criteria.
              </p>
            </div>
          ) : (
            grouped.map(([dateStr, dayExpenses]) => {
              const dayTotal = dayExpenses.reduce((acc, curr) => acc + curr.amount, 0);
              return (
                <div key={dateStr} className="space-y-2">
                  <div className="flex items-center justify-between text-xs font-semibold text-neutral-500 dark:text-neutral-400 px-1">
                    <span>{formatDateHeader(dateStr)}</span>
                    <span>{currencySymbol}{dayTotal.toFixed(2)}</span>
                  </div>
                  <div className="space-y-2">
                    {dayExpenses.map((exp) => (
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
      </div>
    </div>
  );
};
