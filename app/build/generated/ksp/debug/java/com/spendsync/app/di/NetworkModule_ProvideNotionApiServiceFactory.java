package com.spendsync.app.di;

import com.spendsync.app.data.remote.notion.NotionApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideNotionApiServiceFactory implements Factory<NotionApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideNotionApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public NotionApiService get() {
    return provideNotionApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideNotionApiServiceFactory create(
      javax.inject.Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideNotionApiServiceFactory(Providers.asDaggerProvider(retrofitProvider));
  }

  public static NetworkModule_ProvideNotionApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideNotionApiServiceFactory(retrofitProvider);
  }

  public static NotionApiService provideNotionApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideNotionApiService(retrofit));
  }
}
