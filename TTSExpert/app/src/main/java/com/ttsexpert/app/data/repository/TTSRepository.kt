package com.ttsexpert.app.data.repository

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.ttsexpert.app.data.model.TTSSettings
import com.ttsexpert.app.data.model.VoiceGender
import com.ttsexpert.app.data.model.VoiceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileWriter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing TTS functionality
 */
@Singleton
class TTSRepository @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "TTSRepository"
    }
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()
    
    private val _availableVoices = MutableStateFlow<List<VoiceInfo>>(emptyList())
    val availableVoices: StateFlow<List<VoiceInfo>> = _availableVoices.asStateFlow()
    
    private val _currentSettings = MutableStateFlow(TTSSettings())
    val currentSettings: StateFlow<TTSSettings> = _currentSettings.asStateFlow()
    
    private var textToSpeech: TextToSpeech? = null
    private var utteranceId: String? = null
    
    /**
     * Initialize the TTS engine
     */
    fun initializeTTS(onInitListener: (Boolean) -> Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _isInitialized.value = true
                loadAvailableVoices()
                onInitListener(true)
            } else {
                _isInitialized.value = false
                onInitListener(false)
            }
        }
        
        setupUtteranceProgressListener()
    }
    
    /**
     * Setup utterance progress listener to track speech completion
     */
    private fun setupUtteranceProgressListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                this@TTSRepository.utteranceId = utteranceId
            }
            
            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }
            
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                Log.e(TAG, "TTS Error for utterance: $utteranceId")
            }
        })
    }
    
    /**
     * Load all available voices from the TTS engine
     */
    private fun loadAvailableVoices() {
        val voices = textToSpeech?.voices?.map { voice ->
            VoiceInfo(
                name = voice.name,
                language = voice.locale?.displayName ?: "Unknown",
                quality = voice.quality,
                isNetworkConnectionRequired = voice.isNetworkConnectionRequired,
                isDefault = false,
                gender = determineVoiceGender(voice)
            )
        } ?: emptyList()
        
        // Set default voice
        val defaultVoice = textToSpeech?.voice
        val updatedVoices = voices.map { voice ->
            if (voice.name == defaultVoice?.name) {
                voice.copy(isDefault = true)
            } else {
                voice
            }
        }
        
        _availableVoices.value = updatedVoices
        
        // Update current settings with default voice
        defaultVoice?.let {
            _currentSettings.value = _currentSettings.value.copy(
                selectedVoice = VoiceInfo(
                    name = it.name,
                    language = it.locale?.displayName ?: "Unknown",
                    quality = it.quality,
                    isNetworkConnectionRequired = it.isNetworkConnectionRequired,
                    isDefault = true,
                    gender = determineVoiceGender(it)
                )
            )
        }
    }
    
    /**
     * Attempt to determine the gender of a voice based on its name
     */
    private fun determineVoiceGender(voice: Voice): VoiceGender {
        val voiceName = voice.name.lowercase()
        val locale = voice.locale
        
        // Check for common gender indicators in voice names
        return when {
            voiceName.contains("female") || voiceName.contains("woman") || 
            voiceName.contains("girl") || voiceName.contains("f ") ||
            voiceName.contains("_f") -> VoiceGender.FEMALE
            
            voiceName.contains("male") || voiceName.contains("man") || 
            voiceName.contains("boy") || voiceName.contains("m ") ||
            voiceName.contains("_m") -> VoiceGender.MALE
            
            // Try to infer from locale and common voice patterns
            locale != null -> inferGenderFromLocale(locale, voiceName)
            
            else -> VoiceGender.UNKNOWN
        }
    }
    
    /**
     * Infer gender from locale and voice name patterns
     */
    private fun inferGenderFromLocale(locale: Locale, voiceName: String): VoiceGender {
        // Common patterns for different TTS engines
        val femalePatterns = listOf("zira", "hazel", "susan", "linda", "karen", "samantha")
        val malePatterns = listOf("david", "james", "george", "mark", "steven", "daniel")
        
        return when {
            femalePatterns.any { voiceName.contains(it) } -> VoiceGender.FEMALE
            malePatterns.any { voiceName.contains(it) } -> VoiceGender.MALE
            else -> VoiceGender.UNKNOWN
        }
    }
    
    /**
     * Get available languages
     */
    fun getAvailableLanguages(): List<Locale> {
        return textToSpeech?.availableLanguages?.toList() ?: emptyList()
    }
    
    /**
     * Set the selected voice
     */
    fun setSelectedVoice(voiceInfo: VoiceInfo): Boolean {
        val voice = textToSpeech?.voices?.find { it.name == voiceInfo.name }
        return if (voice != null) {
            val result = textToSpeech?.setVoice(voice)
            val success = result == TextToSpeech.SUCCESS
            if (success) {
                _currentSettings.value = _currentSettings.value.copy(selectedVoice = voiceInfo)
            }
            success
        } else {
            false
        }
    }
    
    /**
     * Set pitch (0.0 to 2.0)
     */
    fun setPitch(pitch: Float) {
        val clampedPitch = pitch.coerceIn(0.0f, 2.0f)
        textToSpeech?.setPitch(clampedPitch)
        _currentSettings.value = _currentSettings.value.copy(pitch = clampedPitch)
    }
    
    /**
     * Set speech rate (0.0 to 3.0)
     */
    fun setSpeechRate(rate: Float) {
        val clampedRate = rate.coerceIn(0.0f, 3.0f)
        textToSpeech?.setSpeechRate(clampedRate)
        _currentSettings.value = _currentSettings.value.copy(speechRate = clampedRate)
    }
    
    /**
     * Set volume (0.0 to 1.0)
     */
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        _currentSettings.value = _currentSettings.value.copy(volume = clampedVolume)
        // Volume is handled by the system audio stream
    }
    
    /**
     * Speak text using TTS
     */
    fun speak(text: String): Boolean {
        if (!_isInitialized.value || textToSpeech == null) {
            return false
        }
        
        val bundle = android.os.Bundle()
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_PITCH, _currentSettings.value.pitch)
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_RATE, _currentSettings.value.speechRate)
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, _currentSettings.value.volume)
        
        val utteranceId = UUID.randomUUID().toString()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, utteranceId)
        
        return true
    }
    
    /**
     * Stop speaking
     */
    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }
    
    /**
     * Play demo for a specific voice
     */
    fun playVoiceDemo(voiceInfo: VoiceInfo): Boolean {
        val success = setSelectedVoice(voiceInfo)
        if (success) {
            val demoText = when {
                voiceInfo.gender == VoiceGender.FEMALE -> "Hello, I am a female voice."
                voiceInfo.gender == VoiceGender.MALE -> "Hello, I am a male voice."
                else -> "Hello, this is a voice demo."
            }
            speak(demoText)
        }
        return success
    }
    
    /**
     * Export speech to audio file
     * Note: Android TTS doesn't directly support audio file export.
     * This is a placeholder that would require additional implementation
     * such as recording the audio output or using a server-side TTS API.
     */
    fun exportToAudioFile(text: String, fileName: String): File? {
        try {
            val audioDir = File(context.getExternalFilesDir(null), "AudioExports")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            
            val outputFile = File(audioDir, "$fileName.wav")
            
            // Write metadata about the TTS settings to a companion file
            val metadataFile = File(audioDir, "$fileName.txt")
            FileWriter(metadataFile).use { writer ->
                writer.appendLine("Text: $text")
                writer.appendLine("Voice: ${_currentSettings.value.selectedVoice?.name ?: "Unknown"}")
                writer.appendLine("Language: ${_currentSettings.value.selectedVoice?.language ?: "Unknown"}")
                writer.appendLine("Pitch: ${_currentSettings.value.pitch}")
                writer.appendLine("Speed: ${_currentSettings.value.speechRate}")
                writer.appendLine("Volume: ${_currentSettings.value.volume}")
                writer.appendLine("Generated: ${java.time.LocalDateTime.now()}")
            }
            
            // Note: Actual audio synthesis to file would require:
            // 1. Using AudioRecord to capture the TTS output, OR
            // 2. Using a cloud TTS API that supports direct file generation, OR
            // 3. Using a library like marytts or similar
            
            return outputFile
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting audio file", e)
            return null
        }
    }
    
    /**
     * Shutdown TTS engine
     */
    fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        _isInitialized.value = false
    }
}
