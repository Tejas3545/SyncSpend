-keepnames class com.spendsync.app.data.remote.notion.models.NotionQueryResponse
-if class com.spendsync.app.data.remote.notion.models.NotionQueryResponse
-keep class com.spendsync.app.data.remote.notion.models.NotionQueryResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.spendsync.app.data.remote.notion.models.NotionQueryResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.spendsync.app.data.remote.notion.models.NotionQueryResponse {
    public synthetic <init>(java.util.List,boolean,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
