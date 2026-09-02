import { Expense, Settings } from '../types';
import { StorageService } from './storageService';
import { NotionService } from './notionService';
import { GoogleSheetsService } from './googleSheetsService';

export interface SyncResult {
  totalProcessed: number;
  syncedNotion: number;
  syncedGoogle: number;
  errors: string[];
}

export const SyncManager = {
  isOnline(): boolean {
    return typeof navigator !== 'undefined' ? navigator.onLine : true;
  },

  async syncSingleExpense(
    expense: Expense,
    settings: Settings
  ): Promise<{ expense: Expense; errors: string[] }> {
    const categories = StorageService.getCategories();
    const paymentMethods = StorageService.getPaymentMethods();
    const category = categories.find((c) => c.id === expense.categoryId);
    const paymentMethod = paymentMethods.find((p) => p.id === expense.paymentMethodId);

    const errors: string[] = [];
    let isNotionSynced = expense.isNotionSynced ?? false;
    let notionPageId = expense.notionPageId;
    let isGoogleSynced = expense.isGoogleSynced ?? false;
    let googleRowId = expense.googleRowId;

    // 1. Notion Sync
    if (settings.isNotionEnabled && settings.notionToken && settings.notionDatabaseId && !isNotionSynced) {
      if (this.isOnline()) {
        const res = await NotionService.syncExpense(
          expense,
          category,
          paymentMethod,
          settings.notionToken,
          settings.notionDatabaseId
        );
        if (res.success) {
          isNotionSynced = true;
          notionPageId = res.pageId;
        } else if (res.error) {
          errors.push(`Notion: ${res.error}`);
        }
      }
    }

    // 2. Google Sheets Sync
    if (settings.isGoogleSheetsEnabled && settings.googleSheetsWebhookUrl && !isGoogleSynced) {
      if (this.isOnline()) {
        const res = await GoogleSheetsService.syncExpense(
          expense,
          category,
          paymentMethod,
          settings.googleSheetsWebhookUrl
        );
        if (res.success) {
          isGoogleSynced = true;
          googleRowId = res.rowId;
        } else if (res.error) {
          errors.push(`Google Sheets: ${res.error}`);
        }
      }
    }

    const isFullySynced =
      (!settings.isNotionEnabled || isNotionSynced) &&
      (!settings.isGoogleSheetsEnabled || isGoogleSynced);

    const updatedExpense: Expense = {
      ...expense,
      isNotionSynced,
      notionPageId,
      isGoogleSynced,
      googleRowId,
      isSynced: isFullySynced,
    };

    StorageService.updateExpense(expense.id, updatedExpense);
    return { expense: updatedExpense, errors };
  },

  async syncAllPending(settings: Settings): Promise<SyncResult> {
    const expenses = StorageService.getExpenses();
    const pending = expenses.filter(
      (e) =>
        (settings.isNotionEnabled && !e.isNotionSynced) ||
        (settings.isGoogleSheetsEnabled && !e.isGoogleSynced)
    );

    const result: SyncResult = {
      totalProcessed: pending.length,
      syncedNotion: 0,
      syncedGoogle: 0,
      errors: [],
    };

    if (!this.isOnline()) {
      result.errors.push('Device is currently offline. Expenses are safely saved in local offline queue.');
      return result;
    }

    for (const exp of pending) {
      const { expense: updated, errors } = await this.syncSingleExpense(exp, settings);
      if (updated.isNotionSynced && !exp.isNotionSynced) result.syncedNotion++;
      if (updated.isGoogleSynced && !exp.isGoogleSynced) result.syncedGoogle++;
      if (errors.length > 0) result.errors.push(...errors);
    }

    return result;
  },
};
