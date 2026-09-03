package com.spendsync.app;

import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.data.local.datastore.SettingsDataStore;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  public MainActivity_MembersInjector(Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    this.settingsDataStoreProvider = settingsDataStoreProvider;
    this.authDataStoreProvider = authDataStoreProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    return new MainActivity_MembersInjector(settingsDataStoreProvider, authDataStoreProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider,
      javax.inject.Provider<AuthDataStore> authDataStoreProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(settingsDataStoreProvider), Providers.asDaggerProvider(authDataStoreProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSettingsDataStore(instance, settingsDataStoreProvider.get());
    injectAuthDataStore(instance, authDataStoreProvider.get());
  }

  @InjectedFieldSignature("com.spendsync.app.MainActivity.settingsDataStore")
  public static void injectSettingsDataStore(MainActivity instance,
      SettingsDataStore settingsDataStore) {
    instance.settingsDataStore = settingsDataStore;
  }

  @InjectedFieldSignature("com.spendsync.app.MainActivity.authDataStore")
  public static void injectAuthDataStore(MainActivity instance, AuthDataStore authDataStore) {
    instance.authDataStore = authDataStore;
  }
}
