package com.ttsexpert.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttsexpert.app.ui.screens.language.LanguageSelectionScreen
import com.ttsexpert.app.ui.screens.main.MainTTSScreen
import com.ttsexpert.app.ui.screens.voice.VoiceSelectionScreen
import com.ttsexpert.app.ui.viewmodel.TTSViewModel

/**
 * Main composable that manages navigation between screens
 */
@Composable
fun TTSExpertApp(
    viewModel: TTSViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Track current screen state
    var currentScreen by remember { mutableStateOf(OnboardingScreen.LANGUAGE_SELECTION) }
    
    // Show error snackbar if there's an error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
    
    // Handle loading state
    if (uiState.isLoading) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    // Navigate through screens based on state
    when (currentScreen) {
        OnboardingScreen.LANGUAGE_SELECTION -> {
            LanguageSelectionScreen(
                availableLanguages = uiState.availableLanguages,
                selectedLanguage = uiState.selectedLanguage,
                onLanguageSelected = { language ->
                    viewModel.selectLanguage(language)
                },
                onContinueClick = {
                    currentScreen = OnboardingScreen.VOICE_SELECTION
                }
            )
        }
        
        OnboardingScreen.VOICE_SELECTION -> {
            VoiceSelectionScreen(
                availableVoices = uiState.availableVoices,
                selectedVoice = uiState.selectedVoice,
                onVoiceSelected = { voice ->
                    viewModel.selectVoice(voice)
                },
                onDemoClick = { voice ->
                    viewModel.playVoiceDemo(voice)
                },
                onContinueClick = {
                    currentScreen = OnboardingScreen.MAIN_TTS
                }
            )
        }
        
        OnboardingScreen.MAIN_TTS -> {
            MainTTSScreen(
                isPlaying = uiState.isPlaying,
                currentSettings = uiState.currentSettings,
                onTextChange = { text ->
                    viewModel.updateText(text)
                },
                onPlayClick = {
                    viewModel.play()
                },
                onPauseClick = {
                    viewModel.pause()
                },
                onStopClick = {
                    viewModel.stop()
                },
                onExportClick = {
                    // This will be handled by the dialog in MainTTSScreen
                    // The actual export happens after filename is provided
                },
                onPitchChange = { pitch ->
                    viewModel.setPitch(pitch)
                },
                onSpeedChange = { speed ->
                    viewModel.setSpeechRate(speed)
                },
                onVolumeChange = { volume ->
                    viewModel.setVolume(volume)
                }
            )
        }
    }
}
