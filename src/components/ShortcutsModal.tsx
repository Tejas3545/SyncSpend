import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { X, Delete } from 'lucide-react';
import { Expense } from '../types';
import { toLocalDateString } from '../utils/dateUtils';

interface ShortcutsModalProps {
  isOpen: boolean;
  onClose: () => void;
  onQuickSave: (expense: Omit<Expense, 'id' | 'createdAt'>) => void;
  currencySymbol?: string;
  defaultCategoryId: string;
  defaultPaymentMethodId?: string;
  account: string;
}

export const ShortcutsModal: React.FC<ShortcutsModalProps> = ({
  isOpen,
  onClose,
  onQuickSave,
  currencySymbol = '$',
  defaultCategoryId,
  defaultPaymentMethodId,
  account,
}) => {
  const [amountStr, setAmountStr] = useState('16.99');
  const [expenseTitle, setExpenseTitle] = useState('Quick Spend');

  if (!isOpen) return null;

  const handleKeypad = (val: string) => {
    if (val === 'backspace') {
      if (amountStr.length <= 1) {
        setAmountStr('');
      } else {
        setAmountStr(amountStr.slice(0, -1));
      }
      return;
    }

    if (val === '.') {
      if (amountStr.includes('.')) return;
      setAmountStr((amountStr || '0') + '.');
      return;
    }

    // Append digit
    const parts = amountStr.split('.');
    if (parts[1] && parts[1].length >= 2) return;
    if (amountStr.length >= 8) return;

    if (amountStr === '0') {
      setAmountStr(val);
    } else {
      setAmountStr(amountStr + val);
    }
  };

  const handleDone = () => {
    const parsed = parseFloat(amountStr);
    if (!parsed || parsed <= 0) return;

    onQuickSave({
      name: expenseTitle.trim() || 'Quick Expense',
      amount: parsed,
      categoryId: defaultCategoryId,
      paymentMethodId: defaultPaymentMethodId || null,
      account: account || 'Personal',
      date: toLocalDateString(new Date()),
      isSynced: false,
    });
    onClose();
  };

  const keyRows = [
    [
      { num: '1', sub: '' },
      { num: '2', sub: 'ABC' },
      { num: '3', sub: 'DEF' },
    ],
    [
      { num: '4', sub: 'GHI' },
      { num: '5', sub: 'JKL' },
      { num: '6', sub: 'MNO' },
    ],
    [
      { num: '7', sub: 'PQRS' },
      { num: '8', sub: 'TUV' },
      { num: '9', sub: 'WXYZ' },
    ],
    [
      { num: '.', sub: '' },
      { num: '0', sub: '' },
      { num: 'backspace', sub: '' },
    ],
  ];

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex flex-col justify-between p-4 bg-black/70 backdrop-blur-md">
        {/* Top Header */}
        <div className="pt-8 text-center">
          <span className="text-[11px] font-bold uppercase tracking-widest text-neutral-400">
            SHORTCUTS
          </span>
          <h2 className="text-xl font-bold text-white mt-0.5">
            Log Expenses from Anywhere.
          </h2>
        </div>

        {/* Center Floating Dialog (Exact match to Screenshot 3) */}
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: -10 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95 }}
          className="mx-auto w-full max-w-xs rounded-[28px] liquid-glass-card p-5 text-center"
        >
          <div className="mb-2">
            <p className="text-[13px] font-medium text-[#8E8E93] dark:text-[#98989D]">
              What is the amount?
            </p>
            <input
              type="text"
              value={expenseTitle}
              onChange={(e) => setExpenseTitle(e.target.value)}
              placeholder="Expense title"
              className="mt-1 text-center text-xs text-neutral-600 dark:text-neutral-400 bg-transparent border-none outline-hidden underline decoration-dotted font-medium"
            />
          </div>

          {/* Amount Display with Clear Button */}
          <div className="relative mb-3 flex items-center justify-between rounded-2xl bg-white/80 dark:bg-black/40 px-4 py-2.5 shadow-inner border border-black/[0.04] dark:border-white/[0.06]">
            <span className="text-lg font-bold text-neutral-400">{currencySymbol}</span>
            <span className="text-2xl font-bold text-neutral-900 dark:text-white tracking-tight">
              {amountStr || '0'}
            </span>
            {amountStr ? (
              <button
                onClick={() => setAmountStr('')}
                className="w-5 h-5 rounded-full bg-neutral-200 dark:bg-neutral-700 flex items-center justify-center text-neutral-500 hover:text-black"
              >
                <X className="w-3 h-3" />
              </button>
            ) : (
              <div className="w-5" />
            )}
          </div>

          {/* Action Buttons: Cancel and Done (iOS Blue) */}
          <div className="grid grid-cols-2 gap-2">
            <button
              onClick={onClose}
              className="py-2.5 rounded-xl bg-black/[0.05] dark:bg-white/[0.1] text-[14px] font-semibold text-neutral-800 dark:text-neutral-200 hover:opacity-90 active:scale-95 transition-all"
            >
              Cancel
            </button>
            <button
              onClick={handleDone}
              disabled={!amountStr || parseFloat(amountStr) <= 0}
              className="py-2.5 rounded-xl bg-[#007AFF] text-[14px] font-semibold text-white hover:bg-[#006ee6] active:scale-95 transition-all disabled:opacity-40 shadow-sm"
            >
              Done
            </button>
          </div>
        </motion.div>

        {/* Bottom Simulated Keypad (Matching Screenshot 3) */}
        <div className="mx-auto w-full max-w-xs pb-4">
          <div className="rounded-[28px] liquid-glass p-2">
            <div className="grid grid-cols-3 gap-1.5">
              {keyRows.flat().map((k, idx) => (
                <button
                  key={idx}
                  onClick={() => handleKeypad(k.num)}
                  className="h-12 rounded-xl bg-white/70 dark:bg-neutral-800/80 shadow-2xs hover:bg-white dark:hover:bg-neutral-700 active:scale-95 transition-all flex flex-col items-center justify-center"
                >
                  {k.num === 'backspace' ? (
                    <Delete className="w-5 h-5 text-neutral-700 dark:text-neutral-300" />
                  ) : (
                    <>
                      <span className="text-lg font-semibold text-neutral-900 dark:text-white leading-none">
                        {k.num}
                      </span>
                      {k.sub && (
                        <span className="text-[8px] font-bold text-neutral-400 dark:text-neutral-500 tracking-wider">
                          {k.sub}
                        </span>
                      )}
                    </>
                  )}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </AnimatePresence>
  );
};
