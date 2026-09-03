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

    /**
     * Fill this with the public OAuth client id from Notion's integration settings.
     * The app intentionally does not store a Notion client secret; token exchange should be
     * performed by a secure backend that redirects back to NOTION_REDIRECT_URI with an access_token.
     * 
     * However, to achieve a true "Serverless" Sign-in with Notion on Android, we can embed
     * the client_secret here to exchange the token on the device.
     */
    const val NOTION_OAUTH_CLIENT_ID = "377d872b-594c-815e-a900-00377ac783bb"
    const val NOTION_OAUTH_CLIENT_SECRET = ""
    const val NOTION_REDIRECT_URI = "https://syncspend/oauth"
}
