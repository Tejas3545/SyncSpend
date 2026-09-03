package com.spendsync.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.spendsync.app.domain.repository.GoogleSheetsRepository;
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
public final class GoogleSyncWorker_Factory {
  private final Provider<GoogleSheetsRepository> googleSheetsRepositoryProvider;

  public GoogleSyncWorker_Factory(Provider<GoogleSheetsRepository> googleSheetsRepositoryProvider) {
    this.googleSheetsRepositoryProvider = googleSheetsRepositoryProvider;
  }

  public GoogleSyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, googleSheetsRepositoryProvider.get());
  }

  public static GoogleSyncWorker_Factory create(
      javax.inject.Provider<GoogleSheetsRepository> googleSheetsRepositoryProvider) {
    return new GoogleSyncWorker_Factory(Providers.asDaggerProvider(googleSheetsRepositoryProvider));
  }

  public static GoogleSyncWorker_Factory create(
      Provider<GoogleSheetsRepository> googleSheetsRepositoryProvider) {
    return new GoogleSyncWorker_Factory(googleSheetsRepositoryProvider);
  }

  public static GoogleSyncWorker newInstance(Context context, WorkerParameters params,
      GoogleSheetsRepository googleSheetsRepository) {
    return new GoogleSyncWorker(context, params, googleSheetsRepository);
  }
}
