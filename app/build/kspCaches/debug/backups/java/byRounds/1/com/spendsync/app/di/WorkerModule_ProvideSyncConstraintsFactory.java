package com.spendsync.app.di;

import androidx.work.Constraints;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class WorkerModule_ProvideSyncConstraintsFactory implements Factory<Constraints> {
  @Override
  public Constraints get() {
    return provideSyncConstraints();
  }

  public static WorkerModule_ProvideSyncConstraintsFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Constraints provideSyncConstraints() {
    return Preconditions.checkNotNullFromProvides(WorkerModule.INSTANCE.provideSyncConstraints());
  }

  private static final class InstanceHolder {
    static final WorkerModule_ProvideSyncConstraintsFactory INSTANCE = new WorkerModule_ProvideSyncConstraintsFactory();
  }
}
