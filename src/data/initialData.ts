import { Category, PaymentMethod, Expense } from '../types';
import { toLocalDateString } from '../utils/dateUtils';

export const DEFAULT_CATEGORIES: Category[] = [
  { id: 'cat-1', name: 'Food & Drinks', emoji: '🍔', isDefault: true },
  { id: 'cat-2', name: 'Shopping', emoji: '🛍️', isDefault: true },
  { id: 'cat-3', name: 'Entertainment', emoji: '🎭', isDefault: true },
  { id: 'cat-4', name: 'Transportation', emoji: '🚗', isDefault: true },
  { id: 'cat-5', name: 'Health', emoji: '💊', isDefault: true },
  { id: 'cat-6', name: 'Utilities', emoji: '💡', isDefault: true },
  { id: 'cat-7', name: 'Housing', emoji: '🏠', isDefault: true },
  { id: 'cat-8', name: 'Personal', emoji: '👤', isDefault: true },
  { id: 'cat-9', name: 'Travel', emoji: '✈️', isDefault: true },
  { id: 'cat-10', name: 'Other', emoji: '📌', isDefault: true },
];

export const DEFAULT_PAYMENT_METHODS: PaymentMethod[] = [
  { id: 'pm-1', name: 'UPI', isDefault: true },
  { id: 'pm-2', name: 'Credit Card', isDefault: true },
  { id: 'pm-3', name: 'Debit Card', isDefault: true },
  { id: 'pm-4', name: 'Cash', isDefault: true },
  { id: 'pm-5', name: 'Net Banking', isDefault: true },
];

export function getSampleExpenses(): Expense[] {
  const now = new Date();
  const d0 = toLocalDateString(now);
  
  const d1Date = new Date(now);
  d1Date.setDate(d1Date.getDate() - 1);
  const d1 = toLocalDateString(d1Date);

  const d2Date = new Date(now);
  d2Date.setDate(d2Date.getDate() - 2);
  const d2 = toLocalDateString(d2Date);

  const d3Date = new Date(now);
  d3Date.setDate(d3Date.getDate() - 3);
  const d3 = toLocalDateString(d3Date);

  const d5Date = new Date(now);
  d5Date.setDate(d5Date.getDate() - 5);
  const d5 = toLocalDateString(d5Date);

  return [
    {
      id: 'exp-1',
      name: 'Artisan Coffee & Croissant',
      amount: 280.0,
      categoryId: 'cat-1',
      paymentMethodId: 'pm-1',
      date: d0,
      isSynced: true,
      createdAt: now.getTime() - 1000 * 60 * 60 * 2,
    },
    {
      id: 'exp-2',
      name: 'Metro Transit Card Recharge',
      amount: 500.0,
      categoryId: 'cat-4',
      paymentMethodId: 'pm-1',
      date: d0,
      isSynced: false,
      createdAt: now.getTime() - 1000 * 60 * 60 * 5,
    },
    {
      id: 'exp-3',
      name: 'Organic Grocery Store',
      amount: 1450.5,
      categoryId: 'cat-1',
      paymentMethodId: 'pm-2',
      date: d1,
      isSynced: true,
      createdAt: d1Date.getTime() + 1000 * 60 * 60 * 14,
    },
    {
      id: 'exp-4',
      name: 'Spotify Premium Family',
      amount: 179.0,
      categoryId: 'cat-3',
      paymentMethodId: 'pm-2',
      date: d1,
      isSynced: true,
      createdAt: d1Date.getTime() + 1000 * 60 * 60 * 10,
    },
    {
      id: 'exp-5',
      name: 'Electricity & Internet Bill',
      amount: 1890.0,
      categoryId: 'cat-6',
      paymentMethodId: 'pm-5',
      date: d2,
      isSynced: true,
      createdAt: d2Date.getTime() + 1000 * 60 * 60 * 11,
    },
    {
      id: 'exp-6',
      name: 'Bookstore Paperback Novel',
      amount: 499.0,
      categoryId: 'cat-2',
      paymentMethodId: 'pm-1',
      date: d3,
      isSynced: true,
      createdAt: d3Date.getTime() + 1000 * 60 * 60 * 16,
    },
    {
      id: 'exp-7',
      name: 'Weekend Farmer Market',
      amount: 820.0,
      categoryId: 'cat-1',
      paymentMethodId: 'pm-4',
      date: d5,
      isSynced: true,
      createdAt: d5Date.getTime() + 1000 * 60 * 60 * 10,
    },
  ];
}
