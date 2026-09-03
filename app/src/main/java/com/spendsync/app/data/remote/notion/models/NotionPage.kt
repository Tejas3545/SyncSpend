package com.spendsync.app.data.remote.notion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotionPage(
    @Json(name = "id") val id: String,
    @Json(name = "object") val objectType: String? = null,
    @Json(name = "properties") val properties: Map<String, NotionPropertyValue> = emptyMap()
)
