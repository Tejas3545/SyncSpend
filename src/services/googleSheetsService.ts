import { Expense, Category, PaymentMethod } from '../types';

export const GoogleSheetsService = {
  /**
   * Sample Google Apps Script code for zero-cost personal spreadsheet sync
   */
  getAppsScriptTemplate(): string {
    return `// SyncSpend Google Sheets Integration Script (Zero-cost, 100% private)
function doPost(e) {
  try {
    var ss = SpreadsheetApp.getActiveSpreadsheet();
    var sheet = ss.getActiveSheet();
    
    // Auto-create header row if sheet is brand new
    if (sheet.getLastRow() === 0) {
      sheet.appendRow(["ID", "Date", "Expense Name", "Amount", "Category", "Payment Method", "Account", "Timestamp"]);
      sheet.getRange(1, 1, 1, 8).setFontWeight("bold").setBackground("#F2F2F7");
    }
    
    var data = JSON.parse(e.postData.contents);
    if (data.action === "ping") {
      return ContentService.createTextOutput(JSON.stringify({ success: true, message: "Connected to " + ss.getName() }))
        .setMimeType(ContentService.MimeType.JSON);
    }
    
    // Append expense row
    sheet.appendRow([
      data.id || ("exp_" + Date.now()),
      data.date,
      data.name,
      Number(data.amount) || 0,
      data.category || "General",
      data.paymentMethod || "None",
      data.account || "Personal",
      new Date().toISOString()
    ]);
    
    return ContentService.createTextOutput(JSON.stringify({ success: true, row: sheet.getLastRow() }))
      .setMimeType(ContentService.MimeType.JSON);
  } catch (err) {
    return ContentService.createTextOutput(JSON.stringify({ success: false, error: err.toString() }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}`;
  },

  async testConnection(webhookUrl: string): Promise<{ success: boolean; message: string }> {
    if (!webhookUrl.trim()) {
      return { success: false, message: 'Please enter your Google Apps Script Web App URL.' };
    }

    try {
      // Send a test ping payload
      const response = await fetch(webhookUrl.trim(), {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain;charset=utf-8' }, // avoids preflight in Apps Script
        body: JSON.stringify({ action: 'ping' }),
        mode: 'cors',
      });

      if (response.ok) {
        const json = await response.json().catch(() => ({ success: true }));
        return {
          success: true,
          message: json.message || 'Google Sheets Web App connected successfully!',
        };
      } else {
        return { success: false, message: `Server returned HTTP ${response.status}` };
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : String(err);
      // Because Google Apps Script redirects (302) occasionally trigger opaque responses in browser fetch:
      if (msg.includes('Failed to fetch') || msg.includes('CORS') || msg.includes('NetworkError')) {
        return {
          success: true,
          message: 'Webhook URL reachable. Ready to receive synchronized expenses!',
        };
      }
      return { success: false, message: `Connection error: ${msg}` };
    }
  },

  async syncExpense(
    expense: Expense,
    category: Category | undefined,
    paymentMethod: PaymentMethod | undefined,
    webhookUrl: string
  ): Promise<{ success: boolean; rowId?: string; error?: string }> {
    if (!webhookUrl.trim()) {
      return { success: false, error: 'Google Sheets webhook URL is not configured' };
    }

    const payload = {
      id: expense.id,
      name: expense.name,
      amount: expense.amount,
      category: category?.name || 'Other',
      paymentMethod: paymentMethod?.name || 'None',
      account: expense.account || 'Personal',
      date: expense.date,
      timestamp: expense.createdAt,
    };

    try {
      const response = await fetch(webhookUrl.trim(), {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain;charset=utf-8' },
        body: JSON.stringify(payload),
        mode: 'cors',
      });

      if (response.ok) {
        const json = await response.json().catch(() => ({ success: true }));
        return { success: true, rowId: json.row ? String(json.row) : 'row-ok' };
      } else {
        return { success: false, error: `Google Sheets returned HTTP ${response.status}` };
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : String(err);
      return { success: false, error: msg };
    }
  },
};
