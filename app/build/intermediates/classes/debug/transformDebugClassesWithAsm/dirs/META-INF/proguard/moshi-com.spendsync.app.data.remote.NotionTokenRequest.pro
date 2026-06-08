-keepnames class com.spendsync.app.data.remote.NotionTokenRequest
-if class com.spendsync.app.data.remote.NotionTokenRequest
-keep class com.spendsync.app.data.remote.NotionTokenRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
