package com.ttsexpert.app.ui.screens.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ttsexpert.app.data.model.TTSSettings
import com.ttsexpert.app.data.model.VoiceInfo
import com.ttsexpert.app.ui.components.LabeledSlider
import com.ttsexpert.app.ui.components.PlaybackControls
import java.io.File

/**
 * Main TTS screen with text input, playback controls, and settings
 */
@Composable
fun MainTTSScreen(
    isPlaying: Boolean,
    currentSettings: TTSSettings,
    onTextChange: (String) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onExportClick: () -> Unit,
    onPitchChange: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    var showFileNameDialog by remember { mutableStateOf(false) }
    var fileNameInput by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    
    // Permission launcher for exporting audio files
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showFileNameDialog = true
        } else {
            // Show permission denied message
            // In a real app, you'd show a Snackbar or dialog here
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Enter Text",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { 
                        textInput = it
                        onTextChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeholder = { Text("Type or paste your text here...") },
                    maxLines = Int.MAX_VALUE
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Playback Controls
        PlaybackControls(
            isPlaying = isPlaying,
            onPlayClick = {
                if (textInput.isNotEmpty()) {
                    onPlayClick()
                }
            },
            onPauseClick = onPauseClick,
            onStopClick = onStopClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Export Button
        Button(
            onClick = {
                // Request permission based on Android version
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        // No permission needed for scoped storage
                        showFileNameDialog = true
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Save,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Audio")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Voice Settings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Pitch Control (0.0 to 2.0)
                LabeledSlider(
                    label = "Pitch",
                    value = currentSettings.pitch,
                    onValueChange = onPitchChange,
                    valueRange = 0.0f..2.0f,
                    steps = 19
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Speed Control (0.0 to 3.0)
                LabeledSlider(
                    label = "Speed",
                    value = currentSettings.speechRate,
                    onValueChange = onSpeedChange,
                    valueRange = 0.0f..3.0f,
                    steps = 29
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Volume Control (0.0 to 1.0)
                LabeledSlider(
                    label = "Volume",
                    value = currentSettings.volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0.0f..1.0f,
                    steps = 19
                )
            }
        }
        
        // Current Voice Info
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Voice",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currentSettings.selectedVoice?.name ?: "Not selected",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                currentSettings.selectedVoice?.getGenderSymbol()?.let { symbol ->
                    if (symbol.isNotEmpty()) {
                        Text(
                            text = symbol,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
    
    // File Name Dialog for Export
    if (showFileNameDialog) {
        AlertDialog(
            onDismissRequest = { showFileNameDialog = false },
            title = { Text("Save Audio File") },
            text = {
                Column {
                    Text("Enter a name for your audio file:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = fileNameInput,
                        onValueChange = { fileNameInput = it },
                        placeholder = { Text("e.g., my_audio_file") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (fileNameInput.isNotBlank()) {
                            onExportClick()
                            showFileNameDialog = false
                            fileNameInput = ""
                        }
                    },
                    enabled = fileNameInput.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showFileNameDialog = false
                    fileNameInput = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
