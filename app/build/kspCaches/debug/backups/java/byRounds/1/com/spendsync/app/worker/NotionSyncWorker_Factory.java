package com.spendsync.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.spendsync.app.domain.repository.ExpenseRepository;
import com.spendsync.app.domain.repository.NotionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NotionSyncWorker_Factory {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<NotionRepository> notionRepositoryProvider;

  public NotionSyncWorker_Factory(Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<NotionRepository> notionRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.notionRepositoryProvider = notionRepositoryProvider;
  }

  public NotionSyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, expenseRepositoryProvider.get(), notionRepositoryProvider.get());
  }

  public static NotionSyncWorker_Factory create(
      javax.inject.Provider<ExpenseRepository> expenseRepositoryProvider,
      javax.inject.Provider<NotionRepository> notionRepositoryProvider) {
    return new NotionSyncWorker_Factory(Providers.asDaggerProvider(expenseRepositoryProvider), Providers.asDaggerProvider(notionRepositoryProvider));
  }

  public static NotionSyncWorker_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<NotionRepository> notionRepositoryProvider) {
    return new NotionSyncWorker_Factory(expenseRepositoryProvider, notionRepositoryProvider);
  }

  public static NotionSyncWorker newInstance(Context context, WorkerParameters params,
      ExpenseRepository expenseRepository, NotionRepository notionRepository) {
    return new NotionSyncWorker(context, params, expenseRepository, notionRepository);
  }
}
