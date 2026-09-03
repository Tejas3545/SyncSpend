import React, { useState, useEffect } from 'react';
import { WifiOff, Sparkles } from 'lucide-react';

interface PhoneFrameProps {
  children: React.ReactNode;
  fitToFrame?: boolean;
  onToggleFitToFrame?: () => void;
  isOnline: boolean;
  pendingSyncCount: number;
  onQuickAddShortcut: () => void;
  onOpenWidgets?: () => void;
  isDark?: boolean;
}

/**
 * Authentic Android edge-to-edge application container.
 * Fills 100% of native mobile screens without fake phone bezels or simulated notches.
 */
export const PhoneFrame: React.FC<PhoneFrameProps> = ({
  children,
  isOnline,
  pendingSyncCount,
  onQuickAddShortcut,
  isDark = false,
}) => {
  const [quickTapFeedback, setQuickTapFeedback] = useState(false);

  // Accelerometer detection for physical Android device back-tap / quick tap
  useEffect(() => {
    let lastTapTime = 0;
    let tapCount = 0;

    const handleMotion = (e: DeviceMotionEvent) => {
      const acc = e.accelerationIncludingGravity;
      if (!acc) return;
      const totalAcc = Math.sqrt((acc.x || 0) ** 2 + (acc.y || 0) ** 2 + (acc.z || 0) ** 2);
      if (totalAcc > 24) {
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
    setTimeout(() => setQuickTapFeedback(false), 900);
    onQuickAddShortcut();
  };

  return (
    <div className={`min-h-screen w-full flex flex-col items-center justify-start ${isDark ? 'dark bg-[#08090C]' : 'bg-[#EAEBED]'} text-neutral-900 dark:text-white select-none transition-colors duration-200`}>
      {/* Full-screen Android app viewport (edge-to-edge on mobile, sleek centered container on tablet/desktop) */}
      <div className="w-full max-w-md min-h-screen flex flex-col relative bg-[#F2F2F7] dark:bg-[#0B0C10] shadow-2xl sm:border-x sm:border-black/[0.06] sm:dark:border-white/[0.08] pt-[env(safe-area-inset-top,0px)] pb-[env(safe-area-inset-bottom,0px)] overflow-hidden">
        {/* Offline status bar pill for Android */}
        {!isOnline && (
          <div className="shrink-0 mx-4 mt-2 mb-1 px-3 py-1.5 rounded-full bg-amber-500/90 dark:bg-amber-600/90 text-white text-[11px] font-semibold flex items-center justify-center gap-1.5 shadow-xs animate-fade-in z-50">
            <WifiOff className="w-3.5 h-3.5" />
            <span>Offline Mode ({pendingSyncCount} pending sync)</span>
          </div>
        )}

        {/* Android Quick Tap feedback toast */}
        {quickTapFeedback && (
          <div className="absolute top-4 inset-x-6 z-50 py-2.5 px-4 rounded-full bg-neutral-900/95 dark:bg-white/95 text-white dark:text-neutral-900 text-xs font-semibold flex items-center justify-center gap-2 shadow-2xl backdrop-blur-md animate-bounce border border-white/20 dark:border-black/10">
            <Sparkles className="w-4 h-4 text-emerald-400 dark:text-emerald-600" />
            <span>Quick Add Triggered!</span>
          </div>
        )}

        {/* Screen Content */}
        <div className="flex-1 w-full flex flex-col relative overflow-hidden">
          {children}
        </div>
      </div>
    </div>
  );
};

