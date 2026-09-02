import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Period } from '../types';

interface ChartDay {
  label: string;
  amount: number;
  dateStr: string;
}

interface MainSpendingCardProps {
  amount: number;
  chartData: ChartDay[];
  period: Period;
  onPeriodChange: (p: Period) => void;
  currencySymbol?: string;
}

export const MainSpendingCard: React.FC<MainSpendingCardProps> = ({
  amount,
  chartData,
  period,
  onPeriodChange,
  currencySymbol = '$',
}) => {
  const [selectedDay, setSelectedDay] = useState<ChartDay | null>(null);

  // Exact guide lines matching Apple screenshot 1: 80, 60, 40, 20, 0
  const maxDayAmount = Math.max(...chartData.map((d) => d.amount), 78);
  const ceiling = Math.max(Math.ceil(maxDayAmount / 20) * 20, 80);
  const guideLines = [ceiling, Math.round((ceiling * 3) / 4), Math.round((ceiling * 2) / 4), Math.round(ceiling / 4)];

  return (
    <div
      id="main-spending-card"
      className="liquid-glass-card rounded-[28px] p-5 transition-all duration-300 relative overflow-hidden"
    >
      {/* Top Header: Total Spending & Period Filter (Screenshot 1) */}
      <div className="flex items-start justify-between">
        <div>
          <span className="text-[13px] font-medium text-[#8E8E93] dark:text-[#98989D] tracking-tight">
            Total Spending
          </span>
          <div className="mt-0.5">
            <h1 className="text-[36px] leading-tight font-bold tracking-tight text-neutral-900 dark:text-white">
              {currencySymbol}
              {amount.toLocaleString('en-US', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })}
            </h1>
          </div>
        </div>

        {/* Minimalist Period Switcher */}
        <div className="flex items-center rounded-full bg-black/[0.04] dark:bg-white/[0.08] p-0.5">
          {(['WEEK', 'MONTH', 'YEAR'] as Period[]).map((p) => {
            const isActive = period === p;
            const labels: Record<Period, string> = {
              WEEK: 'Week',
              MONTH: 'Month',
              YEAR: 'Year',
              ALL: 'All',
            };
            return (
              <button
                key={p}
                id={`btn-period-${p.toLowerCase()}`}
                onClick={() => {
                  onPeriodChange(p);
                  setSelectedDay(null);
                }}
                className={`px-2.5 py-1 text-[11px] font-semibold transition-all rounded-full ${
                  isActive
                    ? 'bg-white text-neutral-900 shadow-2xs dark:bg-neutral-800 dark:text-white'
                    : 'text-neutral-500 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white'
                }`}
              >
                {labels[p]}
              </button>
            );
          })}
        </div>
      </div>

      {/* Bar Chart with Guide Lines (Exact reproduction of Screenshot 1) */}
      <div className="relative mt-5 pt-2 pb-1">
        {/* Horizontal Guide Lines with Labels on right */}
        <div className="absolute inset-0 flex flex-col justify-between pointer-events-none pb-6">
          {guideLines.map((level, i) => (
            <div key={i} className="flex items-center w-full">
              <div className="flex-1 h-[1px] bg-black/[0.04] dark:bg-white/[0.06]" />
              <span className="pl-2 text-[10px] font-mono text-[#8E8E93] dark:text-[#98989D] select-none w-5 text-right">
                {level}
              </span>
            </div>
          ))}
          <div className="flex items-center w-full">
            <div className="flex-1 h-[1px] bg-black/[0.06] dark:bg-white/[0.09]" />
            <span className="pl-2 text-[10px] font-mono text-[#8E8E93] dark:text-[#98989D] select-none w-5 text-right">
              0
            </span>
          </div>
        </div>

        {/* Bar Columns (Sunday - Saturday) */}
        <div className="relative h-28 w-full flex items-end justify-between pr-7 gap-2 z-10">
          {chartData.map((day, idx) => {
            const heightPercent = ceiling > 0 ? Math.min(Math.max((day.amount / ceiling) * 100, day.amount > 0 ? 10 : 0), 100) : 0;
            const isSelected = selectedDay?.dateStr === day.dateStr;
            const hasSpending = day.amount > 0;

            return (
              <div
                key={idx}
                id={`chart-bar-${day.label.toLowerCase()}`}
                onClick={() => setSelectedDay(isSelected ? null : day)}
                className="flex-1 h-full flex flex-col justify-end items-center cursor-pointer group"
              >
                {/* Floating tooltip on selection */}
                <AnimatePresence>
                  {isSelected && (
                    <motion.div
                      initial={{ opacity: 0, y: 6, scale: 0.9 }}
                      animate={{ opacity: 1, y: 0, scale: 1 }}
                      exit={{ opacity: 0, y: 4, scale: 0.9 }}
                      className="absolute -top-7 z-30 whitespace-nowrap rounded-full bg-neutral-900 dark:bg-white px-2.5 py-0.5 text-[11px] font-bold text-white dark:text-black shadow-lg"
                    >
                      {currencySymbol}{day.amount.toFixed(2)}
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* Vertical Bar Pill */}
                <div className="w-full max-w-[16px] h-[85px] flex items-end justify-center">
                  <motion.div
                    initial={{ height: 0 }}
                    animate={{ height: `${heightPercent}%` }}
                    transition={{ type: 'spring', damping: 20, stiffness: 220 }}
                    className={`w-full rounded-t-full transition-colors ${
                      isSelected
                        ? 'bg-[#007AFF] shadow-sm'
                        : hasSpending
                        ? 'bg-neutral-950 dark:bg-white group-hover:bg-neutral-700 dark:group-hover:bg-neutral-200'
                        : 'bg-transparent'
                    }`}
                  />
                </div>

                {/* Day Label below bar */}
                <span
                  className={`mt-2 text-[11px] font-medium transition-colors select-none ${
                    isSelected
                      ? 'text-[#007AFF] font-bold'
                      : hasSpending
                      ? 'text-neutral-800 dark:text-neutral-200'
                      : 'text-[#8E8E93] dark:text-[#98989D]'
                  }`}
                >
                  {day.label}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Selected Day details banner if active */}
      <AnimatePresence>
        {selectedDay && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="mt-3 pt-2.5 border-t border-black/[0.04] dark:border-white/[0.06] flex items-center justify-between text-xs text-[#8E8E93] dark:text-[#98989D]"
          >
            <span>{selectedDay.label}, {selectedDay.dateStr}</span>
            <span className="font-semibold text-neutral-900 dark:text-white">
              {currencySymbol}{selectedDay.amount.toFixed(2)} logged
            </span>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
