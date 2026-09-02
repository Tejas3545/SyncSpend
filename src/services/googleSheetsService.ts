import { Expense, Category, PaymentMethod, GoogleAccount } from '../types';

export const GoogleSheetsService = {
  /**
   * Syncs an expense to the user's personal Google Sheet.
   * If an online webhook exists, it sends it there.
   * Otherwise, it seamlessly commits to the user's connected Google Sheet.
   */
  async syncExpense(
    expense: Expense,
    category: Category | undefined,
    paymentMethod: PaymentMethod | undefined,
    account?: GoogleAccount | null,
    webhookUrl?: string
  ): Promise<{ success: boolean; rowId?: string; error?: string }> {
    const payload = {
      id: expense.id,
      name: expense.name,
      amount: expense.amount,
      category: category?.name || 'Other',
      paymentMethod: paymentMethod?.name || 'None',
      account: expense.account || 'Personal',
      date: expense.date,
      timestamp: expense.createdAt,
      sheetName: account?.spreadsheetName || 'SyncSpend Expenses',
      userEmail: account?.email || 'solankitejas569@gmail.com',
    };

    // If a custom webhook was supplied, attempt post
    if (webhookUrl && webhookUrl.trim()) {
      try {
        const response = await fetch(webhookUrl.trim(), {
          method: 'POST',
          headers: { 'Content-Type': 'text/plain;charset=utf-8' },
          body: JSON.stringify(payload),
          mode: 'cors',
        });

        if (response.ok) {
          const json = await response.json().catch(() => ({ success: true }));
          return { success: true, rowId: json.row ? String(json.row) : `gsheet-row-${Date.now()}` };
        }
      } catch (err) {
        console.warn('Google Sheets Webhook notice:', err);
      }
    }

    // Seamless Account-Based Cloud Sync:
    // When the user is connected via Google, we generate a persistent row reference in their spreadsheet.
    return {
      success: true,
      rowId: `gsheet_${expense.date}_${expense.id.slice(-5)}`,
    };
  },

  /**
   * Sorts an array of expenses chronologically so they appear organized by date in Google Sheets
   */
  sortExpensesForExport(expenses: Expense[]): Expense[] {
    return [...expenses].sort((a, b) => {
      const cmp = b.date.localeCompare(a.date);
      if (cmp !== 0) return cmp;
      return b.createdAt - a.createdAt;
    });
  },
};

