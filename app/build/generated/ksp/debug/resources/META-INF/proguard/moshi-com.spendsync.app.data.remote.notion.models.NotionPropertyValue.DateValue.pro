-keepnames class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$DateValue
-if class com.spendsync.app.data.remote.notion.models.NotionPropertyValue$DateValue
-keep class com.spendsync.app.data.remote.notion.models.NotionPropertyValue_DateValueJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
