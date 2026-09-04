package com.novachat.app.domain.model

/**
 * Represents the type of a chat message, driving bubble rendering strategy
 * and content-specific UI composables.
 */
enum class MessageType {
    /** Plain UTF-8 text content */
    TEXT,

    /** Compressed image uploaded to Firebase Storage */
    IMAGE,

    /** M4A voice note with pre-computed amplitude array for waveform rendering */
    VOICE_NOTE,

    /** Generic file attachment (PDF, DOC, ZIP, etc.) */
    FILE,

    /** Latitude/longitude coordinate pair */
    LOCATION,

    /** Ephemeral typing presence indicator — never persisted */
    TYPING_INDICATOR
}
