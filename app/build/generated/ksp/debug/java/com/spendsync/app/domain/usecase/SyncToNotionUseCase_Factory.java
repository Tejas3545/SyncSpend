package com.spendsync.app.domain.usecase;

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
public final class SyncToNotionUseCase_Factory implements Factory<SyncToNotionUseCase> {
  private final Provider<NotionRepository> notionRepositoryProvider;

  public SyncToNotionUseCase_Factory(Provider<NotionRepository> notionRepositoryProvider) {
    this.notionRepositoryProvider = notionRepositoryProvider;
  }

  @Override
  public SyncToNotionUseCase get() {
    return newInstance(notionRepositoryProvider.get());
  }

  public static SyncToNotionUseCase_Factory create(
      javax.inject.Provider<NotionRepository> notionRepositoryProvider) {
    return new SyncToNotionUseCase_Factory(Providers.asDaggerProvider(notionRepositoryProvider));
  }

  public static SyncToNotionUseCase_Factory create(
      Provider<NotionRepository> notionRepositoryProvider) {
    return new SyncToNotionUseCase_Factory(notionRepositoryProvider);
  }

  public static SyncToNotionUseCase newInstance(NotionRepository notionRepository) {
    return new SyncToNotionUseCase(notionRepository);
  }
}
