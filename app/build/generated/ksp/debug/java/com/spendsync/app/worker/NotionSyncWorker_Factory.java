package com.spendsync.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
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
  private final Provider<NotionRepository> notionRepositoryProvider;

  public NotionSyncWorker_Factory(Provider<NotionRepository> notionRepositoryProvider) {
    this.notionRepositoryProvider = notionRepositoryProvider;
  }

  public NotionSyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, notionRepositoryProvider.get());
  }

  public static NotionSyncWorker_Factory create(
      javax.inject.Provider<NotionRepository> notionRepositoryProvider) {
    return new NotionSyncWorker_Factory(Providers.asDaggerProvider(notionRepositoryProvider));
  }

  public static NotionSyncWorker_Factory create(
      Provider<NotionRepository> notionRepositoryProvider) {
    return new NotionSyncWorker_Factory(notionRepositoryProvider);
  }

  public static NotionSyncWorker newInstance(Context context, WorkerParameters params,
      NotionRepository notionRepository) {
    return new NotionSyncWorker(context, params, notionRepository);
  }
}
