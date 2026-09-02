import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.spendsync.app',
  appName: 'SyncSpend',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
};

export default config;
