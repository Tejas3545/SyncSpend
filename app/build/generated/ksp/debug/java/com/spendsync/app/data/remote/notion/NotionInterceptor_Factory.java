package com.spendsync.app.data.remote.notion;

import com.spendsync.app.data.local.datastore.AuthDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class NotionInterceptor_Factory implements Factory<NotionInterceptor> {
  private final Provider<AuthDataStore> authDataStoreProvider;

  public NotionInterceptor_Factory(Provider<AuthDataStore> authDataStoreProvider) {
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public NotionInterceptor get() {
    return newInstance(authDataStoreProvider.get());
  }

  public static NotionInterceptor_Factory create(
      javax.inject.Provider<AuthDataStore> authDataStoreProvider) {
    return new NotionInterceptor_Factory(Providers.asDaggerProvider(authDataStoreProvider));
  }

  public static NotionInterceptor_Factory create(Provider<AuthDataStore> authDataStoreProvider) {
    return new NotionInterceptor_Factory(authDataStoreProvider);
  }

  public static NotionInterceptor newInstance(AuthDataStore authDataStore) {
    return new NotionInterceptor(authDataStore);
  }
}
