package com.spendsync.app.presentation.screens.addexpense;

import androidx.work.WorkManager;
import com.spendsync.app.data.local.db.dao.PaymentMethodDao;
import com.spendsync.app.domain.repository.CategoryRepository;
import com.spendsync.app.domain.usecase.AddExpenseUseCase;
import com.spendsync.app.domain.usecase.GetSuggestionsUseCase;
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
public final class AddExpenseViewModel_Factory implements Factory<AddExpenseViewModel> {
  private final Provider<AddExpenseUseCase> addExpenseUseCaseProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<GetSuggestionsUseCase> getSuggestionsUseCaseProvider;

  private final Provider<PaymentMethodDao> paymentMethodDaoProvider;

  private final Provider<WorkManager> workManagerProvider;

  public AddExpenseViewModel_Factory(Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<GetSuggestionsUseCase> getSuggestionsUseCaseProvider,
      Provider<PaymentMethodDao> paymentMethodDaoProvider,
      Provider<WorkManager> workManagerProvider) {
    this.addExpenseUseCaseProvider = addExpenseUseCaseProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.getSuggestionsUseCaseProvider = getSuggestionsUseCaseProvider;
    this.paymentMethodDaoProvider = paymentMethodDaoProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public AddExpenseViewModel get() {
    return newInstance(addExpenseUseCaseProvider.get(), categoryRepositoryProvider.get(), getSuggestionsUseCaseProvider.get(), paymentMethodDaoProvider.get(), workManagerProvider.get());
  }

  public static AddExpenseViewModel_Factory create(
      javax.inject.Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      javax.inject.Provider<CategoryRepository> categoryRepositoryProvider,
      javax.inject.Provider<GetSuggestionsUseCase> getSuggestionsUseCaseProvider,
      javax.inject.Provider<PaymentMethodDao> paymentMethodDaoProvider,
      javax.inject.Provider<WorkManager> workManagerProvider) {
    return new AddExpenseViewModel_Factory(Providers.asDaggerProvider(addExpenseUseCaseProvider), Providers.asDaggerProvider(categoryRepositoryProvider), Providers.asDaggerProvider(getSuggestionsUseCaseProvider), Providers.asDaggerProvider(paymentMethodDaoProvider), Providers.asDaggerProvider(workManagerProvider));
  }

  public static AddExpenseViewModel_Factory create(
      Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<GetSuggestionsUseCase> getSuggestionsUseCaseProvider,
      Provider<PaymentMethodDao> paymentMethodDaoProvider,
      Provider<WorkManager> workManagerProvider) {
    return new AddExpenseViewModel_Factory(addExpenseUseCaseProvider, categoryRepositoryProvider, getSuggestionsUseCaseProvider, paymentMethodDaoProvider, workManagerProvider);
  }

  public static AddExpenseViewModel newInstance(AddExpenseUseCase addExpenseUseCase,
      CategoryRepository categoryRepository, GetSuggestionsUseCase getSuggestionsUseCase,
      PaymentMethodDao paymentMethodDao, WorkManager workManager) {
    return new AddExpenseViewModel(addExpenseUseCase, categoryRepository, getSuggestionsUseCase, paymentMethodDao, workManager);
  }
}
