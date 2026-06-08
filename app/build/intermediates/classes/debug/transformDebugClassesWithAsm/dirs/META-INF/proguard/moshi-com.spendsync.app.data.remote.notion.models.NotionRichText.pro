-keepnames class com.spendsync.app.data.remote.notion.models.NotionRichText
-if class com.spendsync.app.data.remote.notion.models.NotionRichText
-keep class com.spendsync.app.data.remote.notion.models.NotionRichTextJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
