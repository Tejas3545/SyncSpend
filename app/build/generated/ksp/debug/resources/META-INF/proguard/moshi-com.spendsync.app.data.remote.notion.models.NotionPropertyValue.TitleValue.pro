-keepnames class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$TitleValue
-if class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$TitleValue
-keep class com.spendsync.app.data.remote.notion.models.NotionPropertyValue_TitleValueJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
