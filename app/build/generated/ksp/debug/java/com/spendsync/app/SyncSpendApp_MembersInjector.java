package com.spendsync.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class SyncSpendApp_MembersInjector implements MembersInjector<SyncSpendApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public SyncSpendApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<SyncSpendApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new SyncSpendApp_MembersInjector(workerFactoryProvider);
  }

  public static MembersInjector<SyncSpendApp> create(
      javax.inject.Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new SyncSpendApp_MembersInjector(Providers.asDaggerProvider(workerFactoryProvider));
  }

  @Override
  public void injectMembers(SyncSpendApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.spendsync.app.SyncSpendApp.workerFactory")
  public static void injectWorkerFactory(SyncSpendApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
