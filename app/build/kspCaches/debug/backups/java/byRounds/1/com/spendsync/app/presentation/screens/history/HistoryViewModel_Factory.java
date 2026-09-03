package com.spendsync.app.presentation.screens.history;

import com.spendsync.app.domain.usecase.DeleteExpenseUseCase;
import com.spendsync.app.domain.usecase.GetExpensesUseCase;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider;

  public HistoryViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.deleteExpenseUseCaseProvider = deleteExpenseUseCaseProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get(), deleteExpenseUseCaseProvider.get());
  }

  public static HistoryViewModel_Factory create(
      javax.inject.Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      javax.inject.Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    return new HistoryViewModel_Factory(Providers.asDaggerProvider(getExpensesUseCaseProvider), Providers.asDaggerProvider(deleteExpenseUseCaseProvider));
  }

  public static HistoryViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    return new HistoryViewModel_Factory(getExpensesUseCaseProvider, deleteExpenseUseCaseProvider);
  }

  public static HistoryViewModel newInstance(GetExpensesUseCase getExpensesUseCase,
      DeleteExpenseUseCase deleteExpenseUseCase) {
    return new HistoryViewModel(getExpensesUseCase, deleteExpenseUseCase);
  }
}
