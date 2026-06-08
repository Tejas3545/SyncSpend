-keepnames class com.spendsync.app.data.remote.notion.models.NotionText
-if class com.spendsync.app.data.remote.notion.models.NotionText
-keep class com.spendsync.app.data.remote.notion.models.NotionTextJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
