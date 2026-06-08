package com.spendsync.app.di;

import com.spendsync.app.data.local.db.SyncSpendDatabase;
import com.spendsync.app.data.local.db.dao.ExpenseDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideExpenseDaoFactory implements Factory<ExpenseDao> {
  private final Provider<SyncSpendDatabase> dbProvider;

  public DatabaseModule_ProvideExpenseDaoFactory(Provider<SyncSpendDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ExpenseDao get() {
    return provideExpenseDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideExpenseDaoFactory create(
      javax.inject.Provider<SyncSpendDatabase> dbProvider) {
    return new DatabaseModule_ProvideExpenseDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideExpenseDaoFactory create(
      Provider<SyncSpendDatabase> dbProvider) {
    return new DatabaseModule_ProvideExpenseDaoFactory(dbProvider);
  }

  public static ExpenseDao provideExpenseDao(SyncSpendDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExpenseDao(db));
  }
}
