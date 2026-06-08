package com.spendsync.app.di;

import com.spendsync.app.data.remote.notion.NotionInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<NotionInterceptor> interceptorProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<NotionInterceptor> interceptorProvider) {
    this.interceptorProvider = interceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(interceptorProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      javax.inject.Provider<NotionInterceptor> interceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(Providers.asDaggerProvider(interceptorProvider));
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<NotionInterceptor> interceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(interceptorProvider);
  }

  public static OkHttpClient provideOkHttpClient(NotionInterceptor interceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(interceptor));
  }
}
