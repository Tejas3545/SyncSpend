-keepnames class com.spendsync.app.data.remote.notion.models.NotionPropertyValue
-if class com.spendsync.app.data.remote.notion.models.NotionPropertyValue
-keep class com.spendsync.app.data.remote.notion.models.NotionPropertyValueJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.spendsync.app.data.remote.notion.models.NotionPropertyValue
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.spendsync.app.data.remote.notion.models.NotionPropertyValue {
    public synthetic <init>(java.util.List,java.lang.Double,com.spendsync.app.data.remote.notion.models.NotionSelectOption,com.spendsync.app.data.remote.notion.models.NotionDate,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
