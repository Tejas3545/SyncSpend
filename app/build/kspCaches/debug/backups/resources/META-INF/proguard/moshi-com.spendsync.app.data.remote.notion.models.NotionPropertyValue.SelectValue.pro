-keepnames class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$SelectValue
-if class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$SelectValue
-keep class com.spendsync.app.data.remote.notion.models.NotionPropertyValue_SelectValueJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
