package com.ttsexpert.app.data.model

/**
 * Represents a TTS voice with its properties
 */
data class VoiceInfo(
    val name: String,
    val language: String,
    val quality: Int,
    val isNetworkConnectionRequired: Boolean,
    val isDefault: Boolean,
    val gender: VoiceGender = VoiceGender.UNKNOWN
) {
    /**
     * Returns the display name for this voice
     */
    fun getDisplayName(): String {
        return "$name ($language)"
    }
    
    /**
     * Returns the gender symbol for display
     */
    fun getGenderSymbol(): String {
        return when (gender) {
            VoiceGender.MALE -> "♂"
            VoiceGender.FEMALE -> "♀"
            VoiceGender.UNKNOWN -> ""
        }
    }
}

/**
 * Enum representing the gender of a voice
 */
enum class VoiceGender {
    MALE,
    FEMALE,
    UNKNOWN
}

/**
 * Represents playback state for the audio player
 */
data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
)

/**
 * Represents TTS settings
 */
data class TTSSettings(
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val volume: Float = 1.0f,
    val selectedVoice: VoiceInfo? = null
)
