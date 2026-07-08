package com.ttsexpert.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ttsexpert.app.data.model.TTSSettings
import com.ttsexpert.app.data.model.VoiceInfo
import com.ttsexpert.app.data.repository.TTSRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

/**
 * ViewModel for managing TTS functionality across screens
 */
class TTSViewModel(application: Application) : AndroidViewModel(application) {
    
    private val ttsRepository = TTSRepository(application)
    
    // UI State
    data class UiState(
        val isInitialized: Boolean = false,
        val isPlaying: Boolean = false,
        val availableLanguages: List<Locale> = emptyList(),
        val availableVoices: List<VoiceInfo> = emptyList(),
        val selectedLanguage: Locale? = null,
        val selectedVoice: VoiceInfo? = null,
        val currentSettings: TTSSettings = TTSSettings(),
        val currentText: String = "",
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        initializeTTS()
    }
    
    /**
     * Initialize the TTS engine
     */
    private fun initializeTTS() {
        ttsRepository.initializeTTS { success ->
            viewModelScope.launch {
                if (success) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isInitialized = true,
                            isLoading = false,
                            availableVoices = ttsRepository.availableVoices.value,
                            currentSettings = ttsRepository.currentSettings.value
                        )
                    }
                    
                    // Load available languages
                    loadAvailableLanguages()
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isInitialized = false,
                            isLoading = false,
                            errorMessage = "Failed to initialize TTS engine"
                        )
                    }
                }
            }
        }
        
        // Collect repository state changes
        viewModelScope.launch {
            ttsRepository.isInitialized.collect { isInit ->
                _uiState.update { it.copy(isInitialized = isInit) }
            }
        }
        
        viewModelScope.launch {
            ttsRepository.isSpeaking.collect { isSpeaking ->
                _uiState.update { it.copy(isPlaying = isSpeaking) }
            }
        }
        
        viewModelScope.launch {
            ttsRepository.availableVoices.collect { voices ->
                _uiState.update { it.copy(availableVoices = voices) }
            }
        }
        
        viewModelScope.launch {
            ttsRepository.currentSettings.collect { settings ->
                _uiState.update { 
                    it.copy(
                        currentSettings = settings,
                        selectedVoice = settings.selectedVoice
                    ) 
                }
            }
        }
    }
    
    /**
     * Load available languages from TTS engine
     */
    private fun loadAvailableLanguages() {
        val languages = ttsRepository.getAvailableLanguages()
        _uiState.update { it.copy(availableLanguages = languages) }
    }
    
    /**
     * Select a language
     */
    fun selectLanguage(language: Locale) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }
    
    /**
     * Select a voice
     */
    fun selectVoice(voice: VoiceInfo) {
        viewModelScope.launch {
            val success = ttsRepository.setSelectedVoice(voice)
            if (!success) {
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = "Failed to select voice")
                }
            }
        }
    }
    
    /**
     * Play demo for a specific voice
     */
    fun playVoiceDemo(voice: VoiceInfo) {
        viewModelScope.launch {
            ttsRepository.playVoiceDemo(voice)
        }
    }
    
    /**
     * Update text input
     */
    fun updateText(text: String) {
        _uiState.update { it.copy(currentText = text) }
    }
    
    /**
     * Play/speak the current text
     */
    fun play() {
        val text = _uiState.value.currentText
        if (text.isNotEmpty()) {
            viewModelScope.launch {
                ttsRepository.speak(text)
            }
        }
    }
    
    /**
     * Pause speech (note: Android TTS doesn't have native pause, so we stop)
     */
    fun pause() {
        viewModelScope.launch {
            ttsRepository.stop()
        }
    }
    
    /**
     * Stop speech
     */
    fun stop() {
        viewModelScope.launch {
            ttsRepository.stop()
        }
    }
    
    /**
     * Update pitch setting
     */
    fun setPitch(pitch: Float) {
        ttsRepository.setPitch(pitch)
    }
    
    /**
     * Update speech rate setting
     */
    fun setSpeechRate(rate: Float) {
        ttsRepository.setSpeechRate(rate)
    }
    
    /**
     * Update volume setting
     */
    fun setVolume(volume: Float) {
        ttsRepository.setVolume(volume)
    }
    
    /**
     * Export audio to file
     */
    fun exportAudio(fileName: String): File? {
        val text = _uiState.value.currentText
        if (text.isEmpty()) {
            return null
        }
        return ttsRepository.exportToAudioFile(text, fileName)
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsRepository.shutdown()
    }
}
