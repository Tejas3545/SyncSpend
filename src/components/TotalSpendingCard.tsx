import React from 'react';
import { Period } from '../types';

interface TotalSpendingCardProps {
  amount: number;
  period: Period;
  currencySymbol?: string;
  itemCount: number;
}

export const TotalSpendingCard: React.FC<TotalSpendingCardProps> = ({
  amount,
  period,
  currencySymbol = '₹',
  itemCount,
}) => {
  const periodLabels: Record<Period, string> = {
    WEEK: 'This Week',
    MONTH: 'This Month',
    YEAR: 'This Year',
    ALL: 'All Time',
  };

  return (
    <div
      id="total-spending-card"
      className="relative overflow-hidden rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] transition-all"
    >
      <div className="flex items-center justify-between">
        <p className="text-xs font-medium uppercase tracking-wider text-neutral-500 dark:text-neutral-400">
          Total Spending
        </p>
        <span className="rounded-full bg-black/5 px-2.5 py-0.5 text-xs font-medium text-neutral-600 dark:bg-white/10 dark:text-neutral-300">
          {itemCount} {itemCount === 1 ? 'expense' : 'expenses'}
        </span>
      </div>

      <div className="my-2">
        <h1 className="text-4xl sm:text-5xl font-bold tracking-tight text-black dark:text-white">
          {currencySymbol}
          {amount.toLocaleString('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </h1>
      </div>

      <div className="flex items-center justify-between text-xs text-neutral-500 dark:text-neutral-400">
        <span>{periodLabels[period]}</span>
        <span>Minimalist &bull; Offline First</span>
      </div>
    </div>
  );
};
