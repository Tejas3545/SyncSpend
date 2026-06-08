package com.spendsync.app.presentation.screens.login;

import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.domain.repository.AuthRepository;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authRepositoryProvider.get(), authDataStoreProvider.get());
  }

  public static LoginViewModel_Factory create(
      javax.inject.Provider<AuthRepository> authRepositoryProvider,
      javax.inject.Provider<AuthDataStore> authDataStoreProvider) {
    return new LoginViewModel_Factory(Providers.asDaggerProvider(authRepositoryProvider), Providers.asDaggerProvider(authDataStoreProvider));
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    return new LoginViewModel_Factory(authRepositoryProvider, authDataStoreProvider);
  }

  public static LoginViewModel newInstance(AuthRepository authRepository,
      AuthDataStore authDataStore) {
    return new LoginViewModel(authRepository, authDataStore);
  }
}
