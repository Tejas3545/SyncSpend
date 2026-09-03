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
public final class GetSuggestionsUseCase_Factory implements Factory<GetSuggestionsUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public GetSuggestionsUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public GetSuggestionsUseCase get() {
    return newInstance(expenseRepositoryProvider.get());
  }

  public static GetSuggestionsUseCase_Factory create(
      javax.inject.Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GetSuggestionsUseCase_Factory(Providers.asDaggerProvider(expenseRepositoryProvider));
  }

  public static GetSuggestionsUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GetSuggestionsUseCase_Factory(expenseRepositoryProvider);
  }

  public static GetSuggestionsUseCase newInstance(ExpenseRepository expenseRepository) {
    return new GetSuggestionsUseCase(expenseRepository);
  }
}
