package com.spendsync.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.spendsync.app.data.local.datastore.AuthDataStore;
import com.spendsync.app.data.local.datastore.SettingsDataStore;
import com.spendsync.app.data.local.db.SyncSpendDatabase;
import com.spendsync.app.data.local.db.dao.CategoryDao;
import com.spendsync.app.data.local.db.dao.ExpenseDao;
import com.spendsync.app.data.local.db.dao.PaymentMethodDao;
import com.spendsync.app.data.remote.notion.NotionApiService;
import com.spendsync.app.data.remote.notion.NotionInterceptor;
import com.spendsync.app.data.repository.AuthRepositoryImpl;
import com.spendsync.app.data.repository.CategoryRepositoryImpl;
import com.spendsync.app.data.repository.ExpenseRepositoryImpl;
import com.spendsync.app.data.repository.GoogleSheetsRepositoryImpl;
import com.spendsync.app.data.repository.NotionRepositoryImpl;
import com.spendsync.app.di.DatabaseModule_ProvideCategoryDaoFactory;
import com.spendsync.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.spendsync.app.di.DatabaseModule_ProvideExpenseDaoFactory;
import com.spendsync.app.di.DatabaseModule_ProvidePaymentMethodDaoFactory;
import com.spendsync.app.di.NetworkModule_ProvideNotionApiServiceFactory;
import com.spendsync.app.di.NetworkModule_ProvideNotionInterceptorFactory;
import com.spendsync.app.di.NetworkModule_ProvideOkHttpClientFactory;
import com.spendsync.app.di.NetworkModule_ProvideRetrofitFactory;
import com.spendsync.app.di.WorkerModule_ProvideWorkManagerFactory;
import com.spendsync.app.domain.repository.CategoryRepository;
import com.spendsync.app.domain.repository.ExpenseRepository;
import com.spendsync.app.domain.repository.NotionRepository;
import com.spendsync.app.domain.usecase.AddExpenseUseCase;
import com.spendsync.app.domain.usecase.DeleteExpenseUseCase;
import com.spendsync.app.domain.usecase.GetExpensesUseCase;
import com.spendsync.app.domain.usecase.GetSpendingSummaryUseCase;
import com.spendsync.app.domain.usecase.GetSuggestionsUseCase;
import com.spendsync.app.presentation.screens.addexpense.AddExpenseViewModel;
import com.spendsync.app.presentation.screens.addexpense.AddExpenseViewModel_HiltModules;
import com.spendsync.app.presentation.screens.addexpense.AddExpenseViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.spendsync.app.presentation.screens.addexpense.AddExpenseViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.spendsync.app.presentation.screens.addexpense.QuickAddActivity;
import com.spendsync.app.presentation.screens.history.HistoryViewModel;
import com.spendsync.app.presentation.screens.history.HistoryViewModel_HiltModules;
import com.spendsync.app.presentation.screens.history.HistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.spendsync.app.presentation.screens.history.HistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.spendsync.app.presentation.screens.home.HomeViewModel;
import com.spendsync.app.presentation.screens.home.HomeViewModel_HiltModules;
import com.spendsync.app.presentation.screens.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.spendsync.app.presentation.screens.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.spendsync.app.presentation.screens.login.LoginViewModel;
import com.spendsync.app.presentation.screens.login.LoginViewModel_HiltModules;
import com.spendsync.app.presentation.screens.login.LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.spendsync.app.presentation.screens.login.LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.spendsync.app.presentation.screens.settings.SettingsViewModel;
import com.spendsync.app.presentation.screens.settings.SettingsViewModel_HiltModules;
import com.spendsync.app.presentation.screens.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.spendsync.app.presentation.screens.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.spendsync.app.worker.GoogleSyncWorker;
import com.spendsync.app.worker.GoogleSyncWorker_AssistedFactory;
import com.spendsync.app.worker.NotionSyncWorker;
import com.spendsync.app.worker.NotionSyncWorker_AssistedFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerSyncSpendApp_HiltComponents_SingletonC {
  private DaggerSyncSpendApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SyncSpendApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SyncSpendApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SyncSpendApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SyncSpendApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SyncSpendApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SyncSpendApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SyncSpendApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SyncSpendApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SyncSpendApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SyncSpendApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SyncSpendApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SyncSpendApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SyncSpendApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public void injectQuickAddActivity(QuickAddActivity quickAddActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>of(AddExpenseViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AddExpenseViewModel_HiltModules.KeyModule.provide(), HistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HistoryViewModel_HiltModules.KeyModule.provide(), HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide(), LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LoginViewModel_HiltModules.KeyModule.provide(), SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()));
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSettingsDataStore(instance, singletonCImpl.settingsDataStoreProvider.get());
      MainActivity_MembersInjector.injectAuthDataStore(instance, singletonCImpl.authDataStoreProvider.get());
      MainActivity_MembersInjector.injectAuthRepository(instance, singletonCImpl.authRepositoryImplProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends SyncSpendApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddExpenseViewModel> addExpenseViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private AddExpenseUseCase addExpenseUseCase() {
      return new AddExpenseUseCase(singletonCImpl.bindExpenseRepositoryProvider.get());
    }

    private GetSuggestionsUseCase getSuggestionsUseCase() {
      return new GetSuggestionsUseCase(singletonCImpl.bindExpenseRepositoryProvider.get());
    }

    private GetExpensesUseCase getExpensesUseCase() {
      return new GetExpensesUseCase(singletonCImpl.bindExpenseRepositoryProvider.get());
    }

    private DeleteExpenseUseCase deleteExpenseUseCase() {
      return new DeleteExpenseUseCase(singletonCImpl.bindExpenseRepositoryProvider.get(), singletonCImpl.bindNotionRepositoryProvider.get());
    }

    private GetSpendingSummaryUseCase getSpendingSummaryUseCase() {
      return new GetSpendingSummaryUseCase(singletonCImpl.bindExpenseRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addExpenseViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>of(AddExpenseViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) addExpenseViewModelProvider), HistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) historyViewModelProvider), HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) homeViewModelProvider), LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) loginViewModelProvider), SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)));
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.spendsync.app.presentation.screens.addexpense.AddExpenseViewModel 
          return (T) new AddExpenseViewModel(viewModelCImpl.addExpenseUseCase(), singletonCImpl.bindCategoryRepositoryProvider.get(), viewModelCImpl.getSuggestionsUseCase(), singletonCImpl.paymentMethodDao(), singletonCImpl.provideWorkManagerProvider.get());

          case 1: // com.spendsync.app.presentation.screens.history.HistoryViewModel 
          return (T) new HistoryViewModel(viewModelCImpl.getExpensesUseCase(), viewModelCImpl.deleteExpenseUseCase());

          case 2: // com.spendsync.app.presentation.screens.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.getExpensesUseCase(), viewModelCImpl.getSpendingSummaryUseCase(), viewModelCImpl.deleteExpenseUseCase());

          case 3: // com.spendsync.app.presentation.screens.login.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.authRepositoryImplProvider.get(), singletonCImpl.authDataStoreProvider.get());

          case 4: // com.spendsync.app.presentation.screens.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.settingsDataStoreProvider.get(), singletonCImpl.authDataStoreProvider.get(), singletonCImpl.bindNotionRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SyncSpendApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SyncSpendApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends SyncSpendApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AuthDataStore> authDataStoreProvider;

    private Provider<SyncSpendDatabase> provideDatabaseProvider;

    private Provider<ExpenseRepositoryImpl> expenseRepositoryImplProvider;

    private Provider<ExpenseRepository> bindExpenseRepositoryProvider;

    private Provider<GoogleSheetsRepositoryImpl> googleSheetsRepositoryImplProvider;

    private Provider<GoogleSyncWorker_AssistedFactory> googleSyncWorker_AssistedFactoryProvider;

    private Provider<NotionInterceptor> provideNotionInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<NotionApiService> provideNotionApiServiceProvider;

    private Provider<NotionRepositoryImpl> notionRepositoryImplProvider;

    private Provider<NotionRepository> bindNotionRepositoryProvider;

    private Provider<NotionSyncWorker_AssistedFactory> notionSyncWorker_AssistedFactoryProvider;

    private Provider<SettingsDataStore> settingsDataStoreProvider;

    private Provider<AuthRepositoryImpl> authRepositoryImplProvider;

    private Provider<CategoryRepositoryImpl> categoryRepositoryImplProvider;

    private Provider<CategoryRepository> bindCategoryRepositoryProvider;

    private Provider<WorkManager> provideWorkManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private ExpenseDao expenseDao() {
      return DatabaseModule_ProvideExpenseDaoFactory.provideExpenseDao(provideDatabaseProvider.get());
    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideDatabaseProvider.get());
    }

    private PaymentMethodDao paymentMethodDao() {
      return DatabaseModule_ProvidePaymentMethodDaoFactory.providePaymentMethodDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.spendsync.app.worker.GoogleSyncWorker", ((Provider) googleSyncWorker_AssistedFactoryProvider), "com.spendsync.app.worker.NotionSyncWorker", ((Provider) notionSyncWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.authDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<AuthDataStore>(singletonCImpl, 2));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<SyncSpendDatabase>(singletonCImpl, 4));
      this.expenseRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 3);
      this.bindExpenseRepositoryProvider = DoubleCheck.provider((Provider) expenseRepositoryImplProvider);
      this.googleSheetsRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<GoogleSheetsRepositoryImpl>(singletonCImpl, 1));
      this.googleSyncWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<GoogleSyncWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideNotionInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<NotionInterceptor>(singletonCImpl, 10));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 9));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 8));
      this.provideNotionApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<NotionApiService>(singletonCImpl, 7));
      this.notionRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.bindNotionRepositoryProvider = DoubleCheck.provider((Provider) notionRepositoryImplProvider);
      this.notionSyncWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<NotionSyncWorker_AssistedFactory>(singletonCImpl, 5));
      this.settingsDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<SettingsDataStore>(singletonCImpl, 11));
      this.authRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepositoryImpl>(singletonCImpl, 12));
      this.categoryRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.bindCategoryRepositoryProvider = DoubleCheck.provider((Provider) categoryRepositoryImplProvider);
      this.provideWorkManagerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManager>(singletonCImpl, 14));
    }

    @Override
    public void injectSyncSpendApp(SyncSpendApp syncSpendApp) {
      injectSyncSpendApp2(syncSpendApp);
    }

    @Override
    public ExpenseRepository expenseRepository() {
      return bindExpenseRepositoryProvider.get();
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private SyncSpendApp injectSyncSpendApp2(SyncSpendApp instance) {
      SyncSpendApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.spendsync.app.worker.GoogleSyncWorker_AssistedFactory 
          return (T) new GoogleSyncWorker_AssistedFactory() {
            @Override
            public GoogleSyncWorker create(Context context, WorkerParameters params) {
              return new GoogleSyncWorker(context, params, singletonCImpl.googleSheetsRepositoryImplProvider.get());
            }
          };

          case 1: // com.spendsync.app.data.repository.GoogleSheetsRepositoryImpl 
          return (T) new GoogleSheetsRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.authDataStoreProvider.get(), singletonCImpl.bindExpenseRepositoryProvider.get());

          case 2: // com.spendsync.app.data.local.datastore.AuthDataStore 
          return (T) new AuthDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.spendsync.app.data.repository.ExpenseRepositoryImpl 
          return (T) new ExpenseRepositoryImpl(singletonCImpl.expenseDao(), singletonCImpl.categoryDao(), singletonCImpl.paymentMethodDao());

          case 4: // com.spendsync.app.data.local.db.SyncSpendDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.spendsync.app.worker.NotionSyncWorker_AssistedFactory 
          return (T) new NotionSyncWorker_AssistedFactory() {
            @Override
            public NotionSyncWorker create(Context context2, WorkerParameters params2) {
              return new NotionSyncWorker(context2, params2, singletonCImpl.bindExpenseRepositoryProvider.get(), singletonCImpl.bindNotionRepositoryProvider.get());
            }
          };

          case 6: // com.spendsync.app.data.repository.NotionRepositoryImpl 
          return (T) new NotionRepositoryImpl(singletonCImpl.provideNotionApiServiceProvider.get(), singletonCImpl.bindExpenseRepositoryProvider.get(), singletonCImpl.authDataStoreProvider.get());

          case 7: // com.spendsync.app.data.remote.notion.NotionApiService 
          return (T) NetworkModule_ProvideNotionApiServiceFactory.provideNotionApiService(singletonCImpl.provideRetrofitProvider.get());

          case 8: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 9: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.provideNotionInterceptorProvider.get());

          case 10: // com.spendsync.app.data.remote.notion.NotionInterceptor 
          return (T) NetworkModule_ProvideNotionInterceptorFactory.provideNotionInterceptor(singletonCImpl.authDataStoreProvider.get());

          case 11: // com.spendsync.app.data.local.datastore.SettingsDataStore 
          return (T) new SettingsDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // com.spendsync.app.data.repository.AuthRepositoryImpl 
          return (T) new AuthRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.authDataStoreProvider.get());

          case 13: // com.spendsync.app.data.repository.CategoryRepositoryImpl 
          return (T) new CategoryRepositoryImpl(singletonCImpl.categoryDao());

          case 14: // androidx.work.WorkManager 
          return (T) WorkerModule_ProvideWorkManagerFactory.provideWorkManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
