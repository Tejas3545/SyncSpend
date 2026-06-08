package com.spendsync.app.domain.usecase;

import com.spendsync.app.domain.repository.ExpenseRepository;
import com.spendsync.app.domain.repository.NotionRepository;
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
public final class DeleteExpenseUseCase_Factory implements Factory<DeleteExpenseUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<NotionRepository> notionRepositoryProvider;

  public DeleteExpenseUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<NotionRepository> notionRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.notionRepositoryProvider = notionRepositoryProvider;
  }

  @Override
  public DeleteExpenseUseCase get() {
    return newInstance(expenseRepositoryProvider.get(), notionRepositoryProvider.get());
  }

  public static DeleteExpenseUseCase_Factory create(
      javax.inject.Provider<ExpenseRepository> expenseRepositoryProvider,
      javax.inject.Provider<NotionRepository> notionRepositoryProvider) {
    return new DeleteExpenseUseCase_Factory(Providers.asDaggerProvider(expenseRepositoryProvider), Providers.asDaggerProvider(notionRepositoryProvider));
  }

  public static DeleteExpenseUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<NotionRepository> notionRepositoryProvider) {
    return new DeleteExpenseUseCase_Factory(expenseRepositoryProvider, notionRepositoryProvider);
  }

  public static DeleteExpenseUseCase newInstance(ExpenseRepository expenseRepository,
      NotionRepository notionRepository) {
    return new DeleteExpenseUseCase(expenseRepository, notionRepository);
  }
}
