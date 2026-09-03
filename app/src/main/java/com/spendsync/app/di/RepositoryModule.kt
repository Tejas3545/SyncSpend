package com.spendsync.app.di

import com.spendsync.app.data.repository.CategoryRepositoryImpl
import com.spendsync.app.data.repository.ExpenseRepositoryImpl
import com.spendsync.app.data.repository.NotionRepositoryImpl
import com.spendsync.app.domain.repository.CategoryRepository
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.domain.repository.NotionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.spendsync.app.data.repository.AuthRepositoryImpl
import com.spendsync.app.domain.repository.AuthRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindNotionRepository(impl: NotionRepositoryImpl): NotionRepository

    @Binds
    @Singleton
    abstract fun bindGoogleSheetsRepository(impl: com.spendsync.app.data.repository.GoogleSheetsRepositoryImpl): com.spendsync.app.domain.repository.GoogleSheetsRepository
}
