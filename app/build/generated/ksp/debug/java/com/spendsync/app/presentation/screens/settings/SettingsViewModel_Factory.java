package com.spendsync.app.presentation.screens.settings;

import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.data.local.datastore.SettingsDataStore;
import com.spendsync.app.domain.repository.NotionRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  private final Provider<NotionRepository> notionRepositoryProvider;

  public SettingsViewModel_Factory(Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<AuthDataStore> authDataStoreProvider,
      Provider<NotionRepository> notionRepositoryProvider) {
    this.settingsDataStoreProvider = settingsDataStoreProvider;
    this.authDataStoreProvider = authDataStoreProvider;
    this.notionRepositoryProvider = notionRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsDataStoreProvider.get(), authDataStoreProvider.get(), notionRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider,
      javax.inject.Provider<AuthDataStore> authDataStoreProvider,
      javax.inject.Provider<NotionRepository> notionRepositoryProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(settingsDataStoreProvider), Providers.asDaggerProvider(authDataStoreProvider), Providers.asDaggerProvider(notionRepositoryProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<AuthDataStore> authDataStoreProvider,
      Provider<NotionRepository> notionRepositoryProvider) {
    return new SettingsViewModel_Factory(settingsDataStoreProvider, authDataStoreProvider, notionRepositoryProvider);
  }

  public static SettingsViewModel newInstance(SettingsDataStore settingsDataStore,
      AuthDataStore authDataStore, NotionRepository notionRepository) {
    return new SettingsViewModel(settingsDataStore, authDataStore, notionRepository);
  }
}
