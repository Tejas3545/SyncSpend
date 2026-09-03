package com.spendsync.app.di;

import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.data.remote.notion.NotionInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideNotionInterceptorFactory implements Factory<NotionInterceptor> {
  private final Provider<AuthDataStore> authDataStoreProvider;

  public NetworkModule_ProvideNotionInterceptorFactory(
      Provider<AuthDataStore> authDataStoreProvider) {
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public NotionInterceptor get() {
    return provideNotionInterceptor(authDataStoreProvider.get());
  }

  public static NetworkModule_ProvideNotionInterceptorFactory create(
      javax.inject.Provider<AuthDataStore> authDataStoreProvider) {
    return new NetworkModule_ProvideNotionInterceptorFactory(Providers.asDaggerProvider(authDataStoreProvider));
  }

  public static NetworkModule_ProvideNotionInterceptorFactory create(
      Provider<AuthDataStore> authDataStoreProvider) {
    return new NetworkModule_ProvideNotionInterceptorFactory(authDataStoreProvider);
  }

  public static NotionInterceptor provideNotionInterceptor(AuthDataStore authDataStore) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideNotionInterceptor(authDataStore));
  }
}
