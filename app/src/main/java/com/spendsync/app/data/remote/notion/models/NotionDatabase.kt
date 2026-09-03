package com.spendsync.app.data.remote.notion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotionDatabase(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: List<NotionRichText>
)
