package com.spendsync.app.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotionTokenRequest(
    val grant_type: String,
    val code: String,
    val redirect_uri: String
)
