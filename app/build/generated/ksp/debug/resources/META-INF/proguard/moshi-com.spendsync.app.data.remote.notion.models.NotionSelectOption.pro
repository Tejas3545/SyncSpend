-keepnames class com.spendsync.app.data.remote.notion.models.NotionSelectOption
-if class com.spendsync.app.data.remote.notion.models.NotionSelectOption
-keep class com.spendsync.app.data.remote.notion.models.NotionSelectOptionJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
