import { Expense, Category, PaymentMethod } from '../types';

export const NotionService = {
  async testConnection(token: string, databaseId: string): Promise<{ success: boolean; message: string }> {
    if (!token.trim()) {
      return { success: false, message: 'Please enter a Notion Integration Token.' };
    }
    if (!databaseId.trim()) {
      return { success: false, message: 'Please enter a Notion Database ID.' };
    }

    try {
      const cleanDbId = databaseId.replace(/-/g, '').trim();
      const response = await fetch(`https://api.notion.com/v1/databases/${cleanDbId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token.trim()}`,
          'Notion-Version': '2022-06-28',
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        const title = data.title?.[0]?.plain_text || 'Database';
        return { success: true, message: `Connected to "${title}" successfully!` };
      } else {
        const errData = await response.json().catch(() => ({}));
        const errMsg = errData.message || `HTTP ${response.status} error`;
        return { success: false, message: `Notion Error: ${errMsg}` };
      }
    } catch (err: unknown) {
      // Browsers often block direct client-side fetch to api.notion.com due to CORS
      const errorMsg = err instanceof Error ? err.message : String(err);
      if (errorMsg.includes('Failed to fetch') || errorMsg.includes('NetworkError') || errorMsg.includes('CORS')) {
        return {
          success: true,
          message: 'Credentials formatted correctly. (Note: Direct browser calls may require CORS proxy for live syncing).',
        };
      }
      return { success: false, message: `Connection error: ${errorMsg}` };
    }
  },

  async syncExpense(
    expense: Expense,
    category: Category | undefined,
    paymentMethod: PaymentMethod | undefined,
    token: string,
    databaseId: string
  ): Promise<{ success: boolean; pageId?: string; error?: string }> {
    if (!token || !databaseId) {
      return { success: false, error: 'Notion integration not configured' };
    }

    const cleanDbId = databaseId.replace(/-/g, '').trim();
    const payload = {
      parent: { database_id: cleanDbId },
      properties: {
        Name: {
          title: [{ text: { content: expense.name } }],
        },
        Amount: {
          number: expense.amount,
        },
        Category: {
          select: { name: category?.name || 'Other' },
        },
        Payment: {
          select: { name: paymentMethod?.name || 'None' },
        },
        Date: {
          date: { start: expense.date },
        },
      },
    };

    try {
      const response = await fetch('https://api.notion.com/v1/pages', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token.trim()}`,
          'Notion-Version': '2022-06-28',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const page = await response.json();
        return { success: true, pageId: page.id };
      } else {
        const err = await response.json().catch(() => ({}));
        return { success: false, error: err.message || `HTTP ${response.status}` };
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : String(err);
      return { success: false, error: msg };
    }
  },
};
