package com.spendsync.app

object AppConfig {
    // DEVELOPER: Set your Notion integration details here
//    const val NOTION_TOKEN = ""
    const val NOTION_DATABASE_ID = "377be280858c80d2943ec7ac257ce280"
    const val NOTION_API_VERSION = "2022-06-28"
    const val NOTION_BASE_URL = "https://api.notion.com/v1/"
    const val NOTION_DATABASE_URL = "${NOTION_BASE_URL}databases/$NOTION_DATABASE_ID/query"

    // OAuth configuration
    const val NOTION_CLIENT_ID = "377d872b-594c-815e-a900-00377ac783bb"
    const val NOTION_CLIENT_SECRET = "secret_zzbGaRrTVJ5obVMaLZEKE7gKXdbEy3DJPddHdcQ3MSk"
    const val NOTION_REDIRECT_URI = "https://syncspend/oauth"
    const val GOOGLE_CLIENT_ID = "545311358294-0ujf8r8mgsipc8qoh45pj0infn63695c.apps.googleusercontent.com"
}
