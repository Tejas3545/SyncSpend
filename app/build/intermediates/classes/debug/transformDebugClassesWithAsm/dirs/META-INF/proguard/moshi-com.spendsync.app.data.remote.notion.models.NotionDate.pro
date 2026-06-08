-keepnames class com.spendsync.app.data.remote.notion.models.NotionDate
-if class com.spendsync.app.data.remote.notion.models.NotionDate
-keep class com.spendsync.app.data.remote.notion.models.NotionDateJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
