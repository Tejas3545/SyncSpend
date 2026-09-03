package com.spendsync.app.data.repository;

import android.content.Context;
import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.domain.repository.ExpenseRepository;
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
public final class GoogleSheetsRepositoryImpl_Factory implements Factory<GoogleSheetsRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public GoogleSheetsRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<AuthDataStore> authDataStoreProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.authDataStoreProvider = authDataStoreProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public GoogleSheetsRepositoryImpl get() {
    return newInstance(contextProvider.get(), authDataStoreProvider.get(), expenseRepositoryProvider.get());
  }

  public static GoogleSheetsRepositoryImpl_Factory create(
      javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<AuthDataStore> authDataStoreProvider,
      javax.inject.Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GoogleSheetsRepositoryImpl_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(authDataStoreProvider), Providers.asDaggerProvider(expenseRepositoryProvider));
  }

  public static GoogleSheetsRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<AuthDataStore> authDataStoreProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GoogleSheetsRepositoryImpl_Factory(contextProvider, authDataStoreProvider, expenseRepositoryProvider);
  }

  public static GoogleSheetsRepositoryImpl newInstance(Context context, AuthDataStore authDataStore,
      ExpenseRepository expenseRepository) {
    return new GoogleSheetsRepositoryImpl(context, authDataStore, expenseRepository);
  }
}
