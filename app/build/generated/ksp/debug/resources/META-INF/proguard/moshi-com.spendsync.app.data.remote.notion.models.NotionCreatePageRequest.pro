-keepnames class com.spendsync.app.data.remote.notion.models.NotionCreatePageRequest
-if class com.spendsync.app.data.remote.notion.models.NotionCreatePageRequest
-keep class com.spendsync.app.data.remote.notion.models.NotionCreatePageRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
