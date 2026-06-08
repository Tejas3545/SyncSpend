package com.spendsync.app.data.remote.notion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotionCreatePageRequest(
    @Json(name = "parent") val parent: NotionParent,
    @Json(name = "properties") val properties: Map<String, NotionPropertyValue>
)

@JsonClass(generateAdapter = true)
data class NotionParent(
    @Json(name = "type") val type: String = "database_id",
    @Json(name = "database_id") val database_id: String
)
