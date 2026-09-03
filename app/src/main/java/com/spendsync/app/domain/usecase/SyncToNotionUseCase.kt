package com.spendsync.app.domain.usecase

import com.spendsync.app.domain.repository.NotionRepository
import javax.inject.Inject

class SyncToNotionUseCase @Inject constructor(
    private val notionRepository: NotionRepository
) {
    suspend operator fun invoke() {
        notionRepository.syncUnsyncedExpenses()
    }
}
