import React, { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ChevronDown, Check, Plus } from 'lucide-react';

interface AccountSwitcherProps {
  currentAccount: string;
  accounts: string[];
  onSelectAccount: (account: string) => void;
  onAddAccount: (name: string) => void;
}

export const AccountSwitcher: React.FC<AccountSwitcherProps> = ({
  currentAccount,
  accounts,
  onSelectAccount,
  onAddAccount,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isAdding, setIsAdding] = useState(false);
  const [newAccountName, setNewAccountName] = useState('');
  const menuRef = useRef<HTMLDivElement>(null);

  // Close on click outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        setIsAdding(false);
      }
    };
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpen]);

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (newAccountName.trim()) {
      onAddAccount(newAccountName.trim());
      setNewAccountName('');
      setIsAdding(false);
      setIsOpen(false);
    }
  };

  return (
    <div className="relative inline-block" ref={menuRef}>
      {/* Account pill button matching Screenshot 1 */}
      <button
        id="btn-account-switcher"
        onClick={() => setIsOpen(!isOpen)}
        className="liquid-glass inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[13px] font-semibold text-neutral-800 dark:text-neutral-100 transition-all hover:opacity-90 active:scale-95 shadow-xs"
      >
        <span>{currentAccount}</span>
        <ChevronDown className={`w-3.5 h-3.5 text-neutral-500 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`} />
      </button>

      {/* Liquid Dropdown Popover */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 8, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 6, scale: 0.95 }}
            transition={{ type: 'spring', damping: 25, stiffness: 350 }}
            className="absolute left-0 top-full mt-2 w-52 z-50 rounded-2xl liquid-glass p-1.5 shadow-2xl border border-white/60 dark:border-white/10"
          >
            <div className="px-3 py-1.5 text-[11px] font-medium text-neutral-400 dark:text-neutral-500 uppercase tracking-wider">
              Accounts
            </div>

            <div className="space-y-0.5">
              {accounts.map((acc) => {
                const isSelected = acc === currentAccount;
                return (
                  <button
                    key={acc}
                    id={`account-option-${acc.toLowerCase().replace(/\s+/g, '-')}`}
                    onClick={() => {
                      onSelectAccount(acc);
                      setIsOpen(false);
                    }}
                    className={`w-full flex items-center justify-between px-3 py-2 text-[13px] font-medium rounded-xl transition-colors ${
                      isSelected
                        ? 'bg-neutral-900 text-white dark:bg-white dark:text-neutral-950 font-semibold'
                        : 'text-neutral-700 dark:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                  >
                    <span>{acc}</span>
                    {isSelected && <Check className="w-3.5 h-3.5" />}
                  </button>
                );
              })}
            </div>

            {/* Add account option */}
            <div className="mt-1 pt-1 border-t border-neutral-200/50 dark:border-neutral-700/50">
              {isAdding ? (
                <form onSubmit={handleCreate} className="p-1.5">
                  <input
                    type="text"
                    autoFocus
                    placeholder="Account name..."
                    value={newAccountName}
                    onChange={(e) => setNewAccountName(e.target.value)}
                    className="w-full text-xs px-2.5 py-1.5 rounded-lg bg-neutral-100 dark:bg-neutral-800 border-none outline-hidden text-neutral-900 dark:text-white"
                  />
                  <div className="flex gap-1 mt-1.5 justify-end">
                    <button
                      type="button"
                      onClick={() => setIsAdding(false)}
                      className="px-2 py-1 text-[11px] text-neutral-500 hover:text-neutral-800"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      disabled={!newAccountName.trim()}
                      className="px-2.5 py-1 text-[11px] font-semibold bg-[#007AFF] text-white rounded-md disabled:opacity-50"
                    >
                      Add
                    </button>
                  </div>
                </form>
              ) : (
                <button
                  onClick={() => setIsAdding(true)}
                  className="w-full flex items-center gap-2 px-3 py-2 text-[12px] text-neutral-500 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white rounded-xl hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>Add New Account</span>
                </button>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
