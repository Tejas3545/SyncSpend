package com.spendsync.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.spendsync.app.worker.NotionSyncWorker
import com.spendsync.app.worker.GoogleSyncWorker
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SyncSpendApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic Notion sync
        schedulePeriodicSync()
    }
    
    private fun schedulePeriodicSync() {
        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            NotionSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            NotionSyncWorker.buildPeriodicRequest()
        )
        workManager.enqueueUniquePeriodicWork(
            GoogleSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            GoogleSyncWorker.buildPeriodicRequest()
        )
    }
}