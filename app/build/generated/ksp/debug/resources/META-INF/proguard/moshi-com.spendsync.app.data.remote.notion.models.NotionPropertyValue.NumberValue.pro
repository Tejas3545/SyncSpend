-keepnames class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$NumberValue
-if class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$NumberValue
-keep class com.spendsync.app.data.remote.notion.models.NotionPropertyValue_NumberValueJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
