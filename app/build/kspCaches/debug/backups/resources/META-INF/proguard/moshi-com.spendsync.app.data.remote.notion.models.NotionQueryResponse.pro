-keepnames class com.spendsync.app.data.remote.notion.models.NotionQueryResponse
-if class com.spendsync.app.data.remote.notion.models.NotionQueryResponse
-keep class com.spendsync.app.data.remote.notion.models.NotionQueryResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
