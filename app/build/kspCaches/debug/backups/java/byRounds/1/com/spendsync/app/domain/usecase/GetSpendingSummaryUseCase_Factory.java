package com.spendsync.app.domain.usecase;

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
public final class GetSpendingSummaryUseCase_Factory implements Factory<GetSpendingSummaryUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public GetSpendingSummaryUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public GetSpendingSummaryUseCase get() {
    return newInstance(expenseRepositoryProvider.get());
  }

  public static GetSpendingSummaryUseCase_Factory create(
      javax.inject.Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GetSpendingSummaryUseCase_Factory(Providers.asDaggerProvider(expenseRepositoryProvider));
  }

  public static GetSpendingSummaryUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GetSpendingSummaryUseCase_Factory(expenseRepositoryProvider);
  }

  public static GetSpendingSummaryUseCase newInstance(ExpenseRepository expenseRepository) {
    return new GetSpendingSummaryUseCase(expenseRepository);
  }
}
