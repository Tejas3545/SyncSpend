package com.spendsync.app.data.repository;

import android.content.Context;
import com.spendsync.app.data.local.datastore.AuthDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  public AuthRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    this.contextProvider = contextProvider;
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(contextProvider.get(), authDataStoreProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<AuthDataStore> authDataStoreProvider) {
    return new AuthRepositoryImpl_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(authDataStoreProvider));
  }

  public static AuthRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    return new AuthRepositoryImpl_Factory(contextProvider, authDataStoreProvider);
  }

  public static AuthRepositoryImpl newInstance(Context context, AuthDataStore authDataStore) {
    return new AuthRepositoryImpl(context, authDataStore);
  }
}
