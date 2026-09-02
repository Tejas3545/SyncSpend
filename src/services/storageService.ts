import { Category, PaymentMethod, Expense, Settings } from '../types';
import { DEFAULT_CATEGORIES, DEFAULT_PAYMENT_METHODS, getSampleExpenses } from '../data/initialData';

const STORAGE_KEYS = {
  EXPENSES: 'spendsync_expenses_v1',
  CATEGORIES: 'spendsync_categories_v1',
  PAYMENT_METHODS: 'spendsync_payment_methods_v1',
  SETTINGS: 'spendsync_settings_v1',
};

export const StorageService = {
  getExpenses(): Expense[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.EXPENSES);
      if (!data) {
        const initial = getSampleExpenses();
        localStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(initial));
        return initial;
      }
      return JSON.parse(data);
    } catch (e) {
      console.error('Error reading expenses from storage', e);
      return getSampleExpenses();
    }
  },

  saveExpenses(expenses: Expense[]): void {
    try {
      localStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(expenses));
    } catch (e) {
      console.error('Error saving expenses to storage', e);
    }
  },

  addExpense(expense: Omit<Expense, 'id' | 'createdAt'>): Expense {
    const expenses = this.getExpenses();
    const newExpense: Expense = {
      ...expense,
      id: 'exp-' + Date.now() + '-' + Math.random().toString(36).substring(2, 6),
      createdAt: Date.now(),
    };
    expenses.unshift(newExpense);
    this.saveExpenses(expenses);
    return newExpense;
  },

  deleteExpense(id: string): Expense | null {
    const expenses = this.getExpenses();
    const index = expenses.findIndex((e) => e.id === id);
    if (index === -1) return null;
    const [deleted] = expenses.splice(index, 1);
    this.saveExpenses(expenses);
    return deleted;
  },

  updateExpense(id: string, updates: Partial<Expense>): Expense | null {
    const expenses = this.getExpenses();
    const index = expenses.findIndex((e) => e.id === id);
    if (index === -1) return null;
    expenses[index] = { ...expenses[index], ...updates };
    this.saveExpenses(expenses);
    return expenses[index];
  },

  getCategories(): Category[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.CATEGORIES);
      if (!data) {
        localStorage.setItem(STORAGE_KEYS.CATEGORIES, JSON.stringify(DEFAULT_CATEGORIES));
        return DEFAULT_CATEGORIES;
      }
      return JSON.parse(data);
    } catch (e) {
      console.error('Error reading categories from storage', e);
      return DEFAULT_CATEGORIES;
    }
  },

  saveCategories(categories: Category[]): void {
    try {
      localStorage.setItem(STORAGE_KEYS.CATEGORIES, JSON.stringify(categories));
    } catch (e) {
      console.error('Error saving categories to storage', e);
    }
  },

  addCategory(name: string, emoji: string): Category {
    const categories = this.getCategories();
    const newCategory: Category = {
      id: 'cat-' + Date.now(),
      name: name.trim(),
      emoji: emoji.trim() || '🏷️',
      isDefault: false,
    };
    categories.push(newCategory);
    this.saveCategories(categories);
    return newCategory;
  },

  deleteCategory(id: string): boolean {
    const categories = this.getCategories();
    const filtered = categories.filter((c) => c.id !== id);
    if (filtered.length === categories.length) return false;
    this.saveCategories(filtered);
    return true;
  },

  getPaymentMethods(): PaymentMethod[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.PAYMENT_METHODS);
      if (!data) {
        localStorage.setItem(STORAGE_KEYS.PAYMENT_METHODS, JSON.stringify(DEFAULT_PAYMENT_METHODS));
        return DEFAULT_PAYMENT_METHODS;
      }
      return JSON.parse(data);
    } catch (e) {
      console.error('Error reading payment methods from storage', e);
      return DEFAULT_PAYMENT_METHODS;
    }
  },

  savePaymentMethods(methods: PaymentMethod[]): void {
    try {
      localStorage.setItem(STORAGE_KEYS.PAYMENT_METHODS, JSON.stringify(methods));
    } catch (e) {
      console.error('Error saving payment methods to storage', e);
    }
  },

  addPaymentMethod(name: string): PaymentMethod {
    const methods = this.getPaymentMethods();
    const newMethod: PaymentMethod = {
      id: 'pm-' + Date.now(),
      name: name.trim(),
      isDefault: false,
    };
    methods.push(newMethod);
    this.savePaymentMethods(methods);
    return newMethod;
  },

  deletePaymentMethod(id: string): boolean {
    const methods = this.getPaymentMethods();
    const filtered = methods.filter((m) => m.id !== id);
    if (filtered.length === methods.length) return false;
    this.savePaymentMethods(filtered);
    return true;
  },

  getSettings(): Settings {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.SETTINGS);
      if (!data) {
        const defaultSettings: Settings = {
          notionToken: '',
          notionDatabaseId: '',
          theme: 'system',
          currencySymbol: '₹',
          accountName: 'Personal',
        };
        localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(defaultSettings));
        return defaultSettings;
      }
      return JSON.parse(data);
    } catch (e) {
      console.error('Error reading settings from storage', e);
      return {
        notionToken: '',
        notionDatabaseId: '',
        theme: 'system',
        currencySymbol: '₹',
        accountName: 'Personal',
      };
    }
  },

  saveSettings(settings: Settings): void {
    try {
      localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(settings));
    } catch (e) {
      console.error('Error saving settings to storage', e);
    }
  },

  // Smart suggestions query: selects distinct expense names matching prefix
  getSuggestions(prefix: string): string[] {
    const expenses = this.getExpenses();
    const cleanPrefix = prefix.trim().toLowerCase();
    const uniqueNames = new Set<string>();

    for (const exp of expenses) {
      const n = exp.name.trim();
      if (!cleanPrefix || n.toLowerCase().startsWith(cleanPrefix)) {
        uniqueNames.add(n);
        if (uniqueNames.size >= 5) break;
      }
    }
    return Array.from(uniqueNames);
  },

  // Export to CSV
  exportToCSV(): string {
    const expenses = this.getExpenses();
    const categories = this.getCategories();
    const categoryMap = new Map(categories.map((c) => [c.id, `${c.emoji} ${c.name}`]));
    const methods = this.getPaymentMethods();
    const methodMap = new Map(methods.map((m) => [m.id, m.name]));

    const headers = ['ID', 'Date', 'Expense Name', 'Amount (INR)', 'Category', 'Payment Method', 'Synced'];
    const rows = expenses.map((e) => [
      e.id,
      e.date,
      `"${e.name.replace(/"/g, '""')}"`,
      e.amount.toFixed(2),
      `"${(categoryMap.get(e.categoryId) || 'Other').replace(/"/g, '""')}"`,
      `"${(e.paymentMethodId ? methodMap.get(e.paymentMethodId) || 'None' : 'None').replace(/"/g, '""')}"`,
      e.isSynced ? 'Yes' : 'No',
    ]);

    return [headers.join(','), ...rows.map((r) => r.join(','))].join('\n');
  },
};
