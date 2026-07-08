package com.ttsexpert.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable slider component with label
 */
@Composable
fun LabeledSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$label: ${String.format("%.2f", value)}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Voice item component displaying voice info with gender symbol
 */
@Composable
fun VoiceItem(
    voiceName: String,
    language: String,
    genderSymbol: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDemoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = voiceName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    if (genderSymbol.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = genderSymbol,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = language,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            OutlinedButton(
                onClick = onDemoClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = "Demo")
            }
        }
    }
}

/**
 * Playback control buttons component
 */
@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onPlayClick,
            enabled = !isPlaying
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                contentDescription = "Play"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Play")
        }
        
        Button(
            onClick = onPauseClick,
            enabled = isPlaying
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Pause,
                contentDescription = "Pause"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pause")
        }
        
        Button(
            onClick = onStopClick,
            enabled = isPlaying
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Stop,
                contentDescription = "Stop"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Stop")
        }
    }
}
