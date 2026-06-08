-keepnames class com.spendsync.app.data.remote.notion.models.NotionDatabase
-if class com.spendsync.app.data.remote.notion.models.NotionDatabase
-keep class com.spendsync.app.data.remote.notion.models.NotionDatabaseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
