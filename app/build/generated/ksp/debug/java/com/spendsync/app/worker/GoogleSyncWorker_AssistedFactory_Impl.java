package com.spendsync.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class GoogleSyncWorker_AssistedFactory_Impl implements GoogleSyncWorker_AssistedFactory {
  private final GoogleSyncWorker_Factory delegateFactory;

  GoogleSyncWorker_AssistedFactory_Impl(GoogleSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public GoogleSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<GoogleSyncWorker_AssistedFactory> create(
      GoogleSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new GoogleSyncWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<GoogleSyncWorker_AssistedFactory> createFactoryProvider(
      GoogleSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new GoogleSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
