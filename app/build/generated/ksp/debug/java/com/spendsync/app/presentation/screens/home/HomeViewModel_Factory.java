package com.spendsync.app.presentation.screens.home;

import com.spendsync.app.domain.usecase.DeleteExpenseUseCase;
import com.spendsync.app.domain.usecase.GetExpensesUseCase;
import com.spendsync.app.domain.usecase.GetSpendingSummaryUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<GetSpendingSummaryUseCase> getSpendingSummaryUseCaseProvider;

  private final Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider;

  public HomeViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<GetSpendingSummaryUseCase> getSpendingSummaryUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.getSpendingSummaryUseCaseProvider = getSpendingSummaryUseCaseProvider;
    this.deleteExpenseUseCaseProvider = deleteExpenseUseCaseProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get(), getSpendingSummaryUseCaseProvider.get(), deleteExpenseUseCaseProvider.get());
  }

  public static HomeViewModel_Factory create(
      javax.inject.Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      javax.inject.Provider<GetSpendingSummaryUseCase> getSpendingSummaryUseCaseProvider,
      javax.inject.Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    return new HomeViewModel_Factory(Providers.asDaggerProvider(getExpensesUseCaseProvider), Providers.asDaggerProvider(getSpendingSummaryUseCaseProvider), Providers.asDaggerProvider(deleteExpenseUseCaseProvider));
  }

  public static HomeViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<GetSpendingSummaryUseCase> getSpendingSummaryUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    return new HomeViewModel_Factory(getExpensesUseCaseProvider, getSpendingSummaryUseCaseProvider, deleteExpenseUseCaseProvider);
  }

  public static HomeViewModel newInstance(GetExpensesUseCase getExpensesUseCase,
      GetSpendingSummaryUseCase getSpendingSummaryUseCase,
      DeleteExpenseUseCase deleteExpenseUseCase) {
    return new HomeViewModel(getExpensesUseCase, getSpendingSummaryUseCase, deleteExpenseUseCase);
  }
}
