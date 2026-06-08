package com.spendsync.app.data.remote.notion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotionPropertyValue(
    @Json(name = "title") val title: List<NotionRichText>? = null,
    @Json(name = "number") val number: Double? = null,
    @Json(name = "select") val select: NotionSelectOption? = null,
    @Json(name = "date") val date: NotionDate? = null
) {
    @JsonClass(generateAdapter = true)
    data class TitleValue(
        @Json(name = "title") val title: List<NotionRichText>
    )

    @JsonClass(generateAdapter = true)
    data class NumberValue(
        @Json(name = "number") val number: Double
    )

    @JsonClass(generateAdapter = true)
    data class SelectValue(
        @Json(name = "select") val select: NotionSelectOption
    )

    @JsonClass(generateAdapter = true)
    data class DateValue(
        @Json(name = "date") val date: NotionDate
    )
}

@JsonClass(generateAdapter = true)
data class NotionRichText(
    @Json(name = "text") val text: NotionText
)

@JsonClass(generateAdapter = true)
data class NotionText(
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class NotionSelectOption(
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class NotionDate(
    @Json(name = "start") val start: String
)
