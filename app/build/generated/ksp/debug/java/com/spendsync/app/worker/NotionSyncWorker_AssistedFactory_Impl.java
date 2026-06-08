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
public final class NotionSyncWorker_AssistedFactory_Impl implements NotionSyncWorker_AssistedFactory {
  private final NotionSyncWorker_Factory delegateFactory;

  NotionSyncWorker_AssistedFactory_Impl(NotionSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public NotionSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<NotionSyncWorker_AssistedFactory> create(
      NotionSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new NotionSyncWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<NotionSyncWorker_AssistedFactory> createFactoryProvider(
      NotionSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new NotionSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
