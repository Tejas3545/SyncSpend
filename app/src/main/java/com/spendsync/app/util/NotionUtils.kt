package com.spendsync.app.util

object NotionUtils {
    /**
     * Extracts clean 32-character hex Notion database ID from raw ID, UUID with hyphens, or full Notion URL.
     */
    fun extractDatabaseId(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        // If it's a URL, extract path segment
        val withoutQuery = trimmed.substringBefore("?").substringBefore("#")
        val pathSegment = withoutQuery.substringAfterLast("/")

        // Check if there is an 8-4-4-4-12 UUID in the string
        val uuidRegex = Regex("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        val uuidMatch = uuidRegex.find(pathSegment)
        if (uuidMatch != null) {
            return uuidMatch.value.replace("-", "").lowercase()
        }

        // Check if there is a 32-character hex string
        val hex32Regex = Regex("(?i)[0-9a-f]{32}")
        val hexMatch = hex32Regex.find(pathSegment)
        if (hexMatch != null) {
            return hexMatch.value.lowercase()
        }

        // Fallback: strip hyphens and check
        val stripped = pathSegment.replace("-", "")
        if (stripped.length >= 32) {
            return stripped.takeLast(32).lowercase()
        }
        return trimmed.replace("-", "").lowercase()
    }
}
