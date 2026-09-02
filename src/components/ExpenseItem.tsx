import React from 'react';
import { Expense, Category, PaymentMethod } from '../types';
import { CategoryIcon } from './CategoryIcon';
import { Trash2 } from 'lucide-react';

interface ExpenseItemProps {
  expense: Expense;
  category?: Category;
  paymentMethod?: PaymentMethod;
  currencySymbol?: string;
  onDelete?: (id: string) => void;
  onClick?: () => void;
}

export const ExpenseItem: React.FC<ExpenseItemProps> = ({
  expense,
  category,
  currencySymbol = '$',
  onDelete,
  onClick,
}) => {
  // Format date display: e.g. "10 Mar 2026"
  const formatDateDisplay = (dateStr: string) => {
    try {
      const parts = dateStr.split('-');
      if (parts.length === 3) {
        const d = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
        return d.toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' });
      }
      return dateStr;
    } catch {
      return dateStr;
    }
  };

  const iconName = category?.iconName || expense.name.toLowerCase();

  return (
    <div
      id={`expense-row-${expense.id}`}
      onClick={onClick}
      className="group relative flex items-center justify-between py-2.5 px-3 hover:bg-black/[0.02] dark:hover:bg-white/[0.04] transition-colors cursor-pointer"
    >
      {/* Left Column: Authentic iOS Squircle with clean SVG Icon + Name + Date */}
      <div className="flex items-center gap-3.5 min-w-0 pr-2">
        <div className="w-10 h-10 rounded-[12px] bg-[#EFEFF4] dark:bg-[#2C2C2E] flex items-center justify-center text-neutral-800 dark:text-neutral-200 shrink-0 shadow-2xs">
          <CategoryIcon iconName={iconName} className="w-4.5 h-4.5" />
        </div>

        <div className="min-w-0">
          <p className="text-[15px] font-semibold text-neutral-900 dark:text-white tracking-[-0.018em] truncate leading-tight">
            {expense.name}
          </p>
          <p className="text-[13px] font-normal text-[#8E8E93] dark:text-[#98989D] leading-tight mt-0.5 tracking-[-0.006em]">
            {formatDateDisplay(expense.date)}
          </p>
        </div>
      </div>

      {/* Right Column: Amount */}
      <div className="flex items-center gap-2 shrink-0">
        <span className="text-[15px] font-semibold text-neutral-900 dark:text-white tracking-[-0.015em] tabular-nums">
          {currencySymbol}{expense.amount.toFixed(2)}
        </span>

        {onDelete && (
          <button
            id={`btn-del-${expense.id}`}
            onClick={(e) => {
              e.stopPropagation();
              onDelete(expense.id);
            }}
            title="Delete Expense"
            className="opacity-0 group-hover:opacity-100 focus:opacity-100 transition-opacity p-1 text-neutral-400 hover:text-red-500 rounded-md hover:bg-neutral-100 dark:hover:bg-neutral-800 ml-1"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        )}
      </div>
    </div>
  );
};
