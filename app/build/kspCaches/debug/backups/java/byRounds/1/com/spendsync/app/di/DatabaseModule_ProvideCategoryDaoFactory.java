package com.spendsync.app.di;

import com.spendsync.app.data.local.db.SyncSpendDatabase;
import com.spendsync.app.data.local.db.dao.CategoryDao;
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
public final class DatabaseModule_ProvideCategoryDaoFactory implements Factory<CategoryDao> {
  private final Provider<SyncSpendDatabase> dbProvider;

  public DatabaseModule_ProvideCategoryDaoFactory(Provider<SyncSpendDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CategoryDao get() {
    return provideCategoryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCategoryDaoFactory create(
      javax.inject.Provider<SyncSpendDatabase> dbProvider) {
    return new DatabaseModule_ProvideCategoryDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideCategoryDaoFactory create(
      Provider<SyncSpendDatabase> dbProvider) {
    return new DatabaseModule_ProvideCategoryDaoFactory(dbProvider);
  }

  public static CategoryDao provideCategoryDao(SyncSpendDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCategoryDao(db));
  }
}
