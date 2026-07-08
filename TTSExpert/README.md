# TTS Expert - Text to Speech Audio Application

A modern Android application built with Jetpack Compose that provides comprehensive text-to-speech functionality with multiple voice options, playback controls, and audio export capabilities.

## Features

### 🎯 Core Functionality
- **Multi-step Onboarding Flow**
  - Step 1: Language Selection - Choose from all available system languages
  - Step 2: Voice Selection - Browse and select from all available TTS voices
  - Step 3: Main TTS Screen - Full-featured text-to-speech interface

### 🎤 Voice Features
- **All TTS Engines Supported**
  - Device default TTS engines
  - Google Text-to-Speech
  - Open source TTS engines (when installed)
  
- **Voice Information Display**
  - Gender symbols (♂ Male, ♀ Female)
  - Language identification
  - Voice quality indicators
  - Network requirement status
  
- **Voice Demo Playback**
  - Tap "Demo" button to hear voice samples
  - Automatic gender-specific demo text

### 🎛️ Playback Controls
- **Play/Pause/Stop Buttons**
  - Play: Start speech synthesis
  - Pause: Pause current playback
  - Stop: Stop and reset playback
  
- **Seekbar Controls**
  - Pitch control (0.0 - 2.0)
  - Speech rate/speed control (0.0 - 3.0)
  - Volume control (0.0 - 1.0)

### 💾 Export Functionality
- **Audio Export**
  - Export button triggers file naming dialog
  - Custom file name input before saving
  - Saves to device's AudioExports folder
  - Includes metadata file with TTS settings

### 🎨 Modern UI/UX
- **Jetpack Compose**
  - Material Design 3 components
  - Smooth animations and transitions
  - Responsive layouts
  
- **Theme Support**
  - Light and dark theme support
  - Dynamic color schemes
  - Edge-to-edge display

## Technical Specifications

### Requirements
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

### Dependencies (Latest 2026 Versions)
```kotlin
// Kotlin
kotlin = "2.1.0"

// Android Gradle Plugin
agp = "8.7.0"

// AndroidX Libraries
core-ktx = "1.15.0"
lifecycle-runtime-ktx = "2.8.7"
activity-compose = "1.9.3"
compose-bom = "2024.12.01"
navigation-compose = "2.8.5"
material3 = "1.3.1"

// Java 8+ API desugaring
desugarJdkLibs = "2.1.3"
```

### Architecture
- **MVVM Pattern** (Model-View-ViewModel)
- **Repository Pattern** for data management
- **StateFlow** for reactive state management
- **Clean Architecture** principles

### Project Structure
```
TTSExpert/
├── app/
│   ├── src/main/
│   │   ├── java/com/ttsexpert/app/
│   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   │   └── VoiceInfo.kt
│   │   │   │   └── repository/
│   │   │   │       └── TTSRepository.kt
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   └── CommonComponents.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── language/
│   │   │   │   │   │   └── LanguageSelectionScreen.kt
│   │   │   │   │   ├── voice/
│   │   │   │   │   │   └── VoiceSelectionScreen.kt
│   │   │   │   │   └── main/
│   │   │   │   │       └── MainTTSScreen.kt
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   └── viewmodel/
│   │   │   │       └── TTSViewModel.kt
│   │   │   ├── MainActivity.kt
│   │   │   └── TTSExpertApp.kt
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── xml/
│   │   │   │   └── file_paths.xml
│   │   │   └── mipmap-*/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   ├── wrapper/
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## Setup Instructions

### Prerequisites
1. Android Studio Hedgehog (2023.1.1) or later
2. JDK 17 or later
3. Android SDK with API level 35

### Installation Steps
1. Clone or copy the project to your workspace
2. Open the project in Android Studio
3. Sync Gradle files (File > Sync Project with Gradle Files)
4. Build the project (Build > Make Project)
5. Run on an emulator or physical device

### Building from Command Line
```bash
cd TTSExpert
./gradlew assembleDebug
```

### Running Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Usage Guide

### First Launch
1. **Language Selection**: Choose your preferred language from the list
2. **Voice Selection**: Browse available voices, tap "Demo" to hear samples
3. **Main Screen**: Access full TTS functionality

### Using the Main Screen
1. **Enter Text**: Type or paste text in the input field
2. **Adjust Settings**: Use sliders to modify pitch, speed, and volume
3. **Playback**: Use Play/Pause/Stop buttons to control speech
4. **Export**: Tap "Export Audio", enter filename, and save

### Voice Demo
- Each voice displays gender symbol (♂ or ♀)
- Tap "Demo" button to hear a sample
- Demo text varies based on detected gender

## Permissions

The app requests the following permissions:
- `WRITE_EXTERNAL_STORAGE` (Android 9 and below): For exporting audio files
- `READ_EXTERNAL_STORAGE` (Android 12 and below): For accessing storage
- `READ_MEDIA_AUDIO` (Android 13+): For accessing audio files

## Notes

### TTS Engine Compatibility
- The app uses Android's built-in TextToSpeech API
- Available voices depend on installed TTS engines
- Google TTS is recommended for best voice selection

### Audio Export Limitation
- Android's native TTS API doesn't directly support file export
- The current implementation creates metadata files
- For actual audio synthesis, consider:
  - Using AudioRecord to capture output
  - Integrating cloud TTS APIs (Google Cloud TTS, AWS Polly, etc.)
  - Using third-party TTS libraries

### Gender Detection
- Voice gender is inferred from voice names
- Common patterns are used for detection
- Some voices may show as "Unknown" gender

## Troubleshooting

### No Voices Available
1. Ensure a TTS engine is installed (e.g., Google TTS)
2. Go to Settings > Accessibility > Text-to-speech output
3. Select and configure your preferred engine

### Export Not Working
1. Grant storage permissions when prompted
2. Check available storage space
3. Verify the app has permission in system settings

### TTS Not Initializing
1. Restart the app
2. Check if TTS service is enabled in system settings
3. Try installing/updating Google TTS

## License

This project is provided as-is for educational purposes.

## Support

For issues or feature requests, please check:
- Android Developer Documentation: https://developer.android.com/guide/topics/text-to-speech
- Material Design Guidelines: https://material.io/design

---

**Built with ❤️ using Jetpack Compose and Kotlin**
