package com.ttsexpert.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttsexpert.app.ui.screens.language.LanguageSelectionScreen
import com.ttsexpert.app.ui.screens.main.MainTTSScreen
import com.ttsexpert.app.ui.screens.voice.VoiceSelectionScreen
import com.ttsexpert.app.ui.theme.TTSExpertTheme
import com.ttsexpert.app.ui.viewmodel.TTSViewModel

/**
 * Main Activity for TTS Expert App
 * Implements a multi-step onboarding flow:
 * 1. Language Selection
 * 2. Voice Selection  
 * 3. Main TTS Screen with playback controls and settings
 */
class MainActivity : ComponentActivity() {
    
    // Track the current screen in the onboarding flow
    private var currentScreen = OnboardingScreen.LANGUAGE_SELECTION
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            TTSExpertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: TTSViewModel = viewModel()
                    
                    TTSExpertApp(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * Enum representing the different screens in the app
 */
enum class OnboardingScreen {
    LANGUAGE_SELECTION,
    VOICE_SELECTION,
    MAIN_TTS
}
