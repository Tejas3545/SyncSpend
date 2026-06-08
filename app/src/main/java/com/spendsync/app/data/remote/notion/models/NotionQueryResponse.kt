package com.spendsync.app.data.remote.notion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotionQueryResponse(
    @Json(name = "results") val results: List<NotionPage>,
    @Json(name = "has_more") val hasMore: Boolean
)
