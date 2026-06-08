package com.spendsync.app.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = NotionSyncWorker.class
)
public interface NotionSyncWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.spendsync.app.worker.NotionSyncWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(NotionSyncWorker_AssistedFactory factory);
}
