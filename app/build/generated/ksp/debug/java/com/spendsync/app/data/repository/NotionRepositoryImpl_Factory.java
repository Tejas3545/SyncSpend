package com.spendsync.app.data.repository;

import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.data.remote.notion.NotionApiService;
import com.spendsync.app.domain.repository.ExpenseRepository;
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
public final class NotionRepositoryImpl_Factory implements Factory<NotionRepositoryImpl> {
  private final Provider<NotionApiService> notionApiServiceProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  public NotionRepositoryImpl_Factory(Provider<NotionApiService> notionApiServiceProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    this.notionApiServiceProvider = notionApiServiceProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public NotionRepositoryImpl get() {
    return newInstance(notionApiServiceProvider.get(), expenseRepositoryProvider.get(), authDataStoreProvider.get());
  }

  public static NotionRepositoryImpl_Factory create(
      javax.inject.Provider<NotionApiService> notionApiServiceProvider,
      javax.inject.Provider<ExpenseRepository> expenseRepositoryProvider,
      javax.inject.Provider<AuthDataStore> authDataStoreProvider) {
    return new NotionRepositoryImpl_Factory(Providers.asDaggerProvider(notionApiServiceProvider), Providers.asDaggerProvider(expenseRepositoryProvider), Providers.asDaggerProvider(authDataStoreProvider));
  }

  public static NotionRepositoryImpl_Factory create(
      Provider<NotionApiService> notionApiServiceProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    return new NotionRepositoryImpl_Factory(notionApiServiceProvider, expenseRepositoryProvider, authDataStoreProvider);
  }

  public static NotionRepositoryImpl newInstance(NotionApiService notionApiService,
      ExpenseRepository expenseRepository, AuthDataStore authDataStore) {
    return new NotionRepositoryImpl(notionApiService, expenseRepository, authDataStore);
  }
}
