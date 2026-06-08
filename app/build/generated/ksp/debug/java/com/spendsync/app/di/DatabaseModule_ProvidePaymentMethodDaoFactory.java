package com.spendsync.app.di;

import com.spendsync.app.data.local.db.SyncSpendDatabase;
import com.spendsync.app.data.local.db.dao.PaymentMethodDao;
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
public final class DatabaseModule_ProvidePaymentMethodDaoFactory implements Factory<PaymentMethodDao> {
  private final Provider<SyncSpendDatabase> dbProvider;

  public DatabaseModule_ProvidePaymentMethodDaoFactory(Provider<SyncSpendDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PaymentMethodDao get() {
    return providePaymentMethodDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePaymentMethodDaoFactory create(
      javax.inject.Provider<SyncSpendDatabase> dbProvider) {
    return new DatabaseModule_ProvidePaymentMethodDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvidePaymentMethodDaoFactory create(
      Provider<SyncSpendDatabase> dbProvider) {
    return new DatabaseModule_ProvidePaymentMethodDaoFactory(dbProvider);
  }

  public static PaymentMethodDao providePaymentMethodDao(SyncSpendDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePaymentMethodDao(db));
  }
}
