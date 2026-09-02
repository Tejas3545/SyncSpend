import React from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { X, Search } from 'lucide-react';
import { SpendingSummary } from '../types';

interface WidgetsModalProps {
  isOpen: boolean;
  onClose: () => void;
  summary: SpendingSummary;
  currencySymbol?: string;
  isNotionConfigured: boolean;
  isGoogleConfigured: boolean;
  onOpenSettings: () => void;
}

export const WidgetsModal: React.FC<WidgetsModalProps> = ({
  isOpen,
  onClose,
  summary,
  currencySymbol = '$',
  isNotionConfigured,
  isGoogleConfigured,
  onOpenSettings,
}) => {
  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-md">
        <motion.div
          initial={{ opacity: 0, scale: 0.92, y: 15 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 10 }}
          className="relative w-full max-w-sm rounded-[36px] bg-[#F2F2F7] dark:bg-[#000000] p-6 shadow-2xl border border-white/20 dark:border-white/10 overflow-hidden"
        >
          {/* Header */}
          <div className="flex items-center justify-between mb-4">
            <div>
              <span className="text-[11px] font-bold uppercase tracking-widest text-neutral-400">
                WIDGETS
              </span>
              <h2 className="text-xl font-bold text-neutral-900 dark:text-white leading-tight">
                Spending Trends at a Glance.
              </h2>
            </div>
            <button
              onClick={onClose}
              className="p-2 rounded-full bg-neutral-200/80 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 hover:opacity-80 transition-opacity"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* 3 Stacked Widget Cards (Matching Screenshot 2) */}
          <div className="space-y-3.5 my-5">
            {/* Card 1: This Week */}
            <motion.div
              whileHover={{ scale: 1.02 }}
              className="liquid-glass-card rounded-[24px] p-5 text-center"
            >
              <span className="text-[12px] font-medium text-[#8E8E93] dark:text-[#98989D]">
                This Week
              </span>
              <p className="text-[32px] font-bold text-neutral-950 dark:text-white tracking-tight mt-0.5">
                {currencySymbol}
                {summary.totalThisWeek.toLocaleString('en-US', {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2,
                })}
              </p>
            </motion.div>

            {/* Card 2: This Month */}
            <motion.div
              whileHover={{ scale: 1.02 }}
              className="liquid-glass-card rounded-[24px] p-5 text-center"
            >
              <span className="text-[12px] font-medium text-[#8E8E93] dark:text-[#98989D]">
                This Month
              </span>
              <p className="text-[32px] font-bold text-neutral-950 dark:text-white tracking-tight mt-0.5">
                {currencySymbol}
                {summary.totalThisMonth.toLocaleString('en-US', {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2,
                })}
              </p>
            </motion.div>

            {/* Card 3: This Year */}
            <motion.div
              whileHover={{ scale: 1.02 }}
              className="liquid-glass-card rounded-[24px] p-5 text-center"
            >
              <span className="text-[12px] font-medium text-[#8E8E93] dark:text-[#98989D]">
                This Year
              </span>
              <p className="text-[32px] font-bold text-neutral-950 dark:text-white tracking-tight mt-0.5">
                {currencySymbol}
                {summary.totalThisYear.toLocaleString('en-US', {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2,
                })}
              </p>
            </motion.div>
          </div>

          {/* Search Pill + Dock (Matching Screenshot 2 bottom) */}
          <div className="flex flex-col items-center gap-3 pt-2">
            <div className="flex items-center gap-1.5 px-4 py-1.5 rounded-full liquid-glass-subtle text-[11px] font-medium text-neutral-600 dark:text-neutral-400">
              <Search className="w-3.5 h-3.5 text-neutral-500" />
              <span>Search</span>
            </div>

            {/* Dock with Notion and Google Sheets icons */}
            <div className="w-full flex items-center justify-center gap-4 py-2.5 px-6 rounded-full bg-neutral-200/50 dark:bg-neutral-900/80 backdrop-blur-md">
              {/* Notion App Icon */}
              <div
                onClick={() => {
                  onClose();
                  onOpenSettings();
                }}
                className="group relative cursor-pointer flex flex-col items-center"
                title="Notion Sync Status"
              >
                <div className="w-12 h-12 rounded-2xl bg-white dark:bg-neutral-800 flex items-center justify-center shadow-md border border-black/10 dark:border-white/10 group-hover:scale-105 transition-transform">
                  <span className="font-serif font-black text-xl text-black dark:text-white">N</span>
                </div>
                <div className="absolute -top-1 -right-1">
                  {isNotionConfigured ? (
                    <span className="w-3.5 h-3.5 rounded-full bg-emerald-500 border-2 border-white flex items-center justify-center" />
                  ) : (
                    <span className="w-3.5 h-3.5 rounded-full bg-neutral-400 border-2 border-white flex items-center justify-center" />
                  )}
                </div>
                <span className="text-[9px] font-medium text-neutral-500 mt-1">Notion</span>
              </div>

              {/* Google Sheets / Spreadsheet App Icon */}
              <div
                onClick={() => {
                  onClose();
                  onOpenSettings();
                }}
                className="group relative cursor-pointer flex flex-col items-center"
                title="Google Sheets Sync Status"
              >
                <div className="w-12 h-12 rounded-2xl bg-emerald-600 dark:bg-emerald-700 flex items-center justify-center shadow-md border border-black/10 group-hover:scale-105 transition-transform">
                  <div className="grid grid-cols-2 gap-0.5 p-1 bg-white/20 rounded-md">
                    <div className="w-2 h-2 bg-white rounded-2xs" />
                    <div className="w-2 h-2 bg-white rounded-2xs" />
                    <div className="w-2 h-2 bg-white rounded-2xs" />
                    <div className="w-2 h-2 bg-white rounded-2xs" />
                  </div>
                </div>
                <div className="absolute -top-1 -right-1">
                  {isGoogleConfigured ? (
                    <span className="w-3.5 h-3.5 rounded-full bg-emerald-400 border-2 border-white flex items-center justify-center" />
                  ) : (
                    <span className="w-3.5 h-3.5 rounded-full bg-neutral-400 border-2 border-white flex items-center justify-center" />
                  )}
                </div>
                <span className="text-[9px] font-medium text-neutral-500 mt-1">Sheets</span>
              </div>
            </div>
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
