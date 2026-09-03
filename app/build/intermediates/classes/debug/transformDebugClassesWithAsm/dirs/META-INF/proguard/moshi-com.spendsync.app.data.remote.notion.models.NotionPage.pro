-keepnames class com.spendsync.app.data.remote.notion.models.NotionPage
-if class com.spendsync.app.data.remote.notion.models.NotionPage
-keep class com.spendsync.app.data.remote.notion.models.NotionPageJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.spendsync.app.data.remote.notion.models.NotionPage
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.spendsync.app.data.remote.notion.models.NotionPage {
    public synthetic <init>(java.lang.String,java.util.Map,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
