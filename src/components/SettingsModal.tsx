import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Settings, Category, PaymentMethod } from '../types';
import { NotionService } from '../services/notionService';
import { GoogleSheetsService } from '../services/googleSheetsService';
import { CategoryIcon, AVAILABLE_CATEGORY_ICONS } from './CategoryIcon';
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
  Copy,
  Check,
  Moon,
  Sun,
  Laptop,
  Table,
} from 'lucide-react';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: Settings;
  onSaveSettings: (settings: Settings) => void;
  categories: Category[];
  onAddCategory: (name: string, iconName: string) => void;
  onDeleteCategory: (id: string) => void;
  paymentMethods: PaymentMethod[];
  onAddPaymentMethod: (name: string) => void;
  onDeletePaymentMethod: (id: string) => void;
  onExportCSV: () => void;
  onTriggerSync: () => Promise<void>;
  onResetSampleData: () => void;
  isSyncing: boolean;
  isOnline: boolean;
  pendingSyncCount: number;
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
  onResetSampleData,
  isSyncing,
  isOnline,
  pendingSyncCount,
}) => {
  const [activeTab, setActiveTab] = useState<'sync' | 'preferences' | 'categories'>('sync');

  // Notion state
  const [notionToken, setNotionToken] = useState(settings.notionToken);
  const [notionDbId, setNotionDbId] = useState(settings.notionDatabaseId);
  const [isNotionEnabled, setIsNotionEnabled] = useState(settings.isNotionEnabled);
  const [showNotionToken, setShowNotionToken] = useState(false);
  const [notionTestResult, setNotionTestResult] = useState<{ success: boolean; message: string } | null>(null);
  const [isTestingNotion, setIsTestingNotion] = useState(false);

  // Google Sheets state
  const [googleWebhookUrl, setGoogleWebhookUrl] = useState(settings.googleSheetsWebhookUrl);
  const [isGoogleEnabled, setIsGoogleEnabled] = useState(settings.isGoogleSheetsEnabled);
  const [googleTestResult, setGoogleTestResult] = useState<{ success: boolean; message: string } | null>(null);
  const [isTestingGoogle, setIsTestingGoogle] = useState(false);
  const [copiedScript, setCopiedScript] = useState(false);

  // Preferences state
  const [theme, setTheme] = useState(settings.theme);
  const [currency, setCurrency] = useState(settings.currencySymbol || '$');
  const [autoSync, setAutoSync] = useState(settings.autoSyncOnOnline ?? true);

  // Categories & Payment state
  const [newCatName, setNewCatName] = useState('');
  const [newCatIcon, setNewCatIcon] = useState('utensils');
  const [newPmName, setNewPmName] = useState('');

  if (!isOpen) return null;

  const handleSaveAll = () => {
    onSaveSettings({
      ...settings,
      notionToken: notionToken.trim(),
      notionDatabaseId: notionDbId.trim(),
      isNotionEnabled,
      googleSheetsWebhookUrl: googleWebhookUrl.trim(),
      isGoogleSheetsEnabled: isGoogleEnabled,
      theme,
      currencySymbol: currency,
      autoSyncOnOnline: autoSync,
    });
    onClose();
  };

  const handleTestNotion = async () => {
    setIsTestingNotion(true);
    setNotionTestResult(null);
    const res = await NotionService.testConnection(notionToken, notionDbId);
    setNotionTestResult(res);
    setIsTestingNotion(false);
  };

  const handleTestGoogle = async () => {
    setIsTestingGoogle(true);
    setGoogleTestResult(null);
    const res = await GoogleSheetsService.testConnection(googleWebhookUrl);
    setGoogleTestResult(res);
    setIsTestingGoogle(false);
  };

  const handleCopyScript = () => {
    const script = GoogleSheetsService.getAppsScriptTemplate();
    navigator.clipboard.writeText(script);
    setCopiedScript(true);
    setTimeout(() => setCopiedScript(false), 2500);
  };

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/60 backdrop-blur-md">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 15 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 10 }}
          className="relative w-full max-w-sm rounded-[36px] bg-[#F2F2F7] dark:bg-[#121214] p-5 shadow-2xl border border-white/40 dark:border-white/10 max-h-[90vh] flex flex-col overflow-hidden"
        >
          {/* Header */}
          <div className="flex items-center justify-between pb-3 border-b border-neutral-200/60 dark:border-neutral-800 shrink-0">
            <h2 className="text-[17px] font-bold text-neutral-900 dark:text-white">Settings</h2>
            <div className="flex items-center gap-2">
              <button
                onClick={handleSaveAll}
                className="text-[14px] font-bold text-[#007AFF] hover:opacity-80 transition-opacity"
              >
                Save
              </button>
              <button
                onClick={onClose}
                className="p-1 rounded-full bg-neutral-200/80 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex items-center gap-1 mt-3 p-1 rounded-full bg-neutral-200/70 dark:bg-neutral-800/80 shrink-0">
            <button
              onClick={() => setActiveTab('sync')}
              className={`flex-1 py-1.5 text-xs font-semibold rounded-full transition-all ${
                activeTab === 'sync'
                  ? 'bg-white text-black shadow-xs dark:bg-neutral-700 dark:text-white'
                  : 'text-neutral-500 hover:text-black dark:text-neutral-400'
              }`}
            >
              Sync & Cloud
            </button>
            <button
              onClick={() => setActiveTab('preferences')}
              className={`flex-1 py-1.5 text-xs font-semibold rounded-full transition-all ${
                activeTab === 'preferences'
                  ? 'bg-white text-black shadow-xs dark:bg-neutral-700 dark:text-white'
                  : 'text-neutral-500 hover:text-black dark:text-neutral-400'
              }`}
            >
              Preferences
            </button>
            <button
              onClick={() => setActiveTab('categories')}
              className={`flex-1 py-1.5 text-xs font-semibold rounded-full transition-all ${
                activeTab === 'categories'
                  ? 'bg-white text-black shadow-xs dark:bg-neutral-700 dark:text-white'
                  : 'text-neutral-500 hover:text-black dark:text-neutral-400'
              }`}
            >
              Categories
            </button>
          </div>

          {/* Tab Content (Scrollable) */}
          <div className="flex-1 overflow-y-auto no-scrollbar py-3 space-y-4">
            {activeTab === 'sync' && (
              <div className="space-y-4">
                {/* Sync Status Banner */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-3.5 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className={`w-2.5 h-2.5 rounded-full ${isOnline ? 'bg-emerald-500' : 'bg-amber-500'}`} />
                      <span className="text-xs font-semibold text-neutral-900 dark:text-white">
                        {isOnline ? 'Connected to Network' : 'Working Offline'}
                      </span>
                    </div>
                    <button
                      onClick={onTriggerSync}
                      disabled={isSyncing || !isOnline}
                      className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#007AFF] text-white text-xs font-semibold disabled:opacity-40"
                    >
                      <RefreshCw className={`w-3 h-3 ${isSyncing ? 'animate-spin' : ''}`} />
                      <span>{isSyncing ? 'Syncing...' : 'Sync Now'}</span>
                    </button>
                  </div>
                  <p className="text-[11px] text-neutral-500 mt-1.5">
                    {pendingSyncCount === 0
                      ? 'All expenses are synchronized with your personal cloud.'
                      : `${pendingSyncCount} expense(s) pending sync in local offline queue.`}
                  </p>
                  <div className="mt-2.5 pt-2 border-t border-neutral-100 dark:border-neutral-800 flex items-center justify-between">
                    <span className="text-[12px] font-medium text-neutral-700 dark:text-neutral-300">
                      Auto-sync when online
                    </span>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={autoSync}
                        onChange={(e) => setAutoSync(e.target.checked)}
                        className="sr-only peer"
                      />
                      <div className="w-8 h-4.5 bg-neutral-200 peer-focus:outline-hidden rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-3.5 after:w-3.5 after:transition-all peer-checked:bg-[#007AFF]" />
                    </label>
                  </div>
                </div>

                {/* Notion Integration Box */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05] space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <div className="w-7 h-7 rounded-lg bg-neutral-900 text-white flex items-center justify-center font-serif font-black text-sm">
                        N
                      </div>
                      <span className="text-sm font-bold text-neutral-900 dark:text-white">Notion Sync</span>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={isNotionEnabled}
                        onChange={(e) => setIsNotionEnabled(e.target.checked)}
                        className="sr-only peer"
                      />
                      <div className="w-9 h-5 bg-neutral-200 peer-focus:outline-hidden rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-[#007AFF]" />
                    </label>
                  </div>

                  {isNotionEnabled && (
                    <div className="space-y-2 pt-1">
                      <div>
                        <label className="text-[11px] font-semibold text-neutral-400">Integration Token</label>
                        <div className="relative mt-0.5">
                          <input
                            type={showNotionToken ? 'text' : 'password'}
                            placeholder="secret_..."
                            value={notionToken}
                            onChange={(e) => setNotionToken(e.target.value)}
                            className="w-full text-xs p-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white pr-8 outline-hidden"
                          />
                          <button
                            type="button"
                            onClick={() => setShowNotionToken(!showNotionToken)}
                            className="absolute right-2.5 top-2.5 text-neutral-400"
                          >
                            {showNotionToken ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
                          </button>
                        </div>
                      </div>

                      <div>
                        <label className="text-[11px] font-semibold text-neutral-400">Database ID</label>
                        <input
                          type="text"
                          placeholder="e.g. 32-character Notion Database ID"
                          value={notionDbId}
                          onChange={(e) => setNotionDbId(e.target.value)}
                          className="w-full text-xs p-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden mt-0.5"
                        />
                      </div>

                      <button
                        type="button"
                        onClick={handleTestNotion}
                        disabled={isTestingNotion || !notionToken || !notionDbId}
                        className="w-full py-1.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 text-xs font-semibold text-neutral-800 dark:text-neutral-200 transition-colors disabled:opacity-50"
                      >
                        {isTestingNotion ? 'Testing Connection...' : 'Test Notion Connection'}
                      </button>

                      {notionTestResult && (
                        <div className={`p-2 rounded-xl text-xs flex items-center gap-1.5 ${notionTestResult.success ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300' : 'bg-red-50 text-red-700 dark:bg-red-950/40 dark:text-red-300'}`}>
                          {notionTestResult.success ? <CheckCircle2 className="w-3.5 h-3.5 shrink-0" /> : <AlertCircle className="w-3.5 h-3.5 shrink-0" />}
                          <span>{notionTestResult.message}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                {/* Google Sheets Integration Box (Zero Developer Cost, User-Owned) */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05] space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <div className="w-7 h-7 rounded-lg bg-emerald-600 text-white flex items-center justify-center font-bold text-xs">
                        <Table className="w-4 h-4" />
                      </div>
                      <div>
                        <span className="text-sm font-bold text-neutral-900 dark:text-white">Google Sheets Sync</span>
                        <span className="block text-[10px] text-emerald-600 dark:text-emerald-400 font-semibold">100% Free &amp; Private</span>
                      </div>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={isGoogleEnabled}
                        onChange={(e) => setIsGoogleEnabled(e.target.checked)}
                        className="sr-only peer"
                      />
                      <div className="w-9 h-5 bg-neutral-200 peer-focus:outline-hidden rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-emerald-600" />
                    </label>
                  </div>

                  {isGoogleEnabled && (
                    <div className="space-y-2 pt-1">
                      <div>
                        <label className="text-[11px] font-semibold text-neutral-400">Google Apps Script Web App URL</label>
                        <input
                          type="url"
                          placeholder="https://script.google.com/macros/s/.../exec"
                          value={googleWebhookUrl}
                          onChange={(e) => setGoogleWebhookUrl(e.target.value)}
                          className="w-full text-xs p-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden mt-0.5"
                        />
                      </div>

                      {/* 1-Click Copy Script Button */}
                      <button
                        type="button"
                        onClick={handleCopyScript}
                        className="w-full flex items-center justify-center gap-1.5 py-2 px-3 rounded-xl bg-emerald-50 dark:bg-emerald-950/30 text-emerald-700 dark:text-emerald-300 text-xs font-semibold border border-emerald-200/50 hover:bg-emerald-100 transition-colors"
                      >
                        {copiedScript ? <Check className="w-3.5 h-3.5 text-emerald-600" /> : <Copy className="w-3.5 h-3.5" />}
                        <span>{copiedScript ? 'Apps Script Copied to Clipboard!' : 'Copy 1-Click Google Apps Script'}</span>
                      </button>

                      <p className="text-[10px] text-neutral-400 leading-tight">
                        Instructions: Open any Google Sheet &rarr; Extensions &rarr; Apps Script &rarr; paste the code &rarr; Deploy as Web App (Access: Anyone).
                      </p>

                      <button
                        type="button"
                        onClick={handleTestGoogle}
                        disabled={isTestingGoogle || !googleWebhookUrl}
                        className="w-full py-1.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 text-xs font-semibold text-neutral-800 dark:text-neutral-200 transition-colors disabled:opacity-50"
                      >
                        {isTestingGoogle ? 'Testing...' : 'Test Google Sheets Connection'}
                      </button>

                      {googleTestResult && (
                        <div className={`p-2 rounded-xl text-xs flex items-center gap-1.5 ${googleTestResult.success ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300' : 'bg-red-50 text-red-700 dark:bg-red-950/40 dark:text-red-300'}`}>
                          {googleTestResult.success ? <CheckCircle2 className="w-3.5 h-3.5 shrink-0" /> : <AlertCircle className="w-3.5 h-3.5 shrink-0" />}
                          <span>{googleTestResult.message}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'preferences' && (
              <div className="space-y-3">
                {/* Currency Symbol Picker */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Currency Symbol
                  </span>
                  <div className="grid grid-cols-5 gap-2">
                    {['$', '₹', '€', '£', '¥'].map((cur) => (
                      <button
                        key={cur}
                        onClick={() => setCurrency(cur)}
                        className={`py-2 text-sm font-bold rounded-xl transition-all ${
                          currency === cur
                            ? 'bg-[#007AFF] text-white shadow-xs'
                            : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 hover:bg-neutral-200'
                        }`}
                      >
                        {cur}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Theme Selector */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Theme Appearance
                  </span>
                  <div className="grid grid-cols-3 gap-2">
                    {[
                      { id: 'light', label: 'Light', icon: Sun },
                      { id: 'dark', label: 'Dark', icon: Moon },
                      { id: 'system', label: 'System', icon: Laptop },
                    ].map((t) => {
                      const Icon = t.icon;
                      const isSelected = theme === t.id;
                      return (
                        <button
                          key={t.id}
                          onClick={() => setTheme(t.id as any)}
                          className={`flex flex-col items-center py-2.5 rounded-xl transition-all ${
                            isSelected
                              ? 'bg-neutral-900 text-white dark:bg-white dark:text-black font-semibold'
                              : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400'
                          }`}
                        >
                          <Icon className="w-4 h-4 mb-1" />
                          <span className="text-xs">{t.label}</span>
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Data Backup & Reset */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05] space-y-2">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block">
                    Data Management
                  </span>
                  <button
                    onClick={onExportCSV}
                    className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 text-xs font-semibold text-neutral-800 dark:text-neutral-200 transition-colors"
                  >
                    <Download className="w-3.5 h-3.5" />
                    <span>Export Expenses to CSV</span>
                  </button>
                  <button
                    onClick={onResetSampleData}
                    className="w-full py-2 rounded-xl text-xs font-medium text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-300 transition-colors"
                  >
                    Load Official SyncSpend Sample Data
                  </button>
                </div>
              </div>
            )}

            {activeTab === 'categories' && (
              <div className="space-y-4">
                {/* Categories Manager */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Categories ({categories.length})
                  </span>
                  <div className="max-h-36 overflow-y-auto space-y-1 no-scrollbar">
                    {categories.map((c) => (
                      <div key={c.id} className="flex items-center justify-between p-2 rounded-lg bg-neutral-50 dark:bg-neutral-800/60">
                        <div className="flex items-center gap-2.5">
                          <div className="w-6 h-6 rounded-md bg-[#EFEFF4] dark:bg-[#2C2C2E] flex items-center justify-center text-neutral-800 dark:text-neutral-200 shrink-0">
                            <CategoryIcon iconName={c.iconName} className="w-3.5 h-3.5" />
                          </div>
                          <span className="text-xs font-medium text-neutral-800 dark:text-neutral-200">{c.name}</span>
                        </div>
                        {!c.isDefault && (
                          <button onClick={() => onDeleteCategory(c.id)} className="text-neutral-400 hover:text-red-500">
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))}
                  </div>

                  {/* Add Category */}
                  <div className="flex gap-1.5 mt-3">
                    <select
                      value={newCatIcon}
                      onChange={(e) => setNewCatIcon(e.target.value)}
                      className="w-28 text-xs p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 outline-hidden cursor-pointer"
                    >
                      {AVAILABLE_CATEGORY_ICONS.map((icon) => (
                        <option key={icon.id} value={icon.id}>
                          {icon.label}
                        </option>
                      ))}
                    </select>
                    <input
                      type="text"
                      placeholder="Category name..."
                      value={newCatName}
                      onChange={(e) => setNewCatName(e.target.value)}
                      className="flex-1 text-xs p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden"
                    />
                    <button
                      type="button"
                      onClick={() => {
                        if (newCatName.trim()) {
                          onAddCategory(newCatName.trim(), newCatIcon);
                          setNewCatName('');
                        }
                      }}
                      disabled={!newCatName.trim()}
                      className="px-3 py-2 rounded-xl bg-[#007AFF] text-white text-xs font-semibold disabled:opacity-40"
                    >
                      <Plus className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                {/* Payment Methods Manager */}
                <div className="rounded-2xl bg-white dark:bg-[#1C1C1E] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.05]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Payment Methods ({paymentMethods.length})
                  </span>
                  <div className="max-h-28 overflow-y-auto space-y-1 no-scrollbar">
                    {paymentMethods.map((pm) => (
                      <div key={pm.id} className="flex items-center justify-between p-2 rounded-lg bg-neutral-50 dark:bg-neutral-800/60">
                        <span className="text-xs font-medium text-neutral-800 dark:text-neutral-200">{pm.name}</span>
                        {!pm.isDefault && (
                          <button onClick={() => onDeletePaymentMethod(pm.id)} className="text-neutral-400 hover:text-red-500">
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))}
                  </div>

                  <div className="flex gap-1.5 mt-3">
                    <input
                      type="text"
                      placeholder="Payment method..."
                      value={newPmName}
                      onChange={(e) => setNewPmName(e.target.value)}
                      className="flex-1 text-xs p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden"
                    />
                    <button
                      type="button"
                      onClick={() => {
                        if (newPmName.trim()) {
                          onAddPaymentMethod(newPmName.trim());
                          setNewPmName('');
                        }
                      }}
                      disabled={!newPmName.trim()}
                      className="px-3 py-2 rounded-xl bg-[#007AFF] text-white text-xs font-semibold disabled:opacity-40"
                    >
                      <Plus className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
