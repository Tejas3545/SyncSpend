import React from 'react';
import { Expense, Category, PaymentMethod } from '../types';
import { Trash2 } from 'lucide-react';

interface ExpenseItemProps {
  expense: Expense;
  category?: Category;
  paymentMethod?: PaymentMethod;
  currencySymbol?: string;
  onDelete?: (id: string) => void;
}

export const ExpenseItem: React.FC<ExpenseItemProps> = ({
  expense,
  category,
  paymentMethod,
  currencySymbol = '₹',
  onDelete,
}) => {
  const emoji = category?.emoji || '📌';
  const categoryName = category?.name || 'General';

  return (
    <div
      id={`expense-item-${expense.id}`}
      className="group relative flex items-center justify-between rounded-2xl bg-[#F5F5F5] p-3.5 transition-colors hover:bg-neutral-200/70 dark:bg-[#1C1C1E] dark:hover:bg-[#2C2C2E]"
    >
      {/* Left: Emoji box + Name + Category info */}
      <div className="flex items-center gap-3 min-w-0 pr-2">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white shadow-xs dark:bg-[#2C2C2E]">
          <span className="text-xl leading-none">{emoji}</span>
        </div>
        <div className="min-w-0">
          <p className="truncate text-[15px] font-medium text-black dark:text-white">
            {expense.name}
          </p>
          <div className="flex items-center gap-1.5 text-xs text-neutral-500 dark:text-neutral-400">
            <span>{categoryName}</span>
            {paymentMethod && (
              <>
                <span>•</span>
                <span>{paymentMethod.name}</span>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Right: Amount + Sync indicator + Optional delete */}
      <div className="flex items-center gap-2.5 shrink-0">
        <div className="text-right">
          <p className="text-[16px] font-semibold text-black dark:text-white">
            {currencySymbol}{expense.amount.toFixed(2)}
          </p>
          <div className="flex items-center justify-end gap-1 text-[11px] text-neutral-500 dark:text-neutral-400">
            {!expense.isSynced ? (
              <span title="Pending cloud sync" className="text-xs">⏳</span>
            ) : (
              <span title="Synced to Notion" className="text-[10px] text-emerald-600 dark:text-emerald-400 font-medium">✓</span>
            )}
          </div>
        </div>

        {onDelete && (
          <button
            id={`btn-delete-expense-${expense.id}`}
            onClick={(e) => {
              e.stopPropagation();
              onDelete(expense.id);
            }}
            title="Delete expense"
            className="opacity-0 group-hover:opacity-100 transition-opacity p-1.5 text-neutral-400 hover:text-red-500 dark:text-neutral-500 dark:hover:text-red-400 focus:opacity-100"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        )}
      </div>
    </div>
  );
};
