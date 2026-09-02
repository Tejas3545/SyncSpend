import React, { useState, useEffect } from 'react';
import { Wifi, Smartphone, Monitor, Zap, Layers, Sparkles } from 'lucide-react';

interface PhoneFrameProps {
  children: React.ReactNode;
  fitToFrame: boolean;
  onToggleFitToFrame: () => void;
  isOnline: boolean;
  pendingSyncCount: number;
  onQuickAddShortcut: () => void;
  onOpenWidgets: () => void;
  isDark?: boolean;
}

export const PhoneFrame: React.FC<PhoneFrameProps> = ({
  children,
  fitToFrame,
  onToggleFitToFrame,
  isOnline,
  pendingSyncCount,
  onQuickAddShortcut,
  onOpenWidgets,
  isDark = false,
}) => {
  const [timeStr, setTimeStr] = useState('12:29');
  const [quickTapFeedback, setQuickTapFeedback] = useState(false);

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

  // Accelerometer detection for physical Android device back-tap
  useEffect(() => {
    let lastTapTime = 0;
    let tapCount = 0;

    const handleMotion = (e: DeviceMotionEvent) => {
      const acc = e.accelerationIncludingGravity;
      if (!acc) return;
      const totalAcc = Math.sqrt((acc.x || 0) ** 2 + (acc.y || 0) ** 2 + (acc.z || 0) ** 2);
      if (totalAcc > 22) {
        const now = Date.now();
        if (now - lastTapTime < 450) {
          tapCount++;
          if (tapCount >= 2) {
            triggerQuickTap();
            tapCount = 0;
          }
        } else {
          tapCount = 1;
        }
        lastTapTime = now;
      }
    };

    if (typeof window !== 'undefined' && 'DeviceMotionEvent' in window) {
      window.addEventListener('devicemotion', handleMotion);
    }
    return () => {
      if (typeof window !== 'undefined' && 'DeviceMotionEvent' in window) {
        window.removeEventListener('devicemotion', handleMotion);
      }
    };
  }, [onQuickAddShortcut]);

  const triggerQuickTap = () => {
    setQuickTapFeedback(true);
    setTimeout(() => setQuickTapFeedback(false), 800);
    onQuickAddShortcut();
  };

  return (
    <div className="min-h-screen w-full bg-[#050608] flex flex-col items-center justify-center p-0 sm:p-4 md:p-6 select-none">
      {/* Top Helper Toolbar */}
      <div className="hidden sm:flex items-center justify-between w-full max-w-md mb-3 px-2 text-xs text-neutral-400">
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1.5 text-neutral-200 font-semibold tracking-tight">
            <Smartphone className="w-4 h-4 text-neutral-300" />
            <span>SyncSpend</span>
          </div>
          <span className="text-[10px] px-2 py-0.5 rounded-full bg-neutral-800 text-neutral-300 border border-neutral-700 font-medium tracking-tight">
            iOS Edition
          </span>
          {isOnline ? (
            <span className="flex items-center gap-1 text-[11px] text-emerald-400 font-medium">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
              Online
            </span>
          ) : (
            <span className="flex items-center gap-1 text-[11px] text-amber-400 font-medium">
              <span className="w-1.5 h-1.5 rounded-full bg-amber-400" />
              Offline ({pendingSyncCount})
            </span>
          )}
        </div>

        <div className="flex items-center gap-2">
          {/* Action button / gesture trigger */}
          <button
            onClick={triggerQuickTap}
            className={`flex items-center gap-1.5 px-3 py-1 rounded-full transition-all text-xs font-medium ${
              quickTapFeedback
                ? 'bg-[#007AFF] text-white font-semibold scale-105 shadow-md shadow-[#007AFF]/30'
                : 'bg-neutral-800 hover:bg-neutral-700 text-neutral-200'
            }`}
            title="Action Button / Quick Add shortcut simulator"
          >
            <Zap className="w-3.5 h-3.5 text-amber-400" />
            <span>Action Button</span>
          </button>

          {/* Widgets launcher */}
          <button
            onClick={onOpenWidgets}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-neutral-800 hover:bg-neutral-700 text-neutral-200 transition-colors text-xs font-medium"
            title="Spending Trends Widget"
          >
            <Layers className="w-3.5 h-3.5 text-blue-400" />
            <span>Widgets</span>
          </button>

          {/* Fit-to-frame mode toggle */}
          <button
            onClick={onToggleFitToFrame}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-neutral-800 hover:bg-neutral-700 text-neutral-200 transition-colors text-xs font-medium"
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
            ? 'max-w-[392px] h-[844px] max-h-[96vh] rounded-[52px] border-[8px] border-[#1C1C1E] shadow-[0_25px_70px_-15px_rgba(0,0,0,0.85)] ring-1 ring-white/15'
            : 'max-w-md min-h-screen sm:min-h-[850px] rounded-none sm:rounded-[44px]'
        } ${isDark ? 'dark bg-[#0B0C10] text-white' : 'bg-[#F2F2F7] text-neutral-900'} liquid-substrate relative flex flex-col overflow-hidden`}
      >
        {/* Apple iOS Flagship Status Bar (Dynamic Island & Native iOS Indicators) */}
        <div className="w-full shrink-0 pt-3 px-7 flex items-center justify-between z-40 bg-transparent select-none">
          {/* iOS Clock */}
          <span className="text-[14px] font-semibold text-neutral-900 dark:text-white tracking-[-0.02em] tabular-nums pl-0.5">
            {timeStr}
          </span>

          {/* Apple Dynamic Island Cutout */}
          <div className="w-[105px] h-[27px] rounded-full bg-black shadow-sm flex items-center justify-between px-2.5">
            <div className="w-2.5 h-2.5 rounded-full bg-[#0a1220] ring-1 ring-white/10" />
            <div className="w-1.5 h-1.5 rounded-full bg-[#10192e]" />
          </div>

          {/* Apple iOS Status Icons: Signal Bars, Wifi, Battery Pill */}
          <div className="flex items-center gap-1.5 text-neutral-900 dark:text-white pr-0.5">
            {/* 4-bar cellular signal */}
            <div className="flex items-end gap-[1.5px] h-3 mr-0.5">
              <div className="w-[3px] h-[3.5px] bg-current rounded-[0.5px]" />
              <div className="w-[3px] h-[5.5px] bg-current rounded-[0.5px]" />
              <div className="w-[3px] h-[8px] bg-current rounded-[0.5px]" />
              <div className="w-[3px] h-[10.5px] bg-current rounded-[0.5px]" />
            </div>

            {/* iOS Wi-Fi Icon */}
            <Wifi className="w-3.5 h-3.5" />

            {/* iOS Authentic Battery Pill */}
            <div className="flex items-center ml-0.5">
              <div className="w-[21px] h-[11px] rounded-[3.5px] border-[1.5px] border-current p-[1.5px] flex items-center">
                <div className="w-[85%] h-full bg-current rounded-[1.5px]" />
              </div>
              <div className="w-[1px] h-[4px] bg-current rounded-r-[1px] -ml-[0.5px]" />
            </div>
          </div>
        </div>

        {/* Quick Tap notification toast if triggered */}
        {quickTapFeedback && (
          <div className="absolute top-14 inset-x-6 z-50 py-2 px-4 rounded-full bg-neutral-900/95 text-white text-xs font-medium flex items-center justify-center gap-2 shadow-xl backdrop-blur-md animate-bounce border border-white/15">
            <Sparkles className="w-3.5 h-3.5 text-[#007AFF]" />
            <span className="tracking-tight">iOS Action Shortcut Activated!</span>
          </div>
        )}

        {/* Internal Screen Content */}
        <div className="flex-1 w-full overflow-hidden flex flex-col relative">
          {children}
        </div>

        {/* Apple iOS Home Indicator Bar (Click / Tap to Quick Add) */}
        <div
          onClick={triggerQuickTap}
          title="Tap Home Indicator for Quick Add"
          className="w-full shrink-0 pb-3 pt-1.5 flex flex-col items-center justify-center bg-transparent z-40 select-none cursor-pointer group"
        >
          <div className="w-[134px] h-[4.5px] rounded-full bg-neutral-900/35 dark:bg-white/40 group-hover:bg-[#007AFF] transition-all" />
        </div>
      </div>
    </div>
  );
};

