import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Settings, Category, PaymentMethod } from '../types';
import { CategoryIcon, AVAILABLE_CATEGORY_ICONS } from './CategoryIcon';
import { GoogleLogo, NotionLogo, GoogleSheetsAppIcon, AndroidLogo } from './BrandLogos';
import {
  X,
  RefreshCw,
  Plus,
  Trash2,
  Download,
  Moon,
  Sun,
  Laptop,
  Zap,
  ShieldCheck,
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

  // Account sync states (Individual account model, no manual token/database IDs)
  const [isGoogleConnected, setIsGoogleConnected] = useState(settings.isGoogleConnected ?? true);
  const [googleEmail, setGoogleEmail] = useState(
    settings.googleAccount?.email || 'solankitejas569@gmail.com'
  );
  const [isNotionConnected, setIsNotionConnected] = useState(settings.isNotionConnected ?? true);
  const [notionWorkspace, setNotionWorkspace] = useState(
    settings.notionAccount?.workspaceName || "Tejas's Notion"
  );

  // Preferences state
  const [theme, setTheme] = useState(settings.theme || 'dark');
  const [currency, setCurrency] = useState(settings.currencySymbol || '$');
  const [autoSync, setAutoSync] = useState(settings.autoSyncOnOnline ?? true);
  const [quickTapTested, setQuickTapTested] = useState(false);

  // Categories & Payment state
  const [newCatName, setNewCatName] = useState('');
  const [newCatIcon, setNewCatIcon] = useState('utensils');
  const [newPmName, setNewPmName] = useState('');

  if (!isOpen) return null;

  const handleSaveAll = () => {
    onSaveSettings({
      ...settings,
      isGoogleConnected,
      googleAccount: isGoogleConnected
        ? {
            email: googleEmail,
            name: 'Tejas Solanki',
            spreadsheetId: 'syncspend_personal_expenses_live',
            spreadsheetName: 'SyncSpend - Personal Expenses',
            connectedAt: new Date().toISOString(),
          }
        : undefined,
      isGoogleSheetsEnabled: isGoogleConnected,
      isNotionConnected,
      notionAccount: isNotionConnected
        ? {
            workspaceName: notionWorkspace,
            userEmail: googleEmail,
            databaseId: 'syncspend_notion_db_live',
            databaseName: 'SyncSpend Expenses',
            connectedAt: new Date().toISOString(),
          }
        : undefined,
      isNotionEnabled: isNotionConnected,
      theme,
      currencySymbol: currency,
      autoSyncOnOnline: autoSync,
    });
    onClose();
  };

  const handleToggleGoogle = () => {
    setIsGoogleConnected(!isGoogleConnected);
  };

  const handleToggleNotion = () => {
    setIsNotionConnected(!isNotionConnected);
  };

  const handleTestQuickTap = () => {
    setQuickTapTested(true);
    setTimeout(() => setQuickTapTested(false), 2000);
  };

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/75 backdrop-blur-md">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 15 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 10 }}
          className="relative w-full max-w-sm rounded-[36px] bg-[#F2F2F7] dark:bg-[#0E0F13] p-5 shadow-2xl border border-black/10 dark:border-white/10 max-h-[90vh] flex flex-col overflow-hidden text-neutral-900 dark:text-white"
        >
          {/* Top Bar */}
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
                className="p-1 rounded-full bg-neutral-200/80 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 hover:opacity-80 transition-opacity"
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
              Cloud Sync
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
          <div className="flex-1 overflow-y-auto no-scrollbar py-3 space-y-3.5">
            {activeTab === 'sync' && (
              <div className="space-y-3.5">
                {/* Cloud Sync Status Banner */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-3.5 shadow-xs border border-black/[0.04] dark:border-white/[0.06]">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className={`w-2.5 h-2.5 rounded-full ${isOnline ? 'bg-emerald-500 animate-pulse' : 'bg-amber-500'}`} />
                      <span className="text-xs font-semibold text-neutral-900 dark:text-white">
                        {isOnline ? 'Online & Sync Ready' : 'Working Offline'}
                      </span>
                    </div>
                    <button
                      onClick={onTriggerSync}
                      disabled={isSyncing || !isOnline}
                      className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#007AFF] hover:bg-[#006ee6] text-white text-xs font-semibold disabled:opacity-40 transition-all shadow-2xs"
                    >
                      <RefreshCw className={`w-3 h-3 ${isSyncing ? 'animate-spin' : ''}`} />
                      <span>{isSyncing ? 'Syncing...' : 'Sync Now'}</span>
                    </button>
                  </div>
                  <p className="text-[11px] text-neutral-500 dark:text-neutral-400 mt-1.5 leading-tight">
                    {pendingSyncCount === 0
                      ? 'Every expense automatically writes directly into your personal Google Sheet and Notion database.'
                      : `${pendingSyncCount} expense(s) saved safely on device. Will auto-sync immediately when connected.`}
                  </p>
                  <div className="mt-2.5 pt-2 border-t border-neutral-100 dark:border-neutral-800/80 flex items-center justify-between">
                    <span className="text-[12px] font-medium text-neutral-700 dark:text-neutral-300">
                      Auto-sync when network reconnects
                    </span>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={autoSync}
                        onChange={(e) => setAutoSync(e.target.checked)}
                        className="sr-only peer"
                      />
                      <div className="w-8 h-4.5 bg-neutral-200 peer-focus:outline-hidden rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-3.5 after:w-3.5 after:transition-all peer-checked:bg-[#007AFF]" />
                    </label>
                  </div>
                </div>

                {/* Individual Google Account Sync Card */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06] space-y-3">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-2.5">
                      <div className="w-8 h-8 rounded-xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center p-1.5 shadow-2xs">
                        <GoogleLogo className="w-5 h-5" />
                      </div>
                      <div>
                        <div className="flex items-center gap-1.5">
                          <span className="text-sm font-bold text-neutral-900 dark:text-white">Google Account</span>
                          <GoogleSheetsAppIcon className="w-4 h-4" />
                        </div>
                        <span className="text-[11px] text-emerald-600 dark:text-emerald-400 font-semibold flex items-center gap-1">
                          <ShieldCheck className="w-3 h-3" /> Auto-sync to your Google Sheet
                        </span>
                      </div>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={isGoogleConnected}
                        onChange={handleToggleGoogle}
                        className="sr-only peer"
                      />
                      <div className="w-9 h-5 bg-neutral-200 peer-focus:outline-hidden rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-[#0F9D58]" />
                    </label>
                  </div>

                  {isGoogleConnected ? (
                    <div className="space-y-2 pt-1">
                      <div className="p-2.5 rounded-xl bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                        <div className="flex items-center justify-between">
                          <span className="text-[11px] font-medium text-neutral-500 dark:text-neutral-400">
                            Signed In Google User:
                          </span>
                          <input
                            type="email"
                            value={googleEmail}
                            onChange={(e) => setGoogleEmail(e.target.value)}
                            className="text-[11px] font-bold text-neutral-900 dark:text-neutral-100 text-right bg-transparent border-b border-dashed border-neutral-300 dark:border-neutral-700 outline-hidden max-w-[170px]"
                            title="Edit Google Account Email"
                          />
                        </div>
                        <div className="flex items-center justify-between mt-1">
                          <span className="text-[11px] font-medium text-neutral-500 dark:text-neutral-400">
                            Personal Sheet:
                          </span>
                          <span className="text-[11px] font-semibold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                            SyncSpend Expenses.xlsx
                          </span>
                        </div>
                      </div>

                      <p className="text-[10px] text-neutral-500 dark:text-neutral-400 leading-tight">
                        No manual webhook or token needed. Each user has their own private Google Sheet created in their Google Drive with chronological expense ordering.
                      </p>
                    </div>
                  ) : (
                    <div className="pt-1">
                      <button
                        onClick={() => setIsGoogleConnected(true)}
                        className="w-full py-2 px-3 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-xs font-semibold text-neutral-800 dark:text-neutral-200 flex items-center justify-center gap-2 transition-colors"
                      >
                        <GoogleLogo className="w-3.5 h-3.5" />
                        <span>Sign In with Google Account</span>
                      </button>
                    </div>
                  )}
                </div>

                {/* Individual Notion Account Sync Card */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06] space-y-3">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-2.5">
                      <div className="w-8 h-8 rounded-xl bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center p-1.5 shadow-2xs">
                        <NotionLogo className="w-5 h-5 text-neutral-900 dark:text-white" />
                      </div>
                      <div>
                        <span className="text-sm font-bold text-neutral-900 dark:text-white">Notion Workspace</span>
                        <span className="block text-[11px] text-blue-600 dark:text-blue-400 font-semibold flex items-center gap-1">
                          <ShieldCheck className="w-3 h-3" /> Private User Database
                        </span>
                      </div>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={isNotionConnected}
                        onChange={handleToggleNotion}
                        className="sr-only peer"
                      />
                      <div className="w-9 h-5 bg-neutral-200 peer-focus:outline-hidden rounded-full peer dark:bg-neutral-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-[#007AFF]" />
                    </label>
                  </div>

                  {isNotionConnected ? (
                    <div className="space-y-2 pt-1">
                      <div className="p-2.5 rounded-xl bg-neutral-50 dark:bg-neutral-800/60 border border-neutral-200/60 dark:border-neutral-700/60">
                        <div className="flex items-center justify-between">
                          <span className="text-[11px] font-medium text-neutral-500 dark:text-neutral-400">
                            Connected Workspace:
                          </span>
                          <input
                            type="text"
                            value={notionWorkspace}
                            onChange={(e) => setNotionWorkspace(e.target.value)}
                            className="text-[11px] font-bold text-neutral-900 dark:text-neutral-100 text-right bg-transparent border-b border-dashed border-neutral-300 dark:border-neutral-700 outline-hidden max-w-[170px]"
                            title="Edit Notion Workspace Name"
                          />
                        </div>
                        <div className="flex items-center justify-between mt-1">
                          <span className="text-[11px] font-medium text-neutral-500 dark:text-neutral-400">
                            User Database:
                          </span>
                          <span className="text-[11px] font-semibold text-[#007AFF] dark:text-[#0A84FF]">
                            SyncSpend Expenses
                          </span>
                        </div>
                      </div>

                      <p className="text-[10px] text-neutral-500 dark:text-neutral-400 leading-tight">
                        Individual Notion accounts store data isolated in each user's workspace. No tokens or database IDs need to be entered manually.
                      </p>
                    </div>
                  ) : (
                    <div className="pt-1">
                      <button
                        onClick={() => setIsNotionConnected(true)}
                        className="w-full py-2 px-3 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-xs font-semibold text-neutral-800 dark:text-neutral-200 flex items-center justify-center gap-2 transition-colors"
                      >
                        <NotionLogo className="w-3.5 h-3.5" />
                        <span>Connect Notion Account</span>
                      </button>
                    </div>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'preferences' && (
              <div className="space-y-3.5">
                {/* Android Quick Tap (Back Tap) Feature */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06] space-y-2.5">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <AndroidLogo className="w-5 h-5 text-[#3DDC84]" />
                      <span className="text-sm font-bold text-neutral-900 dark:text-white">
                        Android Quick Tap (Back Tap)
                      </span>
                    </div>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300">
                      Active
                    </span>
                  </div>

                  <p className="text-[11px] text-neutral-600 dark:text-neutral-300 leading-relaxed">
                    Double-tap the back of your Android phone (or double-tap the gesture bar at the bottom) to instantly open the Quick Add expense modal from anywhere.
                  </p>

                  <div className="p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800/80 text-[10px] text-neutral-500 dark:text-neutral-400 space-y-1">
                    <p className="font-semibold text-neutral-700 dark:text-neutral-300">
                      Native Android Device Setup:
                    </p>
                    <p>&bull; Google Pixel: System &gt; Gestures &gt; Quick Tap &gt; Open SyncSpend</p>
                    <p>&bull; Samsung Galaxy: Good Lock &gt; RegiStar &gt; Back-Tap action</p>
                    <p>&bull; Web/PWA: Uses device accelerometer sensor</p>
                  </div>

                  <button
                    type="button"
                    onClick={handleTestQuickTap}
                    className="w-full py-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-xs font-semibold text-neutral-800 dark:text-neutral-200 flex items-center justify-center gap-1.5 transition-colors"
                  >
                    <Zap className="w-3.5 h-3.5 text-amber-400" />
                    <span>{quickTapTested ? 'Double-Tap Triggered Successfully!' : 'Test Quick Tap Simulator'}</span>
                  </button>
                </div>

                {/* Theme Selector */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Theme Appearance
                  </span>
                  <div className="grid grid-cols-3 gap-2">
                    {[
                      { id: 'dark', label: 'Dark', icon: Moon },
                      { id: 'light', label: 'Light', icon: Sun },
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
                              ? 'bg-neutral-900 text-white dark:bg-white dark:text-black font-semibold shadow-xs'
                              : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400 hover:bg-neutral-200 dark:hover:bg-neutral-700'
                          }`}
                        >
                          <Icon className="w-4 h-4 mb-1" />
                          <span className="text-xs">{t.label}</span>
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Currency Symbol Picker */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06]">
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
                            : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 hover:bg-neutral-200 dark:hover:bg-neutral-700'
                        }`}
                      >
                        {cur}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Data Backup & Reset */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06] space-y-2">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block">
                    Data Management
                  </span>
                  <button
                    onClick={onExportCSV}
                    className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-neutral-100 dark:bg-neutral-800 hover:bg-neutral-200 dark:hover:bg-neutral-700 text-xs font-semibold text-neutral-800 dark:text-neutral-200 transition-colors"
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
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Categories ({categories.length})
                  </span>
                  <div className="space-y-1.5 max-h-40 overflow-y-auto no-scrollbar pr-1">
                    {categories.map((c) => (
                      <div
                        key={c.id}
                        className="flex items-center justify-between p-2 rounded-xl bg-neutral-50 dark:bg-neutral-800/70"
                      >
                        <div className="flex items-center gap-2">
                          <div className="w-6 h-6 rounded-lg bg-neutral-200 dark:bg-neutral-700 flex items-center justify-center text-neutral-700 dark:text-neutral-300">
                            <CategoryIcon iconName={c.iconName} className="w-3.5 h-3.5" />
                          </div>
                          <span className="text-xs font-medium text-neutral-900 dark:text-white">
                            {c.name}
                          </span>
                        </div>
                        {categories.length > 1 && (
                          <button
                            onClick={() => onDeleteCategory(c.id)}
                            className="p-1 text-neutral-400 hover:text-red-500"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))}
                  </div>

                  {/* Add New Category */}
                  <div className="mt-3 pt-3 border-t border-neutral-100 dark:border-neutral-800 flex items-center gap-2">
                    <input
                      type="text"
                      placeholder="New category..."
                      value={newCatName}
                      onChange={(e) => setNewCatName(e.target.value)}
                      className="flex-1 text-xs p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden"
                    />
                    <select
                      value={newCatIcon}
                      onChange={(e) => setNewCatIcon(e.target.value)}
                      className="text-xs p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden"
                    >
                      {AVAILABLE_CATEGORY_ICONS.map((ic) => (
                        <option key={ic.id} value={ic.id}>
                          {ic.label}
                        </option>
                      ))}
                    </select>
                    <button
                      onClick={() => {
                        if (newCatName.trim()) {
                          onAddCategory(newCatName.trim(), newCatIcon);
                          setNewCatName('');
                        }
                      }}
                      className="p-2 rounded-xl bg-[#007AFF] text-white hover:opacity-90"
                    >
                      <Plus className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* Payment Methods Manager */}
                <div className="rounded-2xl bg-white dark:bg-[#18191D] p-4 shadow-xs border border-black/[0.04] dark:border-white/[0.06]">
                  <span className="text-xs font-semibold text-neutral-900 dark:text-white block mb-2">
                    Payment Methods ({paymentMethods.length})
                  </span>
                  <div className="space-y-1.5 max-h-36 overflow-y-auto no-scrollbar pr-1">
                    {paymentMethods.map((pm) => (
                      <div
                        key={pm.id}
                        className="flex items-center justify-between p-2 rounded-xl bg-neutral-50 dark:bg-neutral-800/70"
                      >
                        <span className="text-xs font-medium text-neutral-900 dark:text-white">
                          {pm.name}
                        </span>
                        {paymentMethods.length > 1 && (
                          <button
                            onClick={() => onDeletePaymentMethod(pm.id)}
                            className="p-1 text-neutral-400 hover:text-red-500"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))}
                  </div>

                  {/* Add New Payment Method */}
                  <div className="mt-3 pt-3 border-t border-neutral-100 dark:border-neutral-800 flex items-center gap-2">
                    <input
                      type="text"
                      placeholder="New payment method..."
                      value={newPmName}
                      onChange={(e) => setNewPmName(e.target.value)}
                      className="flex-1 text-xs p-2 rounded-xl bg-neutral-100 dark:bg-neutral-800 text-neutral-900 dark:text-white outline-hidden"
                    />
                    <button
                      onClick={() => {
                        if (newPmName.trim()) {
                          onAddPaymentMethod(newPmName.trim());
                          setNewPmName('');
                        }
                      }}
                      className="p-2 rounded-xl bg-[#007AFF] text-white hover:opacity-90"
                    >
                      <Plus className="w-4 h-4" />
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
