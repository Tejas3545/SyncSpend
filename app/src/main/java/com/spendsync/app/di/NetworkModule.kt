package com.spendsync.app.di

import com.spendsync.app.AppConfig
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.remote.notion.NotionApiService
import com.spendsync.app.data.remote.notion.NotionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNotionInterceptor(authDataStore: AuthDataStore): NotionInterceptor =
        NotionInterceptor(authDataStore)

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: NotionInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConfig.NOTION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNotionApiService(retrofit: Retrofit): NotionApiService =
        retrofit.create(NotionApiService::class.java)
}
