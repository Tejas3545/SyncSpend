-keepnames class com.spendsync.app.data.remote.notion.models.NotionPage
-if class com.spendsync.app.data.remote.notion.models.NotionPage
-keep class com.spendsync.app.data.remote.notion.models.NotionPageJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
