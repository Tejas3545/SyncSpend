package com.spendsync.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.spendsync.app.domain.repository.GoogleSheetsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.util.concurrent.TimeUnit

@HiltWorker
class GoogleSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val googleSheetsRepository: GoogleSheetsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            googleSheetsRepository.syncUnsyncedExpenses()
            Result.success()
        } catch (e: IOException) {
            Result.retry() // Network error, retry
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "google_sync_worker"
        
        fun buildPeriodicRequest() = PeriodicWorkRequestBuilder<GoogleSyncWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        fun buildOneTimeRequest() = androidx.work.OneTimeWorkRequestBuilder<GoogleSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
    }
}
