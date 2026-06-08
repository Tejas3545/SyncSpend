package com.spendsync.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.domain.repository.NotionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@HiltWorker
class NotionSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val expenseRepository: ExpenseRepository,
    private val notionRepository: NotionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            notionRepository.syncUnsyncedExpenses()
            Result.success()
        } catch (e: HttpException) {
            if (e.code() == 429) Result.retry() // Rate limited
            else Result.failure()
        } catch (e: IOException) {
            Result.retry() // Network error, retry
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "notion_sync_worker"
        
        fun buildPeriodicRequest() = PeriodicWorkRequestBuilder<NotionSyncWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        fun buildOneTimeRequest() = androidx.work.OneTimeWorkRequestBuilder<NotionSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
    }
}
