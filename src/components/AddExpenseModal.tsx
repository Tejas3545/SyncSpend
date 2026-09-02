import React, { useState, useEffect } from 'react';
import { Category, PaymentMethod, Expense } from '../types';
import { StorageService } from '../services/storageService';
import { toLocalDateString } from '../utils/dateUtils';
import { Calendar, Delete, Sparkles } from 'lucide-react';

interface AddExpenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (expense: Omit<Expense, 'id' | 'createdAt'>) => void;
  categories: Category[];
  paymentMethods: PaymentMethod[];
  currencySymbol?: string;
  isNotionConfigured?: boolean;
}

export const AddExpenseModal: React.FC<AddExpenseModalProps> = ({
  isOpen,
  onClose,
  onSave,
  categories,
  paymentMethods,
  currencySymbol = '₹',
  isNotionConfigured = false,
}) => {
  const [amountStr, setAmountStr] = useState<string>('0');
  const [name, setName] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<PaymentMethod | null>(null);
  const [date, setDate] = useState<string>(toLocalDateString(new Date()));
  const [suggestions, setSuggestions] = useState<string[]>([]);

  // Initialize defaults on open
  useEffect(() => {
    if (isOpen) {
      setAmountStr('0');
      setName('');
      setDate(toLocalDateString(new Date()));
      if (categories.length > 0) {
        setSelectedCategory(categories[0]);
      }
      if (paymentMethods.length > 0) {
        setSelectedPaymentMethod(paymentMethods[0]);
      }
      setSuggestions(StorageService.getSuggestions(''));
    }
  }, [isOpen, categories, paymentMethods]);

  // Update suggestions when typing name
  useEffect(() => {
    setSuggestions(StorageService.getSuggestions(name));
  }, [name]);

  if (!isOpen) return null;

  // Custom numeric keypad handler
  const handleKeypadPress = (val: string) => {
    if (val === 'backspace') {
      if (amountStr.length <= 1) {
        setAmountStr('0');
      } else {
        setAmountStr(amountStr.slice(0, -1));
      }
      return;
    }

    if (val === '.') {
      if (amountStr.includes('.')) return;
      setAmountStr(amountStr + '.');
      return;
    }

    // Numbers 0-9
    if (amountStr === '0') {
      setAmountStr(val);
    } else {
      // Limit decimal places to 2
      const parts = amountStr.split('.');
      if (parts[1] && parts[1].length >= 2) return;
      if (amountStr.length >= 8) return; // Prevent overflow
      setAmountStr(amountStr + val);
    }
  };

  const parsedAmount = parseFloat(amountStr) || 0;
  const isSaveEnabled = parsedAmount > 0 && name.trim().length > 0;

  const handleSave = () => {
    if (!isSaveEnabled || !selectedCategory) return;

    onSave({
      name: name.trim(),
      amount: parsedAmount,
      categoryId: selectedCategory.id,
      paymentMethodId: selectedPaymentMethod?.id || null,
      date: date,
      isSynced: false,
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/60 backdrop-blur-xs p-0 sm:p-4">
      <div
        id="add-expense-modal"
        className="relative w-full max-w-lg overflow-hidden rounded-t-3xl sm:rounded-3xl bg-white shadow-2xl dark:bg-[#121212] flex flex-col max-h-[92vh]"
      >
        {/* Top Navigation Bar: Cancel (Blue) & Save (Blue when active) */}
        <div className="flex items-center justify-between border-b border-neutral-100 px-5 py-3.5 dark:border-neutral-800">
          <button
            id="btn-cancel-expense"
            onClick={onClose}
            className="text-[15px] font-semibold text-[#007AFF] hover:opacity-80 transition-opacity"
          >
            Cancel
          </button>
          <h2 className="text-[16px] font-bold text-black dark:text-white">
            Add Expense
          </h2>
          <button
            id="btn-save-expense"
            onClick={handleSave}
            disabled={!isSaveEnabled}
            className={`text-[15px] font-semibold transition-opacity ${
              isSaveEnabled
                ? 'text-[#007AFF] cursor-pointer hover:opacity-80'
                : 'text-neutral-400 dark:text-neutral-600 cursor-not-allowed'
            }`}
          >
            Save
          </button>
        </div>

        {/* Scrollable Form Content */}
        <div className="flex-1 overflow-y-auto px-5 py-4 space-y-4">
          {/* Amount Display Card */}
          <div className="rounded-2xl bg-[#F5F5F5] p-5 text-center dark:bg-[#1C1C1E]">
            <p className="text-xs font-medium text-neutral-500 dark:text-neutral-400 uppercase tracking-wider mb-1">
              Amount
            </p>
            <div className="text-4xl sm:text-5xl font-bold tracking-tight text-black dark:text-white flex items-center justify-center gap-0.5">
              <span>{currencySymbol}</span>
              <span>{amountStr}</span>
            </div>
          </div>

          {/* Expense Name Input */}
          <div>
            <label className="block text-xs font-medium text-neutral-500 dark:text-neutral-400 mb-1.5 uppercase tracking-wider">
              Expense Name
            </label>
            <input
              id="input-expense-name"
              type="text"
              placeholder="e.g. Spotify, Lunch, Uber"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-xl border border-neutral-200 bg-transparent px-4 py-3 text-[15px] text-black outline-none placeholder:text-neutral-400 focus:border-black dark:border-neutral-800 dark:text-white dark:focus:border-white"
            />
          </div>

          {/* Smart Autocomplete Suggestions */}
          {suggestions.length > 0 && (
            <div>
              <div className="flex items-center gap-1 text-[11px] font-medium text-neutral-400 dark:text-neutral-500 mb-1.5">
                <Sparkles className="h-3 w-3" />
                <span>Suggestions</span>
              </div>
              <div className="flex flex-wrap gap-1.5">
                {suggestions.map((sug, i) => (
                  <button
                    key={i}
                    id={`suggestion-${i}`}
                    type="button"
                    onClick={() => setName(sug)}
                    className="rounded-full bg-[#F5F5F5] px-3 py-1 text-xs font-medium text-neutral-700 hover:bg-neutral-200 dark:bg-[#2C2C2E] dark:text-neutral-200 dark:hover:bg-[#3A3A3C] transition-colors"
                  >
                    {sug}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Category Selector Chips */}
          <div>
            <label className="block text-xs font-medium text-neutral-500 dark:text-neutral-400 mb-1.5 uppercase tracking-wider">
              Category
            </label>
            <div className="flex flex-wrap gap-2">
              {categories.map((cat) => {
                const isSelected = selectedCategory?.id === cat.id;
                return (
                  <button
                    key={cat.id}
                    id={`chip-cat-${cat.id}`}
                    type="button"
                    onClick={() => setSelectedCategory(cat)}
                    className={`flex items-center gap-1.5 rounded-full px-3.5 py-1.5 text-xs font-medium transition-all ${
                      isSelected
                        ? 'bg-black text-white dark:bg-white dark:text-black shadow-xs'
                        : 'border border-neutral-300 text-neutral-700 hover:border-black dark:border-neutral-700 dark:text-neutral-300 dark:hover:border-white'
                    }`}
                  >
                    <span>{cat.emoji}</span>
                    <span>{cat.name}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Payment Method Selector Chips */}
          <div>
            <label className="block text-xs font-medium text-neutral-500 dark:text-neutral-400 mb-1.5 uppercase tracking-wider">
              Payment Method
            </label>
            <div className="flex flex-wrap gap-2">
              {paymentMethods.map((pm) => {
                const isSelected = selectedPaymentMethod?.id === pm.id;
                return (
                  <button
                    key={pm.id}
                    id={`chip-pm-${pm.id}`}
                    type="button"
                    onClick={() => setSelectedPaymentMethod(pm)}
                    className={`rounded-full px-3.5 py-1.5 text-xs font-medium transition-all ${
                      isSelected
                        ? 'bg-black text-white dark:bg-white dark:text-black shadow-xs'
                        : 'border border-neutral-300 text-neutral-700 hover:border-black dark:border-neutral-700 dark:text-neutral-300 dark:hover:border-white'
                    }`}
                  >
                    {pm.name}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Date Selector */}
          <div>
            <label className="block text-xs font-medium text-neutral-500 dark:text-neutral-400 mb-1.5 uppercase tracking-wider">
              Date
            </label>
            <div className="flex items-center gap-2 rounded-xl border border-neutral-200 bg-transparent px-3 py-2.5 dark:border-neutral-800">
              <Calendar className="h-4 w-4 text-neutral-500" />
              <input
                id="input-expense-date"
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="bg-transparent text-[14px] text-black outline-none dark:text-white w-full"
              />
            </div>
          </div>

          {/* Custom Numeric Keypad */}
          <div className="pt-2">
            <div className="grid grid-cols-3 gap-2">
              {['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0'].map((val) => (
                <button
                  key={val}
                  type="button"
                  id={`keypad-${val}`}
                  onClick={() => handleKeypadPress(val)}
                  className="flex h-12 items-center justify-center rounded-xl bg-[#F5F5F5] text-lg font-semibold text-black hover:bg-neutral-200 active:scale-95 transition-all dark:bg-[#1C1C1E] dark:text-white dark:hover:bg-[#2C2C2E]"
                >
                  {val}
                </button>
              ))}
              {/* Backspace Key */}
              <button
                type="button"
                id="keypad-backspace"
                onClick={() => handleKeypadPress('backspace')}
                className="flex h-12 items-center justify-center rounded-xl bg-[#F5F5F5] text-neutral-700 hover:bg-neutral-200 active:scale-95 transition-all dark:bg-[#1C1C1E] dark:text-neutral-300 dark:hover:bg-[#2C2C2E]"
              >
                <Delete className="h-5 w-5" />
              </button>
            </div>
          </div>

          {/* Notion Sync Notice */}
          {isNotionConfigured && (
            <div className="flex items-center justify-center gap-2 pt-2 text-xs text-neutral-500 dark:text-neutral-400">
              <span className="h-2 w-2 rounded-full bg-emerald-500"></span>
              <span>Will sync automatically to Notion</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
