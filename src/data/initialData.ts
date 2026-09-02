import { Category, PaymentMethod, Expense } from '../types';
import { toLocalDateString } from '../utils/dateUtils';

export const DEFAULT_CATEGORIES: Category[] = [
  { id: 'cat-1', name: 'Food & Drinks', iconName: 'utensils', isDefault: true },
  { id: 'cat-2', name: 'Entertainment', iconName: 'music', isDefault: true },
  { id: 'cat-3', name: 'Transportation', iconName: 'car', isDefault: true },
  { id: 'cat-4', name: 'Shopping', iconName: 'shopping-bag', isDefault: true },
  { id: 'cat-5', name: 'Groceries', iconName: 'shopping-cart', isDefault: true },
  { id: 'cat-6', name: 'Health', iconName: 'heart-pulse', isDefault: true },
  { id: 'cat-7', name: 'Utilities', iconName: 'zap', isDefault: true },
  { id: 'cat-8', name: 'Housing', iconName: 'home', isDefault: true },
  { id: 'cat-9', name: 'Travel', iconName: 'plane', isDefault: true },
  { id: 'cat-10', name: 'Personal', iconName: 'user', isDefault: true },
];

export const DEFAULT_PAYMENT_METHODS: PaymentMethod[] = [
  { id: 'pm-1', name: 'Apple Pay', isDefault: true },
  { id: 'pm-2', name: 'Credit Card', isDefault: true },
  { id: 'pm-3', name: 'Debit Card', isDefault: true },
  { id: 'pm-4', name: 'Cash', isDefault: true },
  { id: 'pm-5', name: 'UPI', isDefault: true },
];

/**
 * Initial dataset matching the official SyncSpend App Store screens:
 * Week total = $120.38
 * Items:
 * - Spotify ($20.98)
 * - Groceries ($56.80)
 * - Uber ($26.40)
 * - Dining out ($16.20)
 * Month total = $349.18
 * Year total = $1,865.18
 */
export function getSampleExpenses(): Expense[] {
  const now = new Date();
  const d0 = toLocalDateString(now); // Today ("Latest")
  
  const d1Date = new Date(now);
  d1Date.setDate(d1Date.getDate() - 1);
  const d1 = toLocalDateString(d1Date); // Yesterday / Monday

  const d5Date = new Date(now);
  d5Date.setDate(d5Date.getDate() - 5);
  const d5 = toLocalDateString(d5Date);

  const d12Date = new Date(now);
  d12Date.setDate(d12Date.getDate() - 12);
  const d12 = toLocalDateString(d12Date);

  const d25Date = new Date(now);
  d25Date.setDate(d25Date.getDate() - 25);
  const d25 = toLocalDateString(d25Date);

  const d60Date = new Date(now);
  d60Date.setDate(d60Date.getDate() - 60);
  const d60 = toLocalDateString(d60Date);

  return [
    {
      id: 'exp-spotify',
      name: 'Spotify',
      amount: 20.98,
      categoryId: 'cat-2', // Entertainment
      paymentMethodId: 'pm-1', // Apple Pay
      account: 'Personal',
      date: d0,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: now.getTime() - 1000 * 60 * 60 * 2,
    },
    {
      id: 'exp-groceries',
      name: 'Groceries',
      amount: 56.80,
      categoryId: 'cat-5', // Groceries
      paymentMethodId: 'pm-2',
      account: 'Personal',
      date: d0,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: now.getTime() - 1000 * 60 * 60 * 4,
    },
    {
      id: 'exp-uber',
      name: 'Uber',
      amount: 26.40,
      categoryId: 'cat-3', // Transportation
      paymentMethodId: 'pm-1',
      account: 'Personal',
      date: d1,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: d1Date.getTime() + 1000 * 60 * 60 * 14,
    },
    {
      id: 'exp-dining',
      name: 'Dining out',
      amount: 16.20,
      categoryId: 'cat-1', // Food & Drinks
      paymentMethodId: 'pm-1',
      account: 'Personal',
      date: d1,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: d1Date.getTime() + 1000 * 60 * 60 * 19,
    },
    // Earlier items in the month to reach $349.18 ($120.38 + $228.80 = $349.18)
    {
      id: 'exp-coffee-beans',
      name: 'Blue Bottle Coffee',
      amount: 42.80,
      categoryId: 'cat-1',
      paymentMethodId: 'pm-1',
      account: 'Personal',
      date: d5,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: d5Date.getTime() + 1000 * 60 * 60 * 9,
    },
    {
      id: 'exp-cloud-sub',
      name: 'Cloud Storage & Workspace',
      amount: 68.00,
      categoryId: 'cat-7',
      paymentMethodId: 'pm-2',
      account: 'Personal',
      date: d12,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: d12Date.getTime() + 1000 * 60 * 60 * 11,
    },
    {
      id: 'exp-pharmacy',
      name: 'Apothecary Vitamins',
      amount: 118.00,
      categoryId: 'cat-6',
      paymentMethodId: 'pm-3',
      account: 'Personal',
      date: d25,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: d25Date.getTime() + 1000 * 60 * 60 * 16,
    },
    // Earlier items in the year to reach $1,865.18 ($349.18 + $1,516.00 = $1,865.18)
    {
      id: 'exp-flight-seat',
      name: 'Annual Tech Summit Travel',
      amount: 1516.00,
      categoryId: 'cat-9',
      paymentMethodId: 'pm-2',
      account: 'Personal',
      date: d60,
      isSynced: true,
      isNotionSynced: true,
      isGoogleSynced: true,
      createdAt: d60Date.getTime() + 1000 * 60 * 60 * 10,
    },
  ];
}
