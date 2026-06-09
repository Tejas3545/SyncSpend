package com.spendsync.app

/**
 * Public, non-secret API configuration.
 *
 * Never place OAuth client secrets, integration tokens, or user credentials in this object. Android
 * applications can be reverse engineered, so values compiled into the APK must be treated as public.
 */
object AppConfig {
    const val NOTION_API_VERSION = "2022-06-28"
    const val NOTION_BASE_URL = "https://api.notion.com/v1/"

}
