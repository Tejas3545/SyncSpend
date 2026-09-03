package com.spendsync.app.data.repository;

import com.spendsync.app.data.local.db.dao.CategoryDao;
import com.spendsync.app.data.local.db.dao.ExpenseDao;
import com.spendsync.app.data.local.db.dao.PaymentMethodDao;
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
public final class ExpenseRepositoryImpl_Factory implements Factory<ExpenseRepositoryImpl> {
  private final Provider<ExpenseDao> expenseDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<PaymentMethodDao> paymentMethodDaoProvider;

  public ExpenseRepositoryImpl_Factory(Provider<ExpenseDao> expenseDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<PaymentMethodDao> paymentMethodDaoProvider) {
    this.expenseDaoProvider = expenseDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.paymentMethodDaoProvider = paymentMethodDaoProvider;
  }

  @Override
  public ExpenseRepositoryImpl get() {
    return newInstance(expenseDaoProvider.get(), categoryDaoProvider.get(), paymentMethodDaoProvider.get());
  }

  public static ExpenseRepositoryImpl_Factory create(
      javax.inject.Provider<ExpenseDao> expenseDaoProvider,
      javax.inject.Provider<CategoryDao> categoryDaoProvider,
      javax.inject.Provider<PaymentMethodDao> paymentMethodDaoProvider) {
    return new ExpenseRepositoryImpl_Factory(Providers.asDaggerProvider(expenseDaoProvider), Providers.asDaggerProvider(categoryDaoProvider), Providers.asDaggerProvider(paymentMethodDaoProvider));
  }

  public static ExpenseRepositoryImpl_Factory create(Provider<ExpenseDao> expenseDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<PaymentMethodDao> paymentMethodDaoProvider) {
    return new ExpenseRepositoryImpl_Factory(expenseDaoProvider, categoryDaoProvider, paymentMethodDaoProvider);
  }

  public static ExpenseRepositoryImpl newInstance(ExpenseDao expenseDao, CategoryDao categoryDao,
      PaymentMethodDao paymentMethodDao) {
    return new ExpenseRepositoryImpl(expenseDao, categoryDao, paymentMethodDao);
  }
}
