import React, { useState } from 'react';
import { Settings, Category, PaymentMethod } from '../types';
import { NotionService } from '../services/notionService';
import {
  X,
  Eye,
  EyeOff,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Plus,
  Trash2,
  Download,
  ShieldCheck,
  Moon,
  Sun,
  Laptop,
} from 'lucide-react';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: Settings;
  onSaveSettings: (settings: Settings) => void;
  categories: Category[];
  onAddCategory: (name: string, emoji: string) => void;
  onDeleteCategory: (id: string) => void;
  paymentMethods: PaymentMethod[];
  onAddPaymentMethod: (name: string) => void;
  onDeletePaymentMethod: (id: string) => void;
  onExportCSV: () => void;
  onTriggerSync: () => Promise<void>;
  isSyncing: boolean;
}

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  settings,
  onSaveSettings,
  categories,
  onAddCategory,
  onDeleteCategory,
  paymentMethods,
  onAddPaymentMethod,
  onDeletePaymentMethod,
  onExportCSV,
  onTriggerSync,
  isSyncing,
}) => {
  const [token, setToken] = useState(settings.notionToken);
  const [dbId, setDbId] = useState(settings.notionDatabaseId);
  const [theme, setTheme] = useState(settings.theme);
  const [showToken, setShowToken] = useState(false);

  // Test connection state
  const [isTesting, setIsTesting] = useState(false);
  const [testResult, setTestResult] = useState<{ success: boolean; message: string } | null>(null);

  // Add category state
  const [newCatName, setNewCatName] = useState('');
  const [newCatEmoji, setNewCatEmoji] = useState('🏷️');

  // Add payment method state
  const [newPmName, setNewPmName] = useState('');

  if (!isOpen) return null;

  const handleSaveNotion = () => {
    onSaveSettings({
      ...settings,
      notionToken: token.trim(),
      notionDatabaseId: dbId.trim(),
    });
  };

  const handleTestConnection = async () => {
    setIsTesting(true);
    setTestResult(null);
    const res = await NotionService.testConnection(token, dbId);
    setTestResult(res);
    setIsTesting(false);
  };

  const handleThemeChange = (newTheme: 'system' | 'light' | 'dark') => {
    setTheme(newTheme);
    onSaveSettings({
      ...settings,
      theme: newTheme,
    });
  };

  const handleCreateCategory = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCatName.trim()) return;
    onAddCategory(newCatName.trim(), newCatEmoji.trim() || '🏷️');
    setNewCatName('');
  };

  const handleCreatePaymentMethod = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPmName.trim()) return;
    onAddPaymentMethod(newPmName.trim());
    setNewPmName('');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-xs p-0 sm:p-4">
      <div
        id="settings-modal"
        className="relative w-full max-w-xl h-full sm:h-auto sm:max-h-[90vh] overflow-hidden rounded-none sm:rounded-3xl bg-white shadow-2xl dark:bg-[#121212] flex flex-col"
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-neutral-100 px-5 py-4 dark:border-neutral-800">
          <h2 className="text-lg font-bold text-black dark:text-white">Settings</h2>
          <button
            id="btn-close-settings"
            onClick={onClose}
            className="p-1.5 text-neutral-500 hover:text-black dark:hover:text-white transition-colors"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Scrollable Settings Body */}
        <div className="flex-1 overflow-y-auto px-5 py-5 space-y-6">
          {/* SECTION 1: Notion Integration */}
          <div className="rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-semibold text-black dark:text-white">
                Notion Integration
              </h3>
              <span className="rounded-full bg-black/5 px-2 py-0.5 text-[11px] font-medium text-neutral-600 dark:bg-white/10 dark:text-neutral-300">
                Zero Cloud Costs
              </span>
            </div>

            <p className="text-xs text-neutral-500 dark:text-neutral-400 leading-relaxed">
              Sync expenses directly to your Notion database using your private integration token.
            </p>

            <div className="space-y-3">
              {/* Token Input */}
              <div>
                <label className="block text-xs font-medium text-neutral-500 dark:text-neutral-400 mb-1">
                  Integration Token (secret_...)
                </label>
                <div className="relative flex items-center">
                  <input
                    id="input-notion-token"
                    type={showToken ? 'text' : 'password'}
                    placeholder="secret_xxxxxxxxxxxxxxxxxxxxxxxx"
                    value={token}
                    onChange={(e) => setToken(e.target.value)}
                    className="w-full rounded-xl border border-neutral-200 bg-white px-3.5 py-2.5 pr-10 text-xs text-black outline-none dark:border-neutral-800 dark:bg-[#2C2C2E] dark:text-white font-mono"
                  />
                  <button
                    type="button"
                    onClick={() => setShowToken(!showToken)}
                    className="absolute right-3 text-neutral-400 hover:text-black dark:hover:text-white"
                  >
                    {showToken ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              {/* Database ID Input */}
              <div>
                <label className="block text-xs font-medium text-neutral-500 dark:text-neutral-400 mb-1">
                  Database ID (32 characters)
                </label>
                <input
                  id="input-notion-db-id"
                  type="text"
                  placeholder="e.g. 1a2b3c4d5e6f..."
                  value={dbId}
                  onChange={(e) => setDbId(e.target.value)}
                  className="w-full rounded-xl border border-neutral-200 bg-white px-3.5 py-2.5 text-xs text-black outline-none dark:border-neutral-800 dark:bg-[#2C2C2E] dark:text-white font-mono"
                />
              </div>
            </div>

            {/* Test Result Feedback */}
            {testResult && (
              <div
                className={`flex items-start gap-2 rounded-xl p-3 text-xs ${
                  testResult.success
                    ? 'bg-emerald-50 text-emerald-800 dark:bg-emerald-950/40 dark:text-emerald-300'
                    : 'bg-red-50 text-red-800 dark:bg-red-950/40 dark:text-red-300'
                }`}
              >
                {testResult.success ? (
                  <CheckCircle2 className="h-4 w-4 shrink-0 mt-0.5" />
                ) : (
                  <AlertCircle className="h-4 w-4 shrink-0 mt-0.5" />
                )}
                <span>{testResult.message}</span>
              </div>
            )}

            {/* Action Buttons: Test Connection & Sync Now */}
            <div className="flex items-center gap-2 pt-1">
              <button
                id="btn-test-notion-connection"
                type="button"
                onClick={handleTestConnection}
                disabled={isTesting || !token.trim()}
                className="flex-1 rounded-xl bg-black px-4 py-2.5 text-xs font-semibold text-white hover:bg-neutral-800 disabled:opacity-50 dark:bg-white dark:text-black dark:hover:bg-neutral-200 transition-colors"
              >
                {isTesting ? 'Testing...' : 'Test Connection'}
              </button>

              <button
                id="btn-sync-now"
                type="button"
                onClick={async () => {
                  handleSaveNotion();
                  await onTriggerSync();
                }}
                disabled={isSyncing || !token.trim() || !dbId.trim()}
                className="flex items-center justify-center gap-1.5 rounded-xl border border-neutral-300 px-4 py-2.5 text-xs font-semibold text-black hover:bg-neutral-200 disabled:opacity-50 dark:border-neutral-700 dark:text-white dark:hover:bg-[#2C2C2E] transition-colors"
              >
                <RefreshCw className={`h-3.5 w-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
                <span>Sync Now</span>
              </button>
            </div>
          </div>

          {/* SECTION 2: Appearance & Theme */}
          <div className="rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] space-y-3">
            <h3 className="text-sm font-semibold text-black dark:text-white">Theme</h3>
            <div className="grid grid-cols-3 gap-2">
              {[
                { id: 'system', label: 'System', icon: Laptop },
                { id: 'light', label: 'Light', icon: Sun },
                { id: 'dark', label: 'Dark', icon: Moon },
              ].map(({ id, label, icon: Icon }) => {
                const isSelected = theme === id;
                return (
                  <button
                    key={id}
                    id={`btn-theme-${id}`}
                    type="button"
                    onClick={() => handleThemeChange(id as 'system' | 'light' | 'dark')}
                    className={`flex items-center justify-center gap-1.5 rounded-xl py-2.5 text-xs font-semibold transition-all ${
                      isSelected
                        ? 'bg-black text-white dark:bg-white dark:text-black shadow-xs'
                        : 'border border-neutral-300 text-neutral-600 hover:border-black dark:border-neutral-700 dark:text-neutral-300'
                    }`}
                  >
                    <Icon className="h-3.5 w-3.5" />
                    <span>{label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* SECTION 3: Manage Categories */}
          <div className="rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] space-y-3">
            <h3 className="text-sm font-semibold text-black dark:text-white">
              Manage Categories ({categories.length})
            </h3>
            <div className="max-h-48 overflow-y-auto space-y-1.5 pr-1">
              {categories.map((cat) => (
                <div
                  key={cat.id}
                  className="flex items-center justify-between rounded-xl bg-white px-3.5 py-2 dark:bg-[#2C2C2E]"
                >
                  <div className="flex items-center gap-2">
                    <span className="text-lg">{cat.emoji}</span>
                    <span className="text-xs font-medium text-black dark:text-white">{cat.name}</span>
                  </div>
                  {!cat.isDefault && (
                    <button
                      id={`btn-delete-cat-${cat.id}`}
                      type="button"
                      onClick={() => onDeleteCategory(cat.id)}
                      className="p-1 text-neutral-400 hover:text-red-500 transition-colors"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  )}
                </div>
              ))}
            </div>

            {/* Add Category Form */}
            <form onSubmit={handleCreateCategory} className="flex gap-2 pt-1">
              <input
                type="text"
                value={newCatEmoji}
                onChange={(e) => setNewCatEmoji(e.target.value)}
                placeholder="🏷️"
                maxLength={2}
                className="w-12 text-center rounded-xl border border-neutral-200 bg-white py-2 text-sm outline-none dark:border-neutral-800 dark:bg-[#2C2C2E] dark:text-white"
              />
              <input
                type="text"
                placeholder="New Category Name"
                value={newCatName}
                onChange={(e) => setNewCatName(e.target.value)}
                className="flex-1 rounded-xl border border-neutral-200 bg-white px-3 py-2 text-xs outline-none dark:border-neutral-800 dark:bg-[#2C2C2E] dark:text-white"
              />
              <button
                type="submit"
                id="btn-add-category"
                disabled={!newCatName.trim()}
                className="flex items-center justify-center rounded-xl bg-black px-3 py-2 text-xs font-medium text-white hover:bg-neutral-800 disabled:opacity-40 dark:bg-white dark:text-black"
              >
                <Plus className="h-4 w-4" />
              </button>
            </form>
          </div>

          {/* SECTION 4: Manage Payment Methods */}
          <div className="rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] space-y-3">
            <h3 className="text-sm font-semibold text-black dark:text-white">
              Payment Methods ({paymentMethods.length})
            </h3>
            <div className="max-h-36 overflow-y-auto space-y-1.5 pr-1">
              {paymentMethods.map((pm) => (
                <div
                  key={pm.id}
                  className="flex items-center justify-between rounded-xl bg-white px-3.5 py-2 dark:bg-[#2C2C2E]"
                >
                  <span className="text-xs font-medium text-black dark:text-white">{pm.name}</span>
                  {!pm.isDefault && (
                    <button
                      id={`btn-delete-pm-${pm.id}`}
                      type="button"
                      onClick={() => onDeletePaymentMethod(pm.id)}
                      className="p-1 text-neutral-400 hover:text-red-500 transition-colors"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  )}
                </div>
              ))}
            </div>

            {/* Add Payment Method Form */}
            <form onSubmit={handleCreatePaymentMethod} className="flex gap-2 pt-1">
              <input
                type="text"
                placeholder="e.g. Apple Pay, Cash, Revolut"
                value={newPmName}
                onChange={(e) => setNewPmName(e.target.value)}
                className="flex-1 rounded-xl border border-neutral-200 bg-white px-3 py-2 text-xs outline-none dark:border-neutral-800 dark:bg-[#2C2C2E] dark:text-white"
              />
              <button
                type="submit"
                id="btn-add-payment-method"
                disabled={!newPmName.trim()}
                className="flex items-center justify-center rounded-xl bg-black px-3 py-2 text-xs font-medium text-white hover:bg-neutral-800 disabled:opacity-40 dark:bg-white dark:text-black"
              >
                <Plus className="h-4 w-4" />
              </button>
            </form>
          </div>

          {/* SECTION 5: Data Export & Privacy */}
          <div className="rounded-2xl bg-[#F5F5F5] p-5 dark:bg-[#1C1C1E] space-y-3">
            <h3 className="text-sm font-semibold text-black dark:text-white">Data & Privacy</h3>
            <button
              id="btn-export-csv-settings"
              onClick={onExportCSV}
              type="button"
              className="flex w-full items-center justify-center gap-2 rounded-xl bg-white py-2.5 text-xs font-semibold text-black hover:bg-neutral-100 dark:bg-[#2C2C2E] dark:text-white dark:hover:bg-[#3A3A3C] transition-colors"
            >
              <Download className="h-4 w-4" />
              <span>Export All Data to CSV</span>
            </button>

            <div className="flex items-start gap-2 pt-2 text-xs text-neutral-500 dark:text-neutral-400">
              <ShieldCheck className="h-4 w-4 shrink-0 text-emerald-600 dark:text-emerald-400" />
              <span>
                100% Offline-First. No advertising, no trackers, and no developer server. Your expenses remain exclusively on your device and your connected Notion workspace.
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
