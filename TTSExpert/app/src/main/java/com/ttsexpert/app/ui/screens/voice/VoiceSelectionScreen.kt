package com.ttsexpert.app.ui.screens.voice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttsexpert.app.data.model.VoiceInfo
import com.ttsexpert.app.ui.components.VoiceItem

/**
 * Voice selection screen - second step in the onboarding flow
 */
@Composable
fun VoiceSelectionScreen(
    availableVoices: List<VoiceInfo>,
    selectedVoice: VoiceInfo?,
    onVoiceSelected: (VoiceInfo) -> Unit,
    onDemoClick: (VoiceInfo) -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Voice",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Choose a voice for text-to-speech. Tap 'Demo' to hear a sample.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (availableVoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading voices...")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(availableVoices) { voice ->
                        VoiceItem(
                            voiceName = voice.name,
                            language = voice.language,
                            genderSymbol = voice.getGenderSymbol(),
                            isSelected = voice == selectedVoice,
                            onClick = { onVoiceSelected(voice) },
                            onDemoClick = { onDemoClick(voice) }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                if (selectedVoice != null) {
                    showDialog = true
                }
            },
            enabled = selectedVoice != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue to Main Screen")
        }
    }
    
    // Confirmation dialog before proceeding to main screen
    if (showDialog && selectedVoice != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Voice Selection") },
            text = { 
                Column {
                    Text("You selected:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedVoice.name} (${selectedVoice.language})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (selectedVoice.getGenderSymbol().isNotEmpty()) {
                        Text(
                            text = "Gender: ${selectedVoice.getGenderSymbol()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onContinueClick()
                    }
                ) {
                    Text("Yes, Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
