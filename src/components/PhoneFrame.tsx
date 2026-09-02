import React, { useState, useEffect } from 'react';
import { Wifi, Smartphone, Monitor, Zap, Layers } from 'lucide-react';

interface PhoneFrameProps {
  children: React.ReactNode;
  fitToFrame: boolean;
  onToggleFitToFrame: () => void;
  isOnline: boolean;
  pendingSyncCount: number;
  onQuickAddShortcut: () => void;
  onOpenWidgets: () => void;
}

export const PhoneFrame: React.FC<PhoneFrameProps> = ({
  children,
  fitToFrame,
  onToggleFitToFrame,
  isOnline,
  pendingSyncCount,
  onQuickAddShortcut,
  onOpenWidgets,
}) => {
  const [timeStr, setTimeStr] = useState('9:41');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      const hours = now.getHours();
      const minutes = now.getMinutes();
      const formatted = `${hours % 12 || 12}:${minutes < 10 ? '0' : ''}${minutes}`;
      setTimeStr(formatted);
    };
    updateTime();
    const timer = setInterval(updateTime, 10000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="min-h-screen w-full bg-[#0B0C10] flex flex-col items-center justify-center p-0 sm:p-4 md:p-6 select-none">
      {/* Top Desktop Helper Toolbar */}
      <div className="hidden sm:flex items-center justify-between w-full max-w-md mb-3 px-2 text-xs text-neutral-400">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-white tracking-tight">SyncSpend</span>
          <span className="text-[10px] px-2 py-0.5 rounded-full bg-neutral-800 text-neutral-300 border border-neutral-700">
            Android Native
          </span>
          {isOnline ? (
            <span className="flex items-center gap-1 text-[11px] text-emerald-400">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
              Online
            </span>
          ) : (
            <span className="flex items-center gap-1 text-[11px] text-amber-400">
              <span className="w-1.5 h-1.5 rounded-full bg-amber-400" />
              Offline ({pendingSyncCount})
            </span>
          )}
        </div>

        <div className="flex items-center gap-2">
          {/* Quick shortcuts launcher */}
          <button
            onClick={onQuickAddShortcut}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-neutral-800 hover:bg-neutral-700 text-neutral-200 transition-colors"
            title="Log via Shortcuts"
          >
            <Zap className="w-3.5 h-3.5 text-amber-400" />
            <span>Shortcut</span>
          </button>

          {/* Widgets launcher */}
          <button
            onClick={onOpenWidgets}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-neutral-800 hover:bg-neutral-700 text-neutral-200 transition-colors"
            title="Spending Trends Widget"
          >
            <Layers className="w-3.5 h-3.5 text-blue-400" />
            <span>Widgets</span>
          </button>

          {/* Fit-to-frame mode toggle */}
          <button
            onClick={onToggleFitToFrame}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-neutral-800 hover:bg-neutral-700 text-neutral-200 transition-colors"
            title={fitToFrame ? 'Switch to Full Screen View' : 'Switch to Phone Frame View'}
          >
            {fitToFrame ? <Monitor className="w-3.5 h-3.5" /> : <Smartphone className="w-3.5 h-3.5" />}
            <span>{fitToFrame ? 'Fullscreen' : 'Frame'}</span>
          </button>
        </div>
      </div>

      {/* Frame Container */}
      <div
        className={`w-full transition-all duration-300 ${
          fitToFrame
            ? 'max-w-[392px] h-[844px] max-h-[96vh] rounded-[52px] border-[9px] border-[#1f2024] shadow-[0_25px_60px_-15px_rgba(0,0,0,0.7)] ring-1 ring-white/10'
            : 'max-w-md min-h-screen sm:min-h-[850px] rounded-none sm:rounded-[36px]'
        } liquid-substrate-light dark:liquid-substrate-dark relative flex flex-col overflow-hidden`}
      >
        {/* Flagship Dynamic Island & Native Status Bar (Matching Screenshots) */}
        <div className="w-full shrink-0 pt-3 px-7 flex items-center justify-between z-40 bg-transparent select-none">
          {/* Clock */}
          <span className="text-[14px] font-semibold text-neutral-900 dark:text-white tracking-tight">
            {timeStr}
          </span>

          {/* Dynamic Island / Punch Hole */}
          <div className="w-24 h-6 rounded-full bg-black flex items-center justify-end px-2.5 shadow-xs">
            <div className="w-2.5 h-2.5 rounded-full bg-[#111] border border-neutral-800 flex items-center justify-center">
              <div className="w-1 h-1 rounded-full bg-[#007AFF]/60" />
            </div>
          </div>

          {/* Status Icons: Cellular, Wifi, Battery */}
          <div className="flex items-center gap-1.5 text-neutral-900 dark:text-white">
            <div className="flex items-end gap-[1.5px] h-3">
              <span className="w-[3px] h-1.5 bg-current rounded-2xs" />
              <span className="w-[3px] h-2 bg-current rounded-2xs" />
              <span className="w-[3px] h-2.5 bg-current rounded-2xs" />
              <span className="w-[3px] h-3 bg-current rounded-2xs" />
            </div>
            <Wifi className="w-3.5 h-3.5" />
            <div className="flex items-center">
              <div className="w-5 h-2.5 rounded-[3px] border border-current p-[1px] flex items-center">
                <div className="w-3.5 h-full bg-current rounded-2xs" />
              </div>
              <div className="w-[1.5px] h-1 bg-current rounded-r-xs" />
            </div>
          </div>
        </div>

        {/* Internal Screen Content: strictly scrollable within frame */}
        <div className="flex-1 w-full overflow-hidden flex flex-col relative">
          {children}
        </div>

        {/* Bottom Home Indicator Bar */}
        <div className="w-full shrink-0 pb-2 pt-1 flex justify-center bg-transparent z-40 select-none pointer-events-none">
          <div className="w-32 h-1 rounded-full bg-neutral-900/40 dark:bg-white/40" />
        </div>
      </div>
    </div>
  );
};
