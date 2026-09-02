import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Category, PaymentMethod, Expense } from '../types';
import { StorageService } from '../services/storageService';
import { toLocalDateString } from '../utils/dateUtils';
import { CategoryIcon } from './CategoryIcon';
import { ChevronRight, Sparkles, Check } from 'lucide-react';

interface AddExpenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (expense: Omit<Expense, 'id' | 'createdAt'>) => void;
  categories: Category[];
  paymentMethods: PaymentMethod[];
  currencySymbol?: string;
  account: string;
}

export const AddExpenseModal: React.FC<AddExpenseModalProps> = ({
  isOpen,
  onClose,
  onSave,
  categories,
  paymentMethods,
  currencySymbol = '$',
  account,
}) => {
  const [name, setName] = useState('');
  const [amountStr, setAmountStr] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedPayment, setSelectedPayment] = useState<PaymentMethod | null>(null);
  const [date, setDate] = useState(toLocalDateString(new Date()));
  const [smartSuggestion, setSmartSuggestion] = useState<Expense | null>(null);

  // Sub-sheets for Category and Payment selection
  const [isCategoryPickerOpen, setIsCategoryPickerOpen] = useState(false);
  const [isPaymentPickerOpen, setIsPaymentPickerOpen] = useState(false);

  // Reset when opened
  useEffect(() => {
    if (isOpen) {
      setName('');
      setAmountStr('');
      setDate(toLocalDateString(new Date()));
      setSelectedCategory(categories[0] || null);
      setSelectedPayment(paymentMethods[0] || null);
      setSmartSuggestion(null);
    }
  }, [isOpen, categories, paymentMethods]);

  // Query smart suggestions as user types (Matching Screenshot 5)
  useEffect(() => {
    if (name.trim().length >= 1) {
      const match = StorageService.getSmartSuggestion(name.trim());
      setSmartSuggestion(match);
    } else {
      setSmartSuggestion(null);
    }
  }, [name]);

  if (!isOpen) return null;

  // Auto-fill from smart suggestion (Matching Screenshot 5)
  const applySmartSuggestion = (suggestion: Expense) => {
    setName(suggestion.name);
    setAmountStr(suggestion.amount.toFixed(2));
    const cat = categories.find((c) => c.id === suggestion.categoryId);
    if (cat) setSelectedCategory(cat);
    const pm = paymentMethods.find((p) => p.id === suggestion.paymentMethodId);
    if (pm) setSelectedPayment(pm);
    setSmartSuggestion(null);
  };

  const parsedAmount = parseFloat(amountStr) || 0;
  const isValid = name.trim().length > 0 && parsedAmount > 0;

  const handleSave = () => {
    if (!isValid || !selectedCategory) return;
    onSave({
      name: name.trim(),
      amount: parsedAmount,
      categoryId: selectedCategory.id,
      paymentMethodId: selectedPayment?.id || null,
      account: account || 'Personal',
      date,
      isSynced: false,
    });
    onClose();
  };

  // Format date display pill
  const formatDatePill = (dateStr: string) => {
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

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/60 backdrop-blur-md">
        <motion.div
          initial={{ opacity: 0, scale: 0.94, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.94, y: 15 }}
          className="relative w-full max-w-sm rounded-[36px] bg-[#F2F2F7] dark:bg-[#121214] p-5 shadow-2xl border border-white/40 dark:border-white/10 overflow-hidden"
        >
          {/* Top Bar (Matching Screenshot 5: Cancel, New Expense, Save) */}
          <div className="flex items-center justify-between pb-3 border-b border-neutral-200/60 dark:border-neutral-800">
            <button
              onClick={onClose}
              className="text-[15px] font-medium text-[#007AFF] hover:opacity-80 transition-opacity"
            >
              Cancel
            </button>
            <h2 className="text-[16px] font-bold text-neutral-900 dark:text-white">
              New Expense
            </h2>
            <button
              onClick={handleSave}
              disabled={!isValid}
              className="text-[15px] font-bold text-[#007AFF] disabled:opacity-30 hover:opacity-80 transition-opacity"
            >
              Save
            </button>
          </div>

          {/* Form Container */}
          <div className="mt-4 space-y-3">
            {/* Amount input block */}
            <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 text-center shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
              <span className="text-[11px] font-medium text-neutral-400 uppercase tracking-wider">
                Amount
              </span>
              <div className="flex items-center justify-center gap-1 mt-1">
                <span className="text-2xl font-bold text-neutral-400">{currencySymbol}</span>
                <input
                  type="number"
                  step="0.01"
                  autoFocus
                  placeholder="0.00"
                  value={amountStr}
                  onChange={(e) => setAmountStr(e.target.value)}
                  className="w-44 text-3xl font-extrabold text-neutral-900 dark:text-white text-center bg-transparent border-none outline-hidden tracking-tight"
                />
              </div>
            </div>

            {/* Name Input & Smart Suggestion (Matching Screenshot 5) */}
            <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-3.5 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
              <div className="flex items-center justify-between">
                <input
                  type="text"
                  placeholder="Title (e.g. Spotify, Groceries...)"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full text-[16px] font-medium text-neutral-900 dark:text-white bg-transparent border-none outline-hidden placeholder:text-neutral-400"
                />
              </div>

              {/* SMART SUGGESTION CARD (Exact match to Screenshot 5) */}
              <AnimatePresence>
                {smartSuggestion && (
                  <motion.div
                    initial={{ opacity: 0, y: -6 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -6 }}
                    onClick={() => applySmartSuggestion(smartSuggestion)}
                    className="mt-3 p-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800/90 border border-neutral-200/60 dark:border-neutral-700/60 cursor-pointer hover:bg-neutral-200/70 transition-colors"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <Sparkles className="w-3.5 h-3.5 text-[#007AFF]" />
                        <span className="text-[14px] font-bold text-neutral-900 dark:text-white">
                          {smartSuggestion.name}
                        </span>
                      </div>
                      <span className="text-[10px] uppercase font-bold text-[#007AFF] bg-[#007AFF]/10 px-2 py-0.5 rounded-full">
                        Auto-Fill
                      </span>
                    </div>
                    <div className="mt-1 text-[12px] text-neutral-500 dark:text-neutral-400">
                      {currencySymbol}{smartSuggestion.amount.toFixed(2)} • {categories.find((c) => c.id === smartSuggestion.categoryId)?.name || 'General'}
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* Field Rows (Category, Payment, Date - Matching Screenshot 5) */}
            <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] divide-y divide-neutral-100 dark:divide-neutral-800 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
              {/* Category Row */}
              <div
                onClick={() => setIsCategoryPickerOpen(true)}
                className="flex items-center justify-between px-4 py-3 cursor-pointer hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
              >
                <span className="text-[14px] font-medium text-neutral-800 dark:text-neutral-200">
                  Category
                </span>
                <div className="flex items-center gap-2 text-neutral-600 dark:text-neutral-300">
                  {selectedCategory && (
                    <div className="w-6 h-6 rounded-md bg-[#EFEFF4] dark:bg-[#2C2C2E] flex items-center justify-center text-neutral-800 dark:text-neutral-200 shrink-0">
                      <CategoryIcon iconName={selectedCategory.iconName} className="w-3.5 h-3.5" />
                    </div>
                  )}
                  <span className="text-[13px] font-medium">
                    {selectedCategory ? selectedCategory.name : 'None'}
                  </span>
                  <ChevronRight className="w-4 h-4 text-neutral-400" />
                </div>
              </div>

              {/* Payment Row */}
              <div
                onClick={() => setIsPaymentPickerOpen(true)}
                className="flex items-center justify-between px-4 py-3 cursor-pointer hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors"
              >
                <span className="text-[14px] font-medium text-neutral-800 dark:text-neutral-200">
                  Payment
                </span>
                <div className="flex items-center gap-1.5 text-neutral-500 dark:text-neutral-400">
                  <span className="text-[13px] font-medium">
                    {selectedPayment ? selectedPayment.name : 'None'}
                  </span>
                  <ChevronRight className="w-4 h-4 text-neutral-400" />
                </div>
              </div>

              {/* Date Row (Matching Screenshot 5 pill) */}
              <div className="flex items-center justify-between px-4 py-3">
                <span className="text-[14px] font-medium text-neutral-800 dark:text-neutral-200">
                  Date
                </span>
                <div className="relative">
                  <input
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    className="opacity-0 absolute inset-0 w-full h-full cursor-pointer z-10"
                  />
                  <div className="px-3 py-1 rounded-full bg-neutral-100 dark:bg-neutral-800 text-[12px] font-semibold text-neutral-800 dark:text-neutral-200 border border-black/[0.04]">
                    {formatDatePill(date)}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Category Picker Sheet Modal */}
          <AnimatePresence>
            {isCategoryPickerOpen && (
              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: 30 }}
                className="absolute inset-x-0 bottom-0 top-14 bg-white dark:bg-[#1C1C1E] z-20 rounded-t-[32px] p-5 shadow-2xl flex flex-col"
              >
                <div className="flex items-center justify-between pb-3 border-b border-neutral-100 dark:border-neutral-800">
                  <h3 className="text-sm font-bold text-neutral-900 dark:text-white">Select Category</h3>
                  <button
                    onClick={() => setIsCategoryPickerOpen(false)}
                    className="text-xs font-semibold text-[#007AFF]"
                  >
                    Done
                  </button>
                </div>
                <div className="mt-3 overflow-y-auto space-y-1 flex-1 no-scrollbar">
                  {categories.map((c) => (
                    <div
                      key={c.id}
                      onClick={() => {
                        setSelectedCategory(c);
                        setIsCategoryPickerOpen(false);
                      }}
                      className="flex items-center justify-between p-2.5 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 cursor-pointer transition-colors"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-[8px] bg-[#EFEFF4] dark:bg-[#2C2C2E] flex items-center justify-center text-neutral-800 dark:text-neutral-200 shrink-0">
                          <CategoryIcon iconName={c.iconName} className="w-4 h-4" />
                        </div>
                        <span className="text-[14px] font-medium text-neutral-800 dark:text-neutral-200">
                          {c.name}
                        </span>
                      </div>
                      {selectedCategory?.id === c.id && <Check className="w-4 h-4 text-[#007AFF]" />}
                    </div>
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          {/* Payment Method Picker Sheet Modal */}
          <AnimatePresence>
            {isPaymentPickerOpen && (
              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: 30 }}
                className="absolute inset-x-0 bottom-0 top-20 bg-white dark:bg-[#1C1C1E] z-20 rounded-t-[32px] p-5 shadow-2xl flex flex-col"
              >
                <div className="flex items-center justify-between pb-3 border-b border-neutral-100 dark:border-neutral-800">
                  <h3 className="text-sm font-bold text-neutral-900 dark:text-white">Select Payment Method</h3>
                  <button
                    onClick={() => setIsPaymentPickerOpen(false)}
                    className="text-xs font-semibold text-[#007AFF]"
                  >
                    Done
                  </button>
                </div>
                <div className="mt-3 overflow-y-auto space-y-1 flex-1 no-scrollbar">
                  {paymentMethods.map((pm) => (
                    <div
                      key={pm.id}
                      onClick={() => {
                        setSelectedPayment(pm);
                        setIsPaymentPickerOpen(false);
                      }}
                      className="flex items-center justify-between p-3 rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 cursor-pointer"
                    >
                      <span className="text-[14px] font-medium text-neutral-800 dark:text-neutral-200">
                        {pm.name}
                      </span>
                      {selectedPayment?.id === pm.id && <Check className="w-4 h-4 text-[#007AFF]" />}
                    </div>
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
