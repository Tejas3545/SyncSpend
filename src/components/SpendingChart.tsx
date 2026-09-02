import React, { useState } from 'react';
import { Period } from '../types';

interface ChartDataPoint {
  label: string;
  amount: number;
  dateStr: string;
}

interface SpendingChartProps {
  data: ChartDataPoint[];
  period: Period;
  onPeriodChange: (p: Period) => void;
  currencySymbol?: string;
}

export const SpendingChart: React.FC<SpendingChartProps> = ({
  data,
  period,
  onPeriodChange,
  currencySymbol = '₹',
}) => {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  const maxAmount = Math.max(...data.map((d) => d.amount), 1);
  const totalInChart = data.reduce((acc, curr) => acc + curr.amount, 0);

  const periods: { id: Period; label: string }[] = [
    { id: 'WEEK', label: 'Week' },
    { id: 'MONTH', label: 'Month' },
    { id: 'YEAR', label: 'Year' },
    { id: 'ALL', label: 'All' },
  ];

  return (
    <div
      id="spending-chart-section"
      className="rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] transition-all"
    >
      {/* Top row: Section title + Total in period */}
      <div className="flex items-center justify-between mb-4">
        <span className="text-xs font-medium uppercase tracking-wider text-neutral-500 dark:text-neutral-400">
          Analytics
        </span>
        <div className="text-right">
          <span className="text-xs text-neutral-500 dark:text-neutral-400">
            Period Total: <strong className="text-black dark:text-white">{currencySymbol}{totalInChart.toFixed(2)}</strong>
          </span>
        </div>
      </div>

      {/* Bar Chart Canvas */}
      <div className="relative h-44 w-full flex items-end justify-between gap-1.5 pt-6 pb-2">
        {data.map((item, idx) => {
          const heightPercent = Math.max((item.amount / maxAmount) * 100, 4);
          const isHovered = hoveredIndex === idx;

          return (
            <div
              key={idx}
              className="group relative flex-1 h-full flex flex-col justify-end items-center cursor-pointer"
              onMouseEnter={() => setHoveredIndex(idx)}
              onMouseLeave={() => setHoveredIndex(null)}
              onClick={() => setHoveredIndex(idx)}
            >
              {/* Tooltip on hover */}
              {isHovered && (
                <div className="absolute -top-7 z-10 whitespace-nowrap rounded-md bg-black px-2 py-1 text-[11px] font-semibold text-white shadow-md dark:bg-white dark:text-black">
                  {currencySymbol}{item.amount.toFixed(2)}
                </div>
              )}

              {/* Bar */}
              <div
                style={{ height: `${heightPercent}%` }}
                className={`w-full max-w-[28px] rounded-t-sm transition-all duration-200 ${
                  isHovered
                    ? 'bg-[#007AFF]'
                    : 'bg-black hover:bg-neutral-800 dark:bg-white dark:hover:bg-neutral-200'
                } ${item.amount === 0 ? 'opacity-20' : 'opacity-100'}`}
              />

              {/* Bottom label */}
              <span className="mt-2 block truncate text-[11px] font-medium text-neutral-500 dark:text-neutral-400">
                {item.label}
              </span>
            </div>
          );
        })}
      </div>

      {/* Period Selector Tabs (Week | Month | Year | All) */}
      <div className="mt-4 flex items-center justify-center gap-1 rounded-xl bg-neutral-200/60 p-1 dark:bg-[#2C2C2E]">
        {periods.map((p) => {
          const isSelected = period === p.id;
          return (
            <button
              key={p.id}
              id={`tab-period-${p.id.toLowerCase()}`}
              onClick={() => onPeriodChange(p.id)}
              className={`flex-1 rounded-lg py-1.5 text-xs font-semibold transition-all ${
                isSelected
                  ? 'bg-black text-white shadow-xs dark:bg-white dark:text-black'
                  : 'text-neutral-600 hover:text-black dark:text-neutral-400 dark:hover:text-white'
              }`}
            >
              {p.label}
            </button>
          );
        })}
      </div>
    </div>
  );
};
