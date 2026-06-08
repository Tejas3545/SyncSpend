-keepnames class com.spendsync.app.data.remote.notion.models.NotionParent
-if class com.spendsync.app.data.remote.notion.models.NotionParent
-keep class com.spendsync.app.data.remote.notion.models.NotionParentJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.spendsync.app.data.remote.notion.models.NotionParent
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.spendsync.app.data.remote.notion.models.NotionParent {
    public synthetic <init>(java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
